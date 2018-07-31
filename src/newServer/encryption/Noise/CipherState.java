package newServer.encryption.Noise;

public interface CipherState {
	void initializeKey(byte[] key);
	boolean hasKey();
	int getKeyLength();
	void setNonce(long nonce);
	int encryptWithAd(byte[] ad, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset, int length);
	int decryptWithAd(byte[] ad, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset, int length);
}
