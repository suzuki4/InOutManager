package frame;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;

import qrCode.QrReader;

public class Frame extends JFrame {
	//フィールド
	public CardLayout cardLayout;
	FrameMain frameMain;
	FrameAccount frameAccount;
	FrameHistory frameHistory;
	
	//コンストラクタ
	public Frame() throws SQLException {
		//終了時
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			// ウィンドウが閉じるときに呼ばれる
			@Override
			public void windowClosing(WindowEvent e) {
					if(QrReader.getInstance().isWorking == true) QrReader.getInstance().isWorking = false;
			}
		});
		//レイアウト
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		//タイトル
		setTitle("メイン画面");
		//各フレーム作成
		frameAccount = new FrameAccount(this);
		frameHistory = new FrameHistory(this);
		frameMain = new FrameMain(this);
		//frameMainの時計起動
		while(true) {
			frameMain.setClock();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
		

}
