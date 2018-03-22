package Backend;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Database {
	Connection conn;
	PreparedStatement psmnt = null;
	ByteBuffer fis;
	ResultSet rs;
	int incrementValueID = 1;
	int incrementValueFaceID = 1;

	public void dbConnect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Images?autoReconnect=true&useSSL=false",
					"root", "password");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean saveBinData(String imageName, String imageData) throws SQLException {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(imageData));
			psmnt = (PreparedStatement) conn.prepareStatement("insert into binData(imageName, imageData) values(?,?)");

			psmnt.setString(1, imageName);
			psmnt.setBinaryStream(2, inputStream, (int) (imageData.length()));
			psmnt.executeUpdate();

			Statement s = conn.createStatement();
			rs = s.executeQuery("SELECT LAST_INSERT_ID();");
			rs.next();
			incrementValueID = (int) rs.getInt(1);
			System.out.println(incrementValueID);
		} catch (FileNotFoundException e) {
			System.out.println("sdasdas");
			e.printStackTrace();
		}

		return false;
	}

	public boolean saveFaceData(int ageLow, int ageHigh, double width, double height, double left, double top,
			double confidence) throws SQLException {

		psmnt = (PreparedStatement) conn
				.prepareStatement("insert into Faces(ID, ageLow, ageHigh, width, height, leftSide, top, confidence)"
						+ " values(?,?,?,?,?,?,?,?)");

		psmnt.setInt(1, incrementValueID);
		psmnt.setInt(2, ageLow);
		psmnt.setInt(3, ageHigh);
		psmnt.setDouble(4, width);
		psmnt.setDouble(5, height);
		psmnt.setDouble(6, left);
		psmnt.setDouble(7, top);
		psmnt.setDouble(8, confidence);
		psmnt.executeUpdate();
		Statement s = conn.createStatement();
		rs = s.executeQuery("SELECT LAST_INSERT_ID();");
		rs.next();
		incrementValueFaceID = (int) rs.getInt(1);
		return true;
	}

	public boolean saveFaceDatail(String name, boolean status, double confidence) throws SQLException {

		psmnt = (PreparedStatement) conn
				.prepareStatement("insert into FaceDetail(detailName, faceID, stat, confidence)" + " values(?,?,?,?)");
		psmnt.setString(1, name);
		psmnt.setInt(2, incrementValueFaceID);
		psmnt.setBoolean(3, status);
		psmnt.setDouble(4, confidence);
		psmnt.executeUpdate();
		return true;
	}

	public boolean saveFaceMood(String mood, double confidence) {

		try {
			psmnt = (PreparedStatement) conn
					.prepareStatement("insert into FaceMood(FaceID, mood, confidence)" + " values(?,?,?)");
			psmnt.setInt(1, incrementValueFaceID);
			psmnt.setString(2, mood);
			psmnt.setDouble(3, confidence);
			psmnt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public boolean saveObject(String label, double confidence) {

		try {
			psmnt = (PreparedStatement) conn
					.prepareStatement("insert into Object(label, ID, confidence)" + " values(?,?,?)");
			psmnt.setString(1, label);
			psmnt.setInt(2, incrementValueID);
			psmnt.setDouble(3, confidence);
			psmnt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public void createARFF() throws ParseException, SQLException {
		FastVector atts;
		FastVector attsRel;
		FastVector attVals;
		FastVector attValsRel;
		Instances dataRel;
		Instances data;
		double[] vals;
		double[] valsRel;
		int i;

		Statement s = conn.createStatement();

		rs = s.executeQuery("select * from faces left join facedetail on faces.faceID = FaceDetail.faceID where faces.ID=1");

		ResultSetMetaData metadata = rs.getMetaData();

//		int size = metadata.getColumnCount();

		// 1. set up attributes
		atts = new FastVector();
		attVals = new FastVector();

		// - numeric
		atts.addElement(new Attribute(metadata.getColumnName(2)));
		while (rs.next()) {
			
			atts.addElement(new Attribute(rs.getString(10)));
		}

		// 2. Create instance data
		data = new Instances("myRelation", atts, 0);

		// 3. fill with data
		vals = new double[data.numAttributes()];
		rs = s.executeQuery("select * from faces left join facedetail on faces.faceID = FaceDetail.faceID order by ID");
		rs.next();
		while (!rs.isAfterLast()) {
			vals[0] = rs.getInt(2);
			
			for (int k = 1; k < data.numAttributes(); k++) {
				
				if(rs.getInt(12)==1){
				vals[k] = rs.getDouble(13);
				}else{
					vals[k] = 100-rs.getDouble(13);
				}
				rs.next();
			}

			// add
			data.add(new DenseInstance(1.0, vals));
			// Empty vector
			vals = new double[data.numAttributes()];
		}
		// 4 output data
		System.out.println(data);
		try {
			generateFile(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateFile(Instances data) throws IOException {

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		File f = new File("/Users/davidahl/desktop/test.arff");
		saver.setFile(new File("/Users/davidahl/desktop/test.arff"));
		saver.setDestination(new File("/Users/davidahl/desktop/test.arff"));
		saver.writeBatch();

	}

}
