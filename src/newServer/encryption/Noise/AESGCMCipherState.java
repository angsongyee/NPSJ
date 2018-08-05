package newServer.encryption.Noise;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import newServer.handlers.test;

public class AESGCMCipherState implements CipherState {

	private Cipher cipher;
	private SecretKeySpec k;
	private long n;
	private final int KEYLENGTH = 32;

	public AESGCMCipherState() {
		try {
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initializeKey(byte[] key) {
		System.out.println("New key");
		k = new SecretKeySpec(key, "AES"); // force 256 bit key
		n = 0;
	}

	@Override
	public boolean hasKey() {
		return k != null;
	}

	@Override
	public int getKeyLength() {
		return KEYLENGTH;
	}

	@Override
	public void setNonce(long nonce) {
		n = nonce;
	}

	@Override
	public int encryptWithAd(byte[] ad, byte[] plaintext, int plaintextOffset, byte[] ciphertext, int ciphertextOffset,
			int length) {
		if (hasKey()) {
			byte[] result = null;
			try {
				GCMParameterSpec gcmParamSpec = new GCMParameterSpec(128, getIv());
				cipher.init(Cipher.ENCRYPT_MODE, k, gcmParamSpec, new SecureRandom());

				if (ad != null)
					cipher.updateAAD(ad);
				result = cipher.doFinal(plaintext, plaintextOffset, length);
				System.arraycopy(result, 0, ciphertext, ciphertextOffset, result.length);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			n++;
			return result.length;
		} else {
			System.arraycopy(plaintext, plaintextOffset, ciphertext, ciphertextOffset, length);
			return length;
		}
	}

	@Override
	public int decryptWithAd(byte[] ad, byte[] ciphertext, int ciphertextOffset, byte[] plaintext, int plaintextOffset,
			int length) {
		if (hasKey()) {
			byte[] result = null;
			try {
				GCMParameterSpec gcmParamSpec = new GCMParameterSpec(128, getIv());
				cipher.init(Cipher.DECRYPT_MODE, k, gcmParamSpec, new SecureRandom());

				if (ad != null)
					cipher.updateAAD(ad);
				result = cipher.doFinal(ciphertext, ciphertextOffset, length);

				System.arraycopy(result, 0, plaintext, plaintextOffset, result.length);
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (AEADBadTagException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			n++;
			return result.length;
		} else {
			System.arraycopy(ciphertext, ciphertextOffset, plaintext, plaintextOffset, length);
			return length;
		}
	}

	private byte[] getIv() {
		byte[] iv = new byte[12];
		iv[0] = 0;
		iv[1] = 0;
		iv[2] = 0;
		iv[3] = 0;
		iv[4] = (byte) (n >> 56);
		iv[5] = (byte) (n >> 48);
		iv[6] = (byte) (n >> 40);
		iv[7] = (byte) (n >> 32);
		iv[8] = (byte) (n >> 24);
		iv[9] = (byte) (n >> 16);
		iv[10] = (byte) (n >> 8);
		iv[11] = (byte) n;
		return iv;
	}

}
