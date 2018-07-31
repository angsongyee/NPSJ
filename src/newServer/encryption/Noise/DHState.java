package newServer.encryption.Noise;

public interface DHState {

	String getDHName();

	int getPublicKeyLength();

	int getPrivateKeyLength();

	int getSharedKeyLength();

	void generateKeyPair();

	void getPublicKey(byte[] key, int offset);

	void setPublicKey(byte[] key, int offset);

	void getPrivateKey(byte[] key, int offset);

	void setPrivateKey(byte[] key, int offset);

	void setToNullPublicKey();

	boolean hasPublicKey();

	boolean hasPrivateKey();

	boolean isNullPublicKey();

	void calculate(byte[] sharedKey, DHState publicDH);

	void copyFrom(DHState other);
}