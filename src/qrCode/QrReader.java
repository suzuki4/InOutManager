package qrCode;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QrReader {
	//field
	private Webcam webcam;
	private Result result;
	
	//constructor
	public QrReader(int webcamNumber) {
		//画像サイズ
		Dimension size = WebcamResolution.QVGA.getSize();
		//カメラ番号を指定して取得
        webcam = Webcam.getWebcams().get(webcamNumber);
        webcam.setViewSize(size);
	}
	
    //QRget
    public String getQrCode() {
    	//初期化
    	result = null;
    	BufferedImage image = null;	
    	//カメラを開いて取得開始
    	webcam.open();
    	while(true) {
        	//100ミリ秒待機
    		try {
        		Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        	//カメラが開いていなければcontinue
            if (!webcam.isOpen()) continue;
        	//イメージがなければcontinue
        	if ((image = webcam.getImage()) == null) continue;
        	//イメージをbitmapへ
        	LuminanceSource source = new BufferedImageLuminanceSource(image);
        	BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        	try {
        		//QR取り出す
        		result = new MultiFormatReader().decode(bitmap);
      			//AudioClip audioClip = Applet.newAudioClip(getClass().getResource("se_maoudamashii_se_syber07.wav"));
      			//audioClip.play();
        	} catch (NotFoundException e) {
                //QRが無かったらcontinue
            	continue;
            }
            //qrCodeを返す
            //Webcam.getDiscoveryService().stop();
            webcam.close();
        	return result.getText();
    	}
	}
}