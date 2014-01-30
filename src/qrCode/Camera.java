package qrCode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class Camera {
	 public void takePicture() throws IOException {
		    Webcam webcam = null;
		    webcam = Webcam.getDefault();
		    if (webcam != null) {
		      System.out.println("Webcam : " + webcam.getName());
		      webcam.open();
		      BufferedImage image = webcam.getImage();
		      ImageIO.write(image, "PNG", new File("pictures/webcam-capture.png"));
		      webcam.close();
		    } else {
		      System.out.println("Failed: Webcam Not Found Error");
		    }
		  }
}
