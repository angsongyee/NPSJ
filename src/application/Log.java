package application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

public class Log {
	InetAddress inetAddress;
	private String id;
	private String osUser;
	private String userLoc;
	private Date dateTime;
	private String osName;
	private String ip;
	private String hostname;

	public Log(String id, String osUser, String userLoc, Date dateTime, String osName, String ip, String hostname)
			throws UnknownHostException {
		this.id = id;
		this.osUser = osUser;
		this.userLoc = userLoc;
		this.dateTime = dateTime;
		this.osName = osName;
		this.ip = ip;
		this.hostname = hostname;
	}

	public Log(String id) throws UnknownHostException {
		this.id = id;
		this.osUser = System.getProperties().getProperty("user.name");
		this.userLoc = Calendar.getInstance().getTimeZone().getID();
		this.dateTime = new Date();
		this.osName = System.getProperties().getProperty("os.name");
		inetAddress = InetAddress.getLocalHost();
		this.ip = inetAddress.getHostAddress();
		this.hostname = inetAddress.getHostName();
	}

	public Log() {

	}

	public Log currLog() throws UnknownHostException {
		Log log = new Log();
		log.osUser = System.getProperties().getProperty("user.name");
		log.userLoc = Calendar.getInstance().getTimeZone().getID();
		log.dateTime = new Date();
		log.osName = System.getProperties().getProperty("os.name");
		inetAddress = InetAddress.getLocalHost();
		log.ip = inetAddress.getHostAddress();
		log.hostname = inetAddress.getHostName();
		return log;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOsUser() {
		return osUser;
	}

	public void setOsUser(String osUser) {
		this.osUser = osUser;
	}

	public String getUserLoc() {
		return userLoc;
	}

	public void setUserLoc(String userLoc) {
		this.userLoc = userLoc;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
