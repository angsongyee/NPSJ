package newServer.encryption.Noise;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;

public class SymmetricState {
	private String protocolName;
	private CipherState cipher;
	private MessageDigest hash;
	private byte[] ck;
	private byte[] h;
	private byte[] prev_h;

	public SymmetricState(String protocolName) throws NoSuchAlgorithmException {
		this.protocolName = protocolName;
		String[] names = protocolName.split("_");
		cipher = Noise.getCipher(names[3]);
		hash = Noise.getHash(names[4]);

		int hashLength = hash.getDigestLength();
		ck = new byte[hashLength];
		h = new byte[hashLength];
		prev_h = new byte[hashLength];

		byte[] protocolNameBytes = protocolName.getBytes();

		if (protocolNameBytes.length <= hashLength) {
			System.arraycopy(protocolNameBytes, 0, h, 0, protocolNameBytes.length);
			Arrays.fill(h, protocolNameBytes.length, h.length, (byte) 0);
		} else {
			hashOne(protocolNameBytes, 0, protocolNameBytes.length, h, 0, h.length);
		}

		System.arraycopy(h, 0, ck, 0, hashLength);

	}

	public void mixKey(byte[] key) {
		byte[] tempKey = new byte[cipher.getKeyLength()];
		hkdf(ck, 0, ck.length, key, 0, key.length, ck, 0, ck.length, tempKey, 0, tempKey.length);
		cipher.initializeKey(tempKey);
	}

	public void mixHash(byte[] data, int offset, int length) {
		hashTwo(h, 0, h.length, data, offset, length, h, 0, h.length);
	}

	public byte[] getHandshakeHash() {
		return h;
	}

	public int encryptAndHash(byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset,
			int length) throws ShortBufferException {
		int ciphertextLength = cipher.encryptWithAd(h, plaintext, plaintextOffset, ciphertext, ciphertextOffset,
				length);
		mixHash(ciphertext, ciphertextOffset, ciphertextLength);
		return ciphertextLength;
	}

	public int decryptAndHash(byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset,
			int length) throws ShortBufferException, BadPaddingException {
		System.arraycopy(h, 0, prev_h, 0, h.length);
		mixHash(ciphertext, ciphertextOffset, length);
		return cipher.decryptWithAd(prev_h, ciphertext, ciphertextOffset, plaintext, plaintextOffset, length);
	}

	public CipherStatePair split() {
		byte[] k1 = new byte[cipher.getKeyLength()];
		byte[] k2 = new byte[cipher.getKeyLength()];
		CipherState c1 = null;
		CipherState c2 = null;
		CipherStatePair pair = null;
		hkdf(ck, 0, ck.length, new byte[0], 0, 0, k1, 0, k1.length, k2, 0, k2.length);

		try {
			String[] names = protocolName.split("_");
			c1 = Noise.getCipher(names[3]);
			c2 = Noise.getCipher(names[3]);
			c1.initializeKey(k1);
			c2.initializeKey(k2);
			pair = new CipherStatePair(c1, c2);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			if (c1 == null || c2 == null || pair == null) {
				if (c1 != null)
					c1 = null;
				if (c2 != null)
					c2 = null;
				pair = null;
			}
		}
		return pair;
	}

	// hash utility functions
	private void hashOne(byte[] data, int offset, int length, byte[] output, int outputOffset, int outputLength) {
		hash.reset();
		hash.update(data, offset, length);
		try {
			hash.digest(output, outputOffset, outputLength);
		} catch (DigestException e) {
			Arrays.fill(output, outputOffset, outputLength, (byte) 0);
		}
	}

	private void hashTwo(byte[] data1, int offset1, int length1, byte[] data2, int offset2, int length2, byte[] output,
			int outputOffset, int outputLength) {
		hash.reset();
		hash.update(data1, offset1, length1);
		hash.update(data2, offset2, length2);
		try {
			hash.digest(output, outputOffset, outputLength);
		} catch (DigestException e) {
			Arrays.fill(output, outputOffset, outputLength, (byte) 0);
		}
	}

	private void hmac(byte[] key, int keyOffset, int keyLength, byte[] data, int dataOffset, int dataLength,
			byte[] output, int outputOffset, int outputLength) {

		int hashLength = hash.getDigestLength();
		int blockLength = hashLength * 2;
		byte[] block = new byte[blockLength];
		int index;
		try {
			if (keyLength <= blockLength) {
				System.arraycopy(key, keyOffset, block, 0, keyLength);
				Arrays.fill(block, keyLength, blockLength, (byte) 0);
			} else {
				hash.reset();
				hash.update(key, keyOffset, keyLength);
				hash.digest(block, 0, hashLength);
				Arrays.fill(block, hashLength, blockLength, (byte) 0);
			}
			for (index = 0; index < blockLength; ++index)
				block[index] ^= (byte) 0x36;
			hash.reset();
			hash.update(block, 0, blockLength);
			hash.update(data, dataOffset, dataLength);
			hash.digest(output, outputOffset, hashLength);
			for (index = 0; index < blockLength; ++index)
				block[index] ^= (byte) (0x36 ^ 0x5C);
			hash.reset();
			hash.update(block, 0, blockLength);
			hash.update(output, outputOffset, hashLength);
			hash.digest(output, outputOffset, outputLength);
		} catch (DigestException e) {
			Arrays.fill(output, outputOffset, outputLength, (byte) 0);
		}
	}

	private void hkdf(byte[] key, int keyOffset, int keyLength, byte[] data, int dataOffset, int dataLength,
			byte[] output1, int output1Offset, int output1Length, byte[] output2, int output2Offset,
			int output2Length) {
		int hashLength = hash.getDigestLength();
		byte[] tempKey = new byte[hashLength];
		byte[] tempHash = new byte[hashLength + 1];
		hmac(key, keyOffset, keyLength, data, dataOffset, dataLength, tempKey, 0, hashLength);
		tempHash[0] = (byte) 0x01;
		hmac(tempKey, 0, hashLength, tempHash, 0, 1, tempHash, 0, hashLength);
		System.arraycopy(tempHash, 0, output1, output1Offset, output1Length);
		tempHash[hashLength] = (byte) 0x02;
		hmac(tempKey, 0, hashLength, tempHash, 0, hashLength + 1, tempHash, 0, hashLength);
		System.arraycopy(tempHash, 0, output2, output2Offset, output2Length);
	}
}
