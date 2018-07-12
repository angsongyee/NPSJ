package newServer.datagram;

import java.io.Serializable;

public class ServerDatagram implements Serializable { // for messages communicating with the server
	private String msg;
	
	public ServerDatagram(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
