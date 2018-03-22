package Rekognition;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Celebrity;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Emotion;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesRequest;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.util.List;
import Backend.Database;

/**
 *
 * @author magnusnilsson
 */
public class Rekognition {
	static Database db;

	public static void main(String[] args) throws InterruptedException, Exception {
		db = new Database();
		db.dbConnect();
		// System.out.println("Hallå");
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAI3CI3Y2D7WIJWQZA",
				"uVHEbKMDQQb8M16aXoR7U/BVYtooxpS25fgV0Y3l");
		String photo;
		String searchWay;
		// String photo = args[0];

		ByteBuffer imageBytes;
		File folder = new File("/Users/davidahl/desktop/Bilder");
		for (File fileEntry : folder.listFiles()) {

			try (InputStream inputStream = new FileInputStream(
					new File("/Users/davidahl/desktop/Bilder/" + fileEntry.getName()))) {
				imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
				photo = fileEntry.getName();
				searchWay = "/Users/davidahl/desktop/Bilder/" + fileEntry.getName();
			}

			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
					.withRegion(Regions.EU_WEST_1).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			try {
				db.saveBinData(photo, searchWay);
				faceDetection(rekognitionClient, imageBytes);
				objectDetection(rekognitionClient, imageBytes);
				celebritiesDetection(rekognitionClient, imageBytes);
				textDetection(rekognitionClient, imageBytes);
			} catch (AmazonRekognitionException e) {
				e.printStackTrace();
			}
		}
	}

	private static void faceDetection(AmazonRekognition rekognitionClient, ByteBuffer imageBytes) throws Exception {
		DetectFacesRequest request = new DetectFacesRequest().withImage(new Image().withBytes(imageBytes))
				.withAttributes(Attribute.ALL);

		DetectFacesResult result = rekognitionClient.detectFaces(request);
		List<FaceDetail> faceDetails = result.getFaceDetails();

		for (FaceDetail face : faceDetails) {
			AgeRange ageRange = face.getAgeRange();
			System.out.println(ageRange.getLow() + " " + ageRange.getHigh());
			db.saveFaceData(ageRange.getLow(), ageRange.getHigh(), face.getBoundingBox().getWidth(),
					face.getBoundingBox().getHeight(), face.getBoundingBox().getLeft(), face.getBoundingBox().getTop(),
					face.getConfidence());
			db.saveFaceDatail("Beard", face.getBeard().getValue(), face.getBeard().getConfidence());
			db.saveFaceDatail("Eyeglasses", face.getEyeglasses().getValue(), face.getEyeglasses().getConfidence());
			db.saveFaceDatail("EyesOpen", face.getEyesOpen().getValue(), face.getEyesOpen().getConfidence());
			db.saveFaceDatail("MouthOpen", face.getMouthOpen().getValue(), face.getMouthOpen().getConfidence());
			db.saveFaceDatail("Mustache", face.getMustache().getValue(), face.getMustache().getConfidence());
			db.saveFaceDatail("Smile", face.getSmile().getValue(), face.getSmile().getConfidence());
			db.saveFaceDatail("Sunglasses", face.getSunglasses().getValue(), face.getSunglasses().getConfidence());
			String[] ss;

			for (Emotion i : face.getEmotions()) {
				ss = i.toString().split(" ");
				ss = ss[1].split(",");
				db.saveFaceMood(ss[0], i.getConfidence());
			}
		}
	}

	private static void objectDetection(AmazonRekognition rekognitionClient, ByteBuffer imageBytes) throws Exception {
		DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withBytes(imageBytes))
				.withMaxLabels(10).withMinConfidence(70F);

		DetectLabelsResult result = rekognitionClient.detectLabels(request);
		List<Label> labels = result.getLabels();

		System.out.println("Detected labels for image");
		for (Label label : labels) {
			db.saveObject(label.getName(), label.getConfidence());
			System.out.println(label.getName() + ": " + label.getConfidence().toString());
		}
	}

	private static void celebritiesDetection(AmazonRekognition rekognitionClient, ByteBuffer imageBytes)
			throws Exception {
		RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest()
				.withImage(new Image().withBytes(imageBytes));

		System.out.println("Looking for celebrities in image\n");

		RecognizeCelebritiesResult result = rekognitionClient.recognizeCelebrities(request);

		// Display recognized celebrity information
		List<Celebrity> celebs = result.getCelebrityFaces();
		System.out.println(celebs.size() + " celebrity(s) were recognized.\n");

		for (Celebrity celebrity : celebs) {
			System.out.println("Celebrity recognized: " + celebrity.getName());
			System.out.println("Celebrity ID: " + celebrity.getId());
			BoundingBox boundingBox = celebrity.getFace().getBoundingBox();
			System.out.println("position: " + boundingBox.getLeft().toString() + " " + boundingBox.getTop().toString());
			System.out.println("Further information (if available):");
			for (String url : celebrity.getUrls()) {
				System.out.println(url);
			}
			System.out.println();
		}
		System.out.println(result.getUnrecognizedFaces().size() + " face(s) were unrecognized.");
	}

	private static void textDetection(AmazonRekognition rekognitionClient, ByteBuffer imageBytes) throws Exception {
		DetectTextRequest request = new DetectTextRequest().withImage(new Image().withBytes(imageBytes));

		DetectTextResult result = rekognitionClient.detectText(request);
		List<TextDetection> textDetections = result.getTextDetections();

		System.out.println("Detected lines and words for image");
		for (TextDetection text : textDetections) {

			System.out.println("Detected: " + text.getDetectedText());
			System.out.println("Confidence: " + text.getConfidence().toString());
			System.out.println("Id : " + text.getId());
			System.out.println("Parent Id: " + text.getParentId());
			System.out.println("Type: " + text.getType());
			System.out.println();
		}
	}
}
