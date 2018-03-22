package Backend;
import javax.swing.JFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.amazonaws.util.IOUtils;

public class Main {

	public static void main(String[] args) throws IOException, SQLException, ParseException {
		
		Database db = new Database();
        db.dbConnect();
        db.createARFF();
        
		
		
		

//		   String photo = "10269320.jpg";
//	       
//
//	        ByteBuffer imageBytes;
//	        try (InputStream inputStream = new FileInputStream(new File(photo))) {
//	            imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
//	        }
//	        
//	        Database db = new Database();
//	        db.dbConnect();
//	        db.saveBinData(photo, photo);
//	       
//	        db.saveFaceData(23, 24, 0.02, 0.03, 0.23, 0.54, 98.898);
//	 
//	        
	        
	        
	        
	        
	        
	        
//		JFrame frame = new JFrame();
//		ImageIcon icon = new ImageIcon("/Users/davidahl/Desktop/IMG_1521.JPG");
//		JLabel label = new JLabel(icon);
//		frame.add(label);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setVisible(true);

	}

}
