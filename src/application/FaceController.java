package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FaceController {
	// Face Files
	private String filePath = "./faces";

	// FaceRecognizer.fxml
	@FXML
	private ListView<String> output;
	@FXML
	private Button recognizeBtn;
	@FXML
	private Button stopRecBtn;

	// NewUser.fxml
	int count = 0;
	@FXML
	private ImageView frame;
	@FXML
	private TextField fName;
	@FXML
	private TextField lName;
	@FXML
	private TextField pass;
	@FXML
	private TextField age;
	@FXML
	private ComboBox<String> job;
	@FXML
	private Label id;
	@FXML
	private Button saveBtn;
	@FXML
	private Button nxtBtn;

	// 2nd Authentication
	private Boolean auth;
	@FXML
	private TextField idLogin;
	@FXML
	private TextField passLogin;

	FaceDetector fd = new FaceDetector();
	Database db = new Database();
	Scanner sc = new Scanner(System.in);
	Log log = new Log();
	User user;
	private ImageView imageView;

	private static ObservableList<String> outEvent = FXCollections.observableArrayList();

	protected void init() {
		startCamera();
		try {
			job.getItems().setAll("Employee", "Manager", "Administrator");
		} catch (NullPointerException e) {
			System.out.println("Main_Rec");
		}
	}

	protected void startCamera() {
		fd.init();
		fd.setFrame(frame);
		fd.start();
		;
		if (!db.init()) {
			System.out.println("Database Connection Failed");
		} else {
			System.out.println("Database Connection Successful");
		}

		// Faces
		String path = filePath;

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		// Image Reader
		for (final File file : listOfFiles) {
			imageView = createImageView(file);
		}
		System.out.println("Camera Started");
	}

	@FXML
	protected void faceRecognise() {
		fd.setIsRecFace(true);
		recognizeBtn.setText("Login");

		// Get Detected Faces
		user = fd.getOutput();

		if (user == null) {
			System.out.println("Detecting");
		} else {
			// Authentication
			auth = false;
			String id = idLogin.getText().trim();
			String pass = db.getHash(Integer.toString(user.getCode()), passLogin.getText().trim());
			if (id.isEmpty() || pass.isEmpty()) {
				System.out.println("Invalid Login");
			} else {
				do {
					if (!id.equals(user.getId())) {
						auth = false;
						System.out.println("Invalid User");
						break;
					} else if (!pass.equals(user.getHash())) {
						auth = false;
						System.out.println("Invalid Pass");
						break;
					} else if (id.equals(user.getId()) && pass.equals(user.getHash())) {
						auth = true;
						try {
							log = new Log(user.getId());
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						db.insertLog(log);
						System.out.println("Login Successful");
					}
				} while (auth == false);
			}

			// Face Data
			output.getItems().clear();

			String n1 = "First Name\t:\t" + user.getfName();
			outEvent.add(n1);
			output.setItems(outEvent);

			String n2 = "Last Name\t:\t" + user.getlName();
			outEvent.add(n2);
			output.setItems(outEvent);

			String a = "Age \t\t\t:\t" + user.getAge();
			outEvent.add(a);
			output.setItems(outEvent);
		}
		stopRecBtn.setDisable(false);
	}

	@FXML
	protected void stopRecognise() {
		fd.setIsRecFace(false);
		fd.clearOutput();
		recognizeBtn.setText("Recognize");
		stopRecBtn.setDisable(true);
	}

	@FXML
	protected void saveFace() {
		// Input Validation
		if (fName.getText().trim().isEmpty() || pass.getText().trim().isEmpty() || age.getText().trim().isEmpty()) {
			System.out.println("Fname, Pass and Age must not be empty");
		} else {
			if (count > 0) {
				fd.setSaveFace(true, count);
				saveBtn.setText("Add Face (" + count + ")");
				System.out.println("Face Added");
			} else {
				try {
					String newID = job.getValue().trim().toUpperCase().charAt(0)
							+ String.format("%04d", db.getNextIntID(job.getValue().toUpperCase()));

					User newUser = new User();
					newUser.setCode(db.getNextCode());
					newUser.setfName(fName.getText().trim().toUpperCase());
					newUser.setlName(lName.getText().trim().toUpperCase());
					newUser.setId(newID);
					newUser.setPass(pass.getText().trim());
					newUser.setAge(Integer.parseInt(age.getText()));
					newUser.setJob(job.getValue().trim().toUpperCase());

					fd.saveFace(newUser);
					fd.setSaveFace(true, count);
					db.insertUser(newUser);

					count++;
					id.setText(newID);
					saveBtn.setText("Add Face (" + count + ")");
				} catch (NumberFormatException e) {
					System.out.println("Invalid Age");
					count--;
				}
			}
		}
	}

	@FXML
	protected void nextUser() {
		count = 0;
		id.setText("");
		fName.clear();
		lName.clear();
		pass.clear();
		age.clear();
		saveBtn.setText("Save");
	}

	@FXML
	protected void stopCam() {
		fd.stop();
		recognizeBtn.setDisable(true);
		stopRecBtn.setDisable(true);
		db.close();
	}

	private ImageView createImageView(final File imageFile) {
		try {
			final Image img = new Image(new FileInputStream(imageFile), 120, 0, true, true);
			imageView = new ImageView(img);
			imageView.setStyle("-fx-background-color: BLACK");
			imageView.setFitHeight(120);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			imageView.setCache(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return imageView;
	}
}
