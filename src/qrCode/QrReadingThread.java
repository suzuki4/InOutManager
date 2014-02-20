package qrCode;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mail.SendMail;
import database.DBManager;
import database.TableHistory;
import frame.FrameMain;

public class QrReadingThread extends Thread {

	private final int STATE_IN = 0;
	private final int STATE_OUT = 1;
	private final int STATE_FAST = 2;
	private JFrame frame; 
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
	
	public QrReadingThread(JFrame frame) {
		this.frame = frame;
	}
	
	public void run() {
		QrReader qrReader = QrReader.getInstance();
		String qr = qrReader.getQr();
		long id = -1;
		DBManager manager = DBManager.getInstance();
		try {
			//officeNameが異なると例外を投げる
			String officeName = manager.getOfficeName();
			if(qr.indexOf(officeName) == -1) throw new SQLException("不正な教室名です。");
			//idを取得。存在しない場合、例外を投げる
			id = Long.parseLong(qr.substring(officeName.length()));
			if(!manager.isId(id)) throw new SQLException("登録IDが存在しません。");
			//入退室時間を取得
			Date inTime = manager.getLastInHistory(id);
			Date outTime = manager.getLastOutHistory(id);
			//現在時間を取得
			Date nowTime = new Date();
			String nowTimeString = dateFormat.format(nowTime);
			//入退室どちらで処理するか
			int state = state(inTime, outTime, nowTimeString);
			//入退室の場合
			if(state != STATE_FAST) {
				//送信先アドレス取得
				ArrayList<String> toAddress = manager.getEmail(id);
				//送信先アドレスがある場合
				if(!toAddress.isEmpty()) {
					ResultSet resultSet = manager.showMasterData();
					SendMail sendMail = new SendMail(frame, resultSet.getString("FROM_ADDRESS"), resultSet.getString("FROM_NAME"), resultSet.getString("ACCOUNT_NAME"), resultSet.getString("PASSWORD"), resultSet.getString("SMTP_SERVER"), resultSet.getString("SMTP_PORT"));							
					//入室の場合
					if(state == STATE_IN) {
						//メール送信して登録
						sendMail.send(toAddress, SendMail.subject, SendMail.inMailMessage);
						manager.addInHistory(id, nowTimeString);
						////oto
					}
					//退室の場合
					else {
						//メール送信して登録
						sendMail.send(toAddress, SendMail.subject, SendMail.outMailMessage);
						manager.addOutHistory(id, nowTimeString);
						////oto
					}
				//送信先アドレスがない場合
				} else {
				////oto
				System.out.println("noaddress");
				}				
			//前回入退室から1分も経っていない場合
			} else {
				////oto
				System.out.println("toofast");
			}
		} catch (SQLException e) {
			////oto
			System.out.println("error");
			qrReader.isWorking = false;
			FrameMain.cardLayout.show(FrameMain.cardPanel, FrameMain.OFF);
			JOptionPane.showMessageDialog(frame, e.getMessage());
		} finally {
			manager.closeAll();
		}
		
		System.out.println("again");
		//QrReaderのisWorkingがfalseになるまでもう1回！
		if(qrReader.isWorking == true) new QrReadingThread(frame).start();
	}
	
	private int state(Date inTime, Date outTime, String nowTimeString) {
		//両方nullではない場合
		if(inTime != null && outTime != null) {
			if(nowTimeString.equals(dateFormat.format(inTime)) || nowTimeString.equals(dateFormat.format(outTime))) return STATE_FAST; 
			if(inTime.before(outTime)) return STATE_IN;
			return STATE_OUT;
		//nullが含まれる場合
		} else {
			if(inTime == null) return STATE_IN;
			return STATE_OUT;
		}
	}
}
