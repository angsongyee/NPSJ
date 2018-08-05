package newServer.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;

import newServer.datagram.ChatMessage;
import newServer.datagram.ClientDatagram;
import newServer.encryption.Noise.CipherStatePair;
import newServer.encryption.Noise.HandshakeState;
import newServer.encryption.Noise.Noise;

public class test {
	static byte[] key = new byte[32];
	static byte[] key2 = new byte[32];

	public static void main(String[] args) throws NoSuchAlgorithmException, ShortBufferException, BadPaddingException {
		HandshakeState client1;
		HandshakeState client2;
		byte[] client1output = new byte[65536];
		byte[] client2output = new byte[65536];
		byte[] buffer = new byte[65536];

		Noise.removeCryptographyRestrictions();

		client1 = new HandshakeState("Noise_XX_25519_AESGCM_SHA256", true, null);
		client2 = new HandshakeState("Noise_XX_25519_AESGCM_SHA256", false, null);

		String payloadfirstmsg = "Hi!";
		byte[] payloadfirstmsgbytes = payloadfirstmsg.getBytes();
		System.out.println("Client 1 initiating handshake: -> e // Message = " + payloadfirstmsg);
		int len = client1.writeMessage(payloadfirstmsgbytes, 0, client1output, 0, payloadfirstmsgbytes.length);

		System.out.println("\nClient 2 reading msg");
		client2.readMessage(client1output, 0, buffer, 0, len);
		System.out.println("\nReceived message: " + new String(buffer, 0, len));

		String msgtwo = "Client 2 details";
		byte[] msgtwobytes = msgtwo.getBytes();
		System.out.println("\nClient 2 reply: <- e, s // Message = " + msgtwo);
		len = client2.writeMessage(msgtwobytes, 0, client2output, 0, msgtwobytes.length);

		System.out.println("\nClient 1 reading msg");
		client1.readMessage(client2output, 0, buffer, 0, len);
		System.out.println("\nReceived message: " + new String(buffer, 0, len));

		String msgthree = "Client 1 Details";
		byte[] msgthreebytes = msgthree.getBytes();
		System.out.println("\nClient 1 reply: <- s // Message = " + msgthree);
		len = client1.writeMessage(msgthreebytes, 0, client1output, 0, msgthreebytes.length);

		System.out.println("\nClient 2 reading msg");
		client2.readMessage(client1output, 0, buffer, 0, len);
		System.out.println("\nReceived message: " + new String(buffer, 0, len));

		CipherStatePair client1pair = client1.split();
		CipherStatePair client2pair = client2.split();

		String msg = "Welcome";
		byte[] msgbytes = msg.getBytes();
		len = client1pair.getSender().encryptWithAd(null, msgbytes, 0, client1output, 0, msgbytes.length);
		client2pair.getSender().decryptWithAd(null, client1output, 0, buffer, 0, len);

		System.out.println("\nReceived message: " + new String(buffer, 0, len));
		
		ClientDatagram c = new ClientDatagram("hi", "bye", new ChatMessage("hi", "hihi"));
		ByteArrayInputStream bis = new ByteArrayInputStream(c.getPayload());
		try {
			ObjectInputStream in = new ObjectInputStream(bis);
			ChatMessage chat = (ChatMessage) in.readObject();
			System.out.println(chat.getTextMsg());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	public static String ByteToHexBitFiddle(byte[] bytes) {
		char[] c = new char[bytes.length * 2];
		int b;
		for (int i = 0; i < bytes.length; i++) {
			b = bytes[i] >> 4;
			c[i * 2] = (char) (55 + b + (((b - 10) >> 31) & -7));
			b = bytes[i] & 0xF;
			c[i * 2 + 1] = (char) (55 + b + (((b - 10) >> 31) & -7));
		}
		return new String(c);
	}

}
