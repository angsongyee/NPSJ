package application;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.cvarrToMat;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;

public class FaceRecognizer {
	LBPHFaceRecognizer faceRecognizer;
	File root;
	MatVector images;
	Mat labels;

	protected void init() {
		// Face Files
		String trainingDir = "./faces";
		root = new File(trainingDir);

		FilenameFilter imgFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
			}
		};

		File[] imageFiles = root.listFiles(imgFilter);
		if (imageFiles.length == 0) {
			System.out.println("No Training");
		} else {
			this.images = new MatVector(imageFiles.length);
			this.labels = new Mat(imageFiles.length, 1, CV_32SC1);
			IntBuffer labelsBuf = labels.createBuffer();
			int count = 0;

			// Image Reader
			for (File image : imageFiles) {
				Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
				// Extract Unique Face Code
				int label = Integer.parseInt(image.getName().split("\\-")[0]);
				images.put(count, img);
				labelsBuf.put(count, label);
				count++;
			}

			// Face Training
			this.faceRecognizer = createLBPHFaceRecognizer();
			this.faceRecognizer.train(images, labels);
		}
	}

	protected int recognize(IplImage faceData) {
		Mat faces = cvarrToMat(faceData);
		cvtColor(faces, faces, CV_BGR2GRAY);
		IntPointer label = new IntPointer(1);
		DoublePointer confidence = new DoublePointer(0);

		this.faceRecognizer.predict(faces, label, confidence);
		int predictedLabel = label.get(0);
		return predictedLabel;
	}
}
