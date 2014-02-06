package frame;

import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JFrame;

public class Frame extends JFrame {
	//フィールド
	public CardLayout cardLayout;	//カードレイアウト
	public FrameMain frameMain;		//各フレーム
	public FrameAccount frameAccount;
	public FrameHistory frameHistory;
	
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
		this.frameMain = new FrameMain(this);
		this.frameAccount = new FrameAccount(this);
		this.frameHistory = new FrameHistory(this);
	}
	
		

}
