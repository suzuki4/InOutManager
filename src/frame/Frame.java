package frame;

import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JFrame;

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
		//レイアウト
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		//タイトル
		setTitle("メイン画面");
		//各フレーム作成
		frameMain = new FrameMain(this);
		frameAccount = new FrameAccount(this);
		frameHistory = new FrameHistory(this);
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
