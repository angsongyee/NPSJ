package newServer.encryption.Noise;

public class Pattern {

	private static String[] XX = { "e", "next", "e", "ee", "s", "es", "next", "s", "se" };

	public static String[] getPattern(String name) {
		switch (name) {
		case "XX":
			return XX;
		default:
			return null;
		}
	}
}