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
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import qrCode.QrReader;
import qrCode.QrReadingThread;

public class FrameMain extends JFrame implements ActionListener{
	//フィールド
	private JButton buttonOn = new JButton("カメラ起動中");
	private JButton buttonOff = new JButton("カメラ休止中");
	private JButton buttonAccount = new JButton("登録情報");
	private JButton buttonSetting = new JButton("設定");
	private JTextField clockTextField = new JTextField(20);
	private JComboBox pastDataCombo = new JComboBox();
	private JTextArea databaseArea = new JTextArea(30,60);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日 'HH:mm:ss");
	private Panel panelMain = new Panel();
	private Frame frame;
	//OnOff切り替え用
	public static CardLayout cardLayout = new CardLayout();
	public static Panel cardPanel = new Panel();
	public final static String ON = "on"; 
	public final static String OFF = "off"; 
	
	//コンストラクタ
	public FrameMain(Frame frame) {
		//親フレームをフィールドへ
		this.frame = frame;
		
		//リスナー
		buttonOn.addActionListener(this);
		buttonOff.addActionListener(this);
		buttonAccount.addActionListener(this);
		buttonSetting.addActionListener(this);
		
		//パネル構成物作成
		panelMain.setLayout(new BorderLayout());
		//NORTH
			//カメラOnOff用カードパネルを作成
			cardPanel.setLayout(cardLayout);
			cardPanel.add(ON, buttonOn);
			cardPanel.add(OFF, buttonOff);
		    cardLayout.show(cardPanel, OFF);
		    //メニューパネルを作成
		    Panel menuPanel = new Panel();
		    menuPanel.setLayout(new GridLayout(1, 3));
		    menuPanel.add(cardPanel);
		    menuPanel.add(buttonAccount);
		    menuPanel.add(buttonSetting);
		    //配置
		    panelMain.add(menuPanel, BorderLayout.NORTH);
		//CENTER		    
		    //データベース検索パネルを作成
		    Panel databasePanel = new Panel();
		    databasePanel.setLayout(new BoxLayout(databasePanel, BoxLayout.X_AXIS));
		    databasePanel.add(clockTextField);
		    databasePanel.add(pastDataCombo);
		    //配置
		    panelMain.add(databasePanel, BorderLayout.CENTER);
		//SOUTH
		    //データベース表示用テキストエリアを配置
		    panelMain.add(databaseArea, BorderLayout.SOUTH);
	    
		//メインパネルをカードpanelMainに設定
		frame.add(panelMain, "panelMain");

		//カードpanelMain表示
		frame.cardLayout.show(frame.getContentPane(), "panelMain");
		frame.pack();
	    frame.setVisible(true);	
	    
	}
		
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		QrReader qrReader = QrReader.getInstance();
		if(e.getSource() == buttonOn) {
			cardLayout.show(cardPanel, OFF);
			qrReader.isWorking = false;
		} else if(qrReader.isWorking == true ) {
			JOptionPane.showMessageDialog(this, "カメラ起動中です。先に休止してください。");
		} else if(e.getSource() == buttonOff) {
			cardLayout.show(cardPanel, ON);
			qrReader.isWorking = true;
			new QrReadingThread(this).start();
		} else if(e.getSource() == buttonAccount) {
			frame.cardLayout.show(frame.getContentPane(), "panelAccount");
			frame.setTitle("登録情報画面");
			frame.pack();
		    frame.setVisible(true);	   
		} else if(e.getSource() == buttonSetting) {
			SettingDialog dialog = new SettingDialog(this);
			dialog.setModal(true);	//操作ブロック
			dialog.setVisible(true);
		}
    }

	//現在時刻設定
	public void setClock() {
		Date clockDate = Calendar.getInstance().getTime();
		String clockText = sdf.format(clockDate);
		clockTextField.setText(clockText);
	}
	
	//テキストエリアに設定
	public void setMsg(String msg) {
		databaseArea.setText(msg);
	}
	
	
}
