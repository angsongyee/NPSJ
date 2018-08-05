package newServer.encryption.Noise;

import java.security.SecureRandom;
import java.util.Arrays;

class Curve25519DHState implements DHState {

	private byte[] publicKey;
	private byte[] privateKey;
	private int mode;

	/**
	 * Constructs a new Diffie-Hellman object for Curve25519.
	 */
	public Curve25519DHState() {
		publicKey = new byte[32];
		privateKey = new byte[32];
		mode = 0;
	}

	@Override
	public String getDHName() {
		return "25519";
	}

	@Override
	public int getPublicKeyLength() {
		return 32;
	}

	@Override
	public int getPrivateKeyLength() {
		return 32;
	}

	@Override
	public int getSharedKeyLength() {
		return 32;
	}

	@Override
	public void generateKeyPair() {
		SecureRandom random = new SecureRandom();
		random.nextBytes(privateKey);
		Curve25519.eval(publicKey, 0, privateKey, null);
		mode = 0x03;
	}

	@Override
	public void getPublicKey(byte[] key, int offset) {
		System.arraycopy(publicKey, 0, key, offset, 32);
	}

	@Override
	public void setPublicKey(byte[] key, int offset) {
		System.arraycopy(key, offset, publicKey, 0, 32);
		Arrays.fill(privateKey, (byte) 0);
		mode = 0x01;
	}

	@Override
	public void getPrivateKey(byte[] key, int offset) {
		System.arraycopy(privateKey, 0, key, offset, 32);
	}

	@Override
	public void setPrivateKey(byte[] key, int offset) {
		System.arraycopy(key, offset, privateKey, 0, 32);
		Curve25519.eval(publicKey, 0, privateKey, null);
		mode = 0x03;
	}

	@Override
	public void setToNullPublicKey() {
		Arrays.fill(publicKey, (byte) 0);
		Arrays.fill(privateKey, (byte) 0);
		mode = 0x01;
	}

	@Override
	public boolean hasPublicKey() {
		return (mode & 0x01) != 0;
	}

	@Override
	public boolean hasPrivateKey() {
		return (mode & 0x02) != 0;
	}

	@Override
	public boolean isNullPublicKey() {
		if ((mode & 0x01) == 0)
			return false;
		int temp = 0;
		for (int index = 0; index < 32; ++index)
			temp |= publicKey[index];
		return temp == 0;
	}

	@Override
	public void calculate(byte[] sharedKey, DHState publicDH) {
		if (!(publicDH instanceof Curve25519DHState))
			throw new IllegalArgumentException("Incompatible DH algorithms");
		Curve25519.eval(sharedKey, 0, privateKey, ((Curve25519DHState) publicDH).publicKey);
	}

	@Override
	public void copyFrom(DHState other) {
		if (!(other instanceof Curve25519DHState))
			throw new IllegalStateException("Mismatched DH key objects");
		if (other == this)
			return;
		Curve25519DHState dh = (Curve25519DHState) other;
		System.arraycopy(dh.privateKey, 0, privateKey, 0, 32);
		System.arraycopy(dh.publicKey, 0, publicKey, 0, 32);
		mode = dh.mode;
	}
}
