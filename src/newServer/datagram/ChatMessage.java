package newServer.datagram;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.buffer.ByteBuf;

public class ChatMessage implements Serializable {

	public enum Type {
		TEXT, FILE
	}

	private static int msgCount = 0;

	private String sender;
	private String timeStamp;
	private int msgNo;
	private String textMsg;
	private ByteBuf fileMsg;
	private int fileSequenceNo;
	private Type msgType;
		
	public ChatMessage(String sender) {
		this.sender = sender;
		this.timeStamp = new SimpleDateFormat("dd:mm a").format(new Date());
		this.msgNo = msgCount++;
	}
	
	public ChatMessage(String sender, String textMsg) {
		this(sender);
		this.msgType = Type.TEXT;
		this.textMsg = textMsg;
	}
	
	public ChatMessage(String sender, byte[] filePart) {
		this(sender);
		this.msgType = Type.FILE;
		this.textMsg = textMsg;
	}
	
	
	
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getMsgNo() {
		return msgNo;
	}

	public void setMsgNo(int msgNo) {
		this.msgNo = msgNo;
	}

	public String getTextMsg() {
		return textMsg;
	}

	public void setTextMsg(String textMsg) {
		this.textMsg = textMsg;
	}

	public ByteBuf getFileMsg() {
		return fileMsg;
	}

	public void setFileMsg(ByteBuf fileMsg) {
		this.fileMsg = fileMsg;
	}

	public static int getMsgCount() {
		return msgCount;
	}

	public static void setMsgCount(int msgCount) {
		ChatMessage.msgCount = msgCount;
	}

	public Type getMsgType() {
		return msgType;
	}

	public void setMsgType(Type msgType) {
		this.msgType = msgType;
	}
}
