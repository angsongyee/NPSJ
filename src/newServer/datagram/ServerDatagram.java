package newServer.datagram;

import java.io.Serializable;

public class ServerDatagram implements Serializable { // for messages communicating with the server
	private String msg;
	private Actions msgType;
	
	public enum Actions {
		LOGIN,
		LOGOUT,
		UPDATE
	}

	public ServerDatagram(String msg) {
		this.msg = msg;
		this.msgType = Actions.LOGIN;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
