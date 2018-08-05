package newServer.datagram;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ClientDatagram implements Serializable { // For messages intended for another client
	
	private String sender;
	private String recipient;
	byte[] payload;

	public ClientDatagram(String sender, String recipient, byte[] payload) {
		this.sender = sender;
		this.recipient = recipient;
		System.arraycopy(payload, 0, this.payload, 0, payload.length);
	}
	
	public ClientDatagram(String sender, String recipient, ChatMessage chatMsg) {
		this.sender = sender;
		this.recipient = recipient;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(chatMsg);
			out.flush();
			this.payload = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ClientDatagram(String sender, String recipient, NoiseHandshake noise) {
		this.sender = sender;
		this.recipient = recipient;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(noise);
			out.flush();
			this.payload = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public String getSender() {
		return sender;
	}
}
