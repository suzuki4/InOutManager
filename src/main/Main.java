package main;

import java.io.IOException;

import qrCode.QrReader;
import frame.MainFrame;

public class Main {

	public static void main(String[] args) throws IOException {
		MainFrame mainFrame = new MainFrame("main");
		QrReader qrReader = new QrReader(0);
		String qrCode = qrReader.getQrCode();
		System.out.println(qrCode);
		mainFrame.setMsg(qrCode);
		
		while(true) {
			mainFrame.setClock();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
