package qrCode;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import database.DBManager;

public class QrReader {
	//Singleton
	private static final QrReader qrReader = new QrReader();
    private QrReader() {
    	constructor();
    }
    public static QrReader getInstance() {
    	return qrReader;
    }
    
	//フィールド
	private Webcam webcam;
	private Result result;
	public boolean isWorking;	
	
    //
    private void constructor() {
		//カメラ番号取得
		int cameraNumber = -1;
		try {
			DBManager manager = DBManager.getInstance();
			cameraNumber = manager.getCamera();
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//画像サイズ
		Dimension size = WebcamResolution.QVGA.getSize();
		//カメラ番号が接続カメラ数を超えていたらカメラ番号0で対応
		if(cameraNumber >= getWebcamNames().length) cameraNumber = 0;
		//カメラ番号を指定して取得
        webcam = Webcam.getWebcams().get(cameraNumber);
        webcam.setViewSize(size);
        
        isWorking = false;
	}
    
	//カメラ名を取得
	public String[] getWebcamNames() {
		List<Webcam> list = Webcam.getWebcams();
		String[] webcamNames = new String[list.size()];
		int i = 0;
		for (Iterator<Webcam> iterator = list.iterator(); iterator.hasNext(); i++) {
			webcamNames[i] = iterator.next().getName();
		}		
		return webcamNames;
	}
	
	//スレッド
	public String getQr() {
		//初期化
    	result = null;
    	BufferedImage image = null;	
    	//カメラを開いて取得開始
    	webcam.open();
		while(isWorking == true) {
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
            webcam.close();
            return result.getText();
    	}
		webcam.close();
		Webcam.getDiscoveryService().stop();
		return null;
	}
}