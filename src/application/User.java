package application;

public class User {
	private int code;
	private String fName;
	private String lName;
	private String id;
	private String pass;
	private String salt;
	private String hash;
	private int age;
	private String job;

	public User() {
	}

	public User(int code, String fName, String lName, String id, String pass, int age, String job) {
		this.code = code;
		this.fName = fName;
		this.lName = lName;
		this.id = id;
		this.pass = pass;
		this.age = age;
		this.job = job;
	}

	public User(int code, String fName, String lName, String id, String salt, String hash, int age, String job) {
		this.code = code;
		this.fName = fName;
		this.lName = lName;
		this.id = id;
		this.salt = salt;
		this.hash = hash;
		this.age = age;
		this.job = job;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSalt() {
		return salt;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
}
