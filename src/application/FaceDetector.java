package application;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvCopy;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvSize;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_AREA;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvResize;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvPoint;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class FaceDetector implements Runnable {
	Database db = new Database();
	User user;
	FaceRecognizer fr = new FaceRecognizer();
	OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
	Java2DFrameConverter fc = new Java2DFrameConverter();
	User output;

	private String classiferName;
	private File classifierFile;

	private int count;
	private boolean saveFace = false;
	private boolean isRecFace = false;
	private boolean stop = false;

	private CvHaarClassifierCascade classifier = null;
	private CvMemStorage storage = null;
	private FrameGrabber grabber = null;
	private IplImage grabbedImage = null, grayImage = null, smallImage = null;
	private IplImage temp;
	private ImageView frames;
	private CvSeq faces = null;

	private int recognizeCode;

	protected void init() {
		fr.init();
		setClassifier("/haarcascade_frontalface_alt.xml");
	}

	protected void start() {
		try {
			new Thread(this).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			try {
				grabber = OpenCVFrameGrabber.createDefault(0);
				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();
				grabbedImage = grabberConverter.convert(grabber.grab());

				storage = CvMemStorage.create();
			} catch (Exception e) {
				if (grabber != null)
					grabber.release();
				grabber = new OpenCVFrameGrabber(0);
				grabber.setImageWidth(700);
				grabber.setImageHeight(700);
				grabber.start();
				grabbedImage = grabberConverter.convert(grabber.grab());
			}

			grayImage = cvCreateImage(cvGetSize(grabbedImage), 8, 1); // Convert GreyScale
			smallImage = cvCreateImage(cvSize(grabbedImage.width() / 4, grabbedImage.height() / 4), 8, 1); // ReduceSize
			stop = false;

			while (!stop && (grabbedImage = grabberConverter.convert(grabber.grab())) != null) {
				Frame frame = grabberConverter.convert(grabbedImage);
				BufferedImage image = fc.getBufferedImage(frame, 2.2 / grabber.getGamma());
				Graphics2D g2 = image.createGraphics();

				if (faces == null) {
					cvClearMemStorage(storage);
					// Temporary Image (Face)
					temp = cvCreateImage(cvGetSize(grabbedImage), grabbedImage.depth(), grabbedImage.nChannels());
					cvCopy(grabbedImage, temp);
					cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
					cvResize(grayImage, smallImage, CV_INTER_AREA);
					// cvHaarDetectObjects(image, cascade, storage, scale_factor, min_neighbors,
					// flags, min_size, max_size)
					faces = cvHaarDetectObjects(smallImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
					// Face Detection
					CvPoint org = null;
					if (grabbedImage != null) {
						if (faces != null) {
							g2.setColor(Color.green);
							g2.setStroke(new BasicStroke(2));
							int total = faces.total();

							for (int i = 0; i < total; i++) {
								// Face Rectangles
								CvRect r = new CvRect(cvGetSeqElem(faces, i));
								g2.drawRect((r.x() * 4), (r.y() * 4), (r.width() * 4), (r.height() * 4));
								CvRect re = new CvRect((r.x() * 4), r.y() * 4, (r.width() * 4), r.height() * 4);
								cvSetImageROI(temp, re);

								org = new CvPoint(r.x(), r.y());

								if (isRecFace) {
									this.recognizeCode = fr.recognize(temp);
									// Get Recognized Face
									db.init();
									user = db.getUser(this.recognizeCode);
									this.output = user;
									// Print Recognized Face
									g2.setColor(Color.WHITE);
									g2.setFont(new Font("Arial Black", Font.BOLD, 20));
									String names = user.getfName() + " " + user.getlName();
									g2.drawString(names, (int) (r.x() * 6.5), r.y() * 4);
								}

								if (saveFace) {
									// Save Face
									String face = "faces/" + user.getCode() + "-" + user.getfName() + "_"
											+ user.getlName() + "_" + count + ".png";
									cvSaveImage(face, temp);
								}
							}
							this.saveFace = false;

							faces = null;
						}
						WritableImage showFrame = SwingFXUtils.toFXImage(image, null);
						frames.setImage(showFrame);
					}
					cvReleaseImage(temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void stop() {
		stop = true;
		grabbedImage = grayImage = smallImage = null;

		try {
			grabber.stop();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}

		try {
			grabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}
		grabber = null;
	}

	protected void saveFace(User user) {
		this.user = user;
	}

	protected void setClassifier(String name) {
		try {
			setClassiferName(name);
			classifierFile = Loader.extractResource(classiferName, null, "classifier", ".xml");

			if (classifierFile == null || classifierFile.length() <= 0) {
				throw new IOException("Could Not Extract: " + classiferName);
			}
			// PreLoad "opencv_objdetect"
			Loader.load(opencv_objdetect.class);
			classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
			classifierFile.delete();
			if (classifier.isNull()) {
				throw new IOException("Classifier Unaable to Load");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClassiferName() {
		return classiferName;
	}

	public void setClassiferName(String classiferName) {
		this.classiferName = classiferName;
	}

	public User getOutput() {
		return output;
	}

	public void clearOutput() {
		this.output = null;
	}

	public void setOutput(User output) {
		this.output = output;
	}

	public int getRecognizeCode() {
		return recognizeCode;
	}

	public void setRecognizeCode(int recogniseCode) {
		this.recognizeCode = recogniseCode;
	}

	public void setFrame(ImageView frame) {
		this.frames = frame;
	}

	public void setSaveFace(Boolean f, int count) {
		this.saveFace = f;
		this.count = count;
	}

	public Boolean getIsRecFace() {
		return isRecFace;
	}

	public void setIsRecFace(Boolean isRecFace) {
		this.isRecFace = isRecFace;
	}
}
