package frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FrameHistory extends JFrame implements ActionListener{
	//フィールド
	private JButton buttonNew = new JButton("新規");
	private JButton buttonEdit = new JButton("編集");
	private JButton buttonDelete = new JButton("削除");
	private JButton buttonSendMail = new JButton("メール送信");
	private JButton buttonCsv = new JButton("csv出力");
	private JButton buttonBack = new JButton("戻る");
	public Panel panelHistory = new Panel();
	private Frame frame;
	
	//コンストラクタ
	public FrameHistory(Frame frame) {
		//親フレームをフィールドへ
		this.frame = frame;
		
		//リスナー
		buttonBack.addActionListener(this);
		
		//パネル構成物作成
		panelHistory.setLayout(new BorderLayout());
		//NORTH
			//メニューパネルを作成
		    Panel menuPanel = new Panel();
		    menuPanel.setLayout(new GridLayout(2, 3));
		    menuPanel.add(buttonNew);
		    menuPanel.add(buttonEdit);
		    menuPanel.add(buttonDelete);
		    menuPanel.add(buttonSendMail);
		    menuPanel.add(buttonCsv);
		    menuPanel.add(buttonBack);
		    //配置
		    panelHistory.add(menuPanel, BorderLayout.NORTH);
		//SOUTH
		    //データベース表示用テキストエリアを配置
		    //panelHistory.add(databaseArea, BorderLayout.SOUTH);
	    
		//メインパネルをカードpanelMainに設定
		frame.add(panelHistory, "panelHistory");

		//カードpanelMain表示
//		frame.cardLayout.show(frame.getContentPane(), "panelHistory");
//		frame.pack();
//	    frame.setVisible(true);	
	}
	
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonBack) {
			frame.setTitle("登録情報画面");
			frame.cardLayout.show(frame.getContentPane(), "panelAccount");
			frame.pack();
		    frame.setVisible(true);	
		}
    }
	
}
