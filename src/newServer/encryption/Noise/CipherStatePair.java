package newServer.encryption.Noise;

public class CipherStatePair {

	private CipherState sender;
	private CipherState receiver;

	public CipherStatePair(CipherState sender, CipherState receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}

	public CipherState getSender() {
		return sender;
	}

	public CipherState getReceiver() {
		return receiver;
	}

}
