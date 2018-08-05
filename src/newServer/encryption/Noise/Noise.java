package newServer.encryption.Noise;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Map;

public class Noise {

	public static MessageDigest getHash(String hashName) throws NoSuchAlgorithmException {
		switch (hashName) {
		case "SHA256":
			return MessageDigest.getInstance("SHA-256");
		case "SHA512":
			return MessageDigest.getInstance("SHA-512");
		default:
			throw new NoSuchAlgorithmException("Algorithm not supported by Noise: " + hashName);
		}

	}

	public static CipherState getCipher(String cipherName) throws NoSuchAlgorithmException {
		switch (cipherName) {
		case "AESGCM":
			return new AESGCMCipherState();
		default:
			throw new NoSuchAlgorithmException("Unknown encryption algorithm name: " + cipherName);
		}
	}

	public static DHState createDH(String DHName) throws NoSuchAlgorithmException {
		if (DHName.equals("25519"))
			return new Curve25519DHState();
		throw new NoSuchAlgorithmException("Unknown Noise DH algorithm name: " + DHName);
	}

	public static void removeCryptographyRestrictions() {
		if (!isRestrictedCryptography()) {
			return;
		}
		try {
			/*
			 * Do the following, but with reflection to bypass access checks:
			 *
			 * JceSecurity.isRestricted = false; JceSecurity.defaultPolicy.perms.clear();
			 * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
			 */
			final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
			final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
			final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

			final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
			isRestrictedField.setAccessible(true);
			final Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
			isRestrictedField.set(null, false);

			final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
			defaultPolicyField.setAccessible(true);
			final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

			final Field perms = cryptoPermissions.getDeclaredField("perms");
			perms.setAccessible(true);
			((Map<?, ?>) perms.get(defaultPolicy)).clear();

			final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
			instance.setAccessible(true);
			defaultPolicy.add((Permission) instance.get(null));

		} catch (final Exception e) {
		}
	}

	private static boolean isRestrictedCryptography() {
		// This matches Oracle Java 7 and 8, but not Java 9 or OpenJDK.
		final String name = System.getProperty("java.runtime.name");
		final String ver = System.getProperty("java.version");
		return name != null && name.equals("Java(TM) SE Runtime Environment") && ver != null
				&& (ver.startsWith("1.7") || ver.startsWith("1.8"));
	}
}
