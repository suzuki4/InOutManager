package main;

import java.io.IOException;

import database.DBManager;
import qrCode.QrReader;
import frame.Frame;
import frame.FrameMain;

public class Main {

	public static void main(String[] args) throws Exception {
		//
		
		//MainFrame mainFrame = new MainFrame("main");
		Frame frame = new Frame();
		//DBManager manager = new DBManager();
		//manager.connect();
		/*QrReader qrReader = new QrReader(0);
		String qrCode = qrReader.getQrCode();
		System.out.println(qrCode);
		mainFrame.setMsg(qrCode);
		*/
		while(true) {
			frame.frameMain.setClock();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
