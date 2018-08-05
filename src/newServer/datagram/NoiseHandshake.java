package newServer.datagram;

public class NoiseHandshake {
	private byte[] payload;
	
	public NoiseHandshake(byte[] payload) {
		System.arraycopy(payload, 0, this.payload, 0, payload.length);
	}

	public byte[] getPayload() {
		return payload;
	}
}
