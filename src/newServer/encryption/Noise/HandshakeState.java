package newServer.encryption.Noise;

import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;

public class HandshakeState {

	private boolean initiator;
	private SymmetricState symState;
	private String[] pattern;
	private int patternPos;
	private byte[] prologue;
	private DHState localStatic;
	private DHState localEphemeral;
	private DHState remoteStatic;
	private DHState remoteEphemeral;
	private State action;

	private enum State {
		NO_ACTION, READ, WRITE, SPLIT, FAILED
	}

	public HandshakeState(String protocolName, boolean initiator, byte[] prologue) throws NoSuchAlgorithmException {
		this.initiator = initiator;
		this.prologue = prologue;
		symState = new SymmetricState(protocolName);
		if (prologue != null)
			symState.mixHash(prologue, 0, prologue.length);

		String[] names = protocolName.split("_");
		pattern = Pattern.getPattern(names[1]);
		patternPos = 0;

		localStatic = Noise.createDH(names[2]);
		localEphemeral = Noise.createDH(names[2]);
		remoteStatic = Noise.createDH(names[2]);
		remoteEphemeral = Noise.createDH(names[2]);

		// remove when static key is used
		localStatic.generateKeyPair();

		if (initiator) {
			action = State.WRITE;
		} else {
			action = State.READ;
		}
	}

	public int writeMessage(byte[] payload, int payloadOffset, byte[] message, int messageOffset, int payloadLength)
			throws ShortBufferException {

		int messagePosn = messageOffset;

		if (messageOffset > message.length) {
			throw new ShortBufferException();
		}

		if (action != State.WRITE)
			throw new IllegalStateException("Handshake not supposed to be in write state");
		for (;;) {
			if (patternPos >= pattern.length) {
				action = State.SPLIT;
				break;
			}

			String messagePattern = pattern[patternPos];
			patternPos++;

			int space = message.length - messagePosn;
			if (space < 0)
				throw new ShortBufferException();

			if (messagePattern.equals("next")) {
				action = State.READ;
				break;
			}
			System.out.println(messagePattern);
			switch (messagePattern) {
			case "e": {
				if (localEphemeral.hasPublicKey())
					throw new IllegalStateException("Ephemeral key already exists.");

				localEphemeral.generateKeyPair();

				int length = localEphemeral.getPublicKeyLength();
				if (space < length)
					throw new ShortBufferException();
				localEphemeral.getPublicKey(message, messagePosn);
				symState.mixHash(message, messagePosn, length);
				messagePosn += length;

				break;
			}
			case "s": {
				if (!localStatic.hasPublicKey())
					throw new IllegalStateException("Local static keypair not initialized.");

				int length = localStatic.getPublicKeyLength();
				if (space < length)
					throw new ShortBufferException();
				localStatic.getPublicKey(message, messagePosn);
				messagePosn += symState.encryptAndHash(message, messagePosn, message, messagePosn, length);
				break;
			}
			case "ee": {
				mixDH(localEphemeral, remoteEphemeral);
				break;
			}
			case "es": {
				if (initiator) {
					mixDH(localEphemeral, remoteStatic);
				} else {
					mixDH(localStatic, remoteEphemeral);
				}
				break;
			}
			case "se": {
				if (initiator) {
					mixDH(localStatic, remoteEphemeral);
				} else {
					mixDH(localEphemeral, remoteStatic);
				}
				break;
			}
			case "ss": {
				mixDH(localStatic, remoteStatic);
				break;
			}

			}
		}
		if (payload.length > 0) {
			messagePosn += symState.encryptAndHash(payload, payloadOffset, message, messagePosn, payloadLength);
		} else {
			messagePosn += symState.encryptAndHash(message, messagePosn, message, messagePosn, 0);
		}
		return messagePosn;
	}

	public int readMessage(byte[] message, int messageOffset, byte[] payload, int payloadOffset, int messageLength)
			throws ShortBufferException, BadPaddingException {

		if (action != State.READ)
			throw new IllegalStateException("Handshake not supposed to be in read state");

		int messageEnd = messageOffset + messageLength;

		if (messageEnd > message.length)
			throw new ShortBufferException();

		for (;;) {
			if (patternPos >= pattern.length) {
				action = State.SPLIT;
				break;
			}

			String messagePattern = pattern[patternPos];
			patternPos++;
			if (messagePattern.equals("next")) {
				action = State.WRITE;
				break;
			}
			System.out.println(messagePattern);
			switch (messagePattern) {
			case "e": {
				if (remoteEphemeral.hasPublicKey())
					throw new IllegalStateException("Ephemeral key already exists.");

				int keyLen = remoteEphemeral.getPublicKeyLength();
				if (message.length < keyLen)
					throw new ShortBufferException();

				remoteEphemeral.setPublicKey(message, messageOffset);

				symState.mixHash(message, messageOffset, keyLen);
				messageOffset += keyLen;
				break;
			}
			case "s": {
				if (remoteStatic.hasPublicKey())
					throw new IllegalStateException("Static key already exists.");

				int keyLen = remoteStatic.getPublicKeyLength();
				byte[] temp = new byte[keyLen];
				symState.decryptAndHash(message, messageOffset, temp, 0, keyLen + 16);
				remoteStatic.setPublicKey(temp, 0);
				messageOffset += keyLen + 16;
				break;
			}
			case "ee": {
				mixDH(localEphemeral, remoteEphemeral);
				break;
			}
			case "es": {
				if (initiator) {
					mixDH(localEphemeral, remoteStatic);
				} else {
					mixDH(localStatic, remoteEphemeral);
				}
				break;
			}
			case "se": {
				if (initiator) {
					mixDH(localStatic, remoteEphemeral);
				} else {
					mixDH(localEphemeral, remoteStatic);
				}
				break;
			}
			case "ss": {
				mixDH(localStatic, remoteStatic);
				break;
			}
			}
		}
		int payloadLength = symState.decryptAndHash(message, messageOffset, payload, payloadOffset,
				messageEnd - messageOffset);
		return payloadLength;
	}

	public CipherStatePair split() {
		if (action != State.SPLIT)
			throw new IllegalStateException("Handshake not in Split mode");
		return symState.split();
	}

	public State getNextAction() {
		return action;
	}

	private void mixDH(DHState DH1, DHState DH2) {
		byte[] sharedKey = new byte[DH1.getSharedKeyLength()];
		DH1.calculate(sharedKey, DH2);
		symState.mixKey(sharedKey);
	}
}
