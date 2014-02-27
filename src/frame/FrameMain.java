package frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import database.DBManager;
import qrCode.QrReader;
import qrCode.QrReadingThread;

public class FrameMain extends JFrame implements ActionListener{
	//フィールド
	private final String REGULAR_EXPRESSION = "^\\d{8}$";
	private JButton buttonOn = new JButton("カメラ起動中");
	private JButton buttonOff = new JButton("カメラ休止中");
	private JButton buttonAccount = new JButton("登録情報");
	private JButton buttonSetting = new JButton("設定");
	private JButton buttonInputDate = new JButton("更新");
	private JTextField clockTextField = new JTextField(20);
	private JTextField inputDateField = new JTextField(8);
	private JTextArea databaseAreaIn = new JTextArea(30,30);
	private JTextArea databaseAreaOut = new JTextArea(30,30);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日('E') 'HH:mm:ss");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private JPanel panelMain = new JPanel();
	private Frame frame;
	//OnOff切り替え用
	public static CardLayout cardLayout = new CardLayout();
	public static JPanel cardPanel = new JPanel();
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
		buttonInputDate.addActionListener(this);
		
		//パネル構成物作成
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		//1行
			//カメラOnOff用カードパネルを作成
			cardPanel.setLayout(cardLayout);
			cardPanel.add(ON, buttonOn);
			cardPanel.add(OFF, buttonOff);
		    cardLayout.show(cardPanel, OFF);
		    //メニューパネルを作成
		    JPanel menuPanel = new JPanel();
		    menuPanel.setLayout(new GridLayout(1, 3));
		    menuPanel.add(cardPanel);
		    menuPanel.add(buttonAccount);
		    menuPanel.add(buttonSetting);
		    //配置
		    panelMain.add(menuPanel);
		//2行		    
		    //データベース検索パネルを作成
		    JPanel databasePanel = new JPanel();
		    databasePanel.setLayout(new BoxLayout(databasePanel, BoxLayout.X_AXIS));
		    databasePanel.add(clockTextField);
		    databasePanel.add(inputDateField);
		    databasePanel.add(buttonInputDate);
		    	//inputDateFieldに本日を挿入
		    	inputDateField.setText(dateFormat.format(new Date()));
		    //配置
		    panelMain.add(databasePanel);
		//3行
		    //データベース表示用テキストエリアを配置
		    JPanel databaseAreaPanel = new JPanel();
		    databaseAreaPanel.setLayout(new GridLayout(1, 2));
		    databaseAreaPanel.add(databaseAreaIn);
		    databaseAreaPanel.add(databaseAreaOut);
		    panelMain.add(databaseAreaPanel);
	    
		//メインパネルをカードpanelMainに設定
		frame.add(panelMain, "panelMain");

		//カードpanelMain表示
		frame.cardLayout.show(frame.getContentPane(), "panelMain");
		frame.pack();
	    frame.setVisible(true);	
	    
	    //テキストエリアにデータをセット
	    setMsg();
	    
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
		} else if(e.getSource() == buttonInputDate) {
			//きちんと12桁数字の場合
			if(inputDateField.getText().matches(REGULAR_EXPRESSION)) {
				setMsg();
			} else {
				JOptionPane.showMessageDialog(this, "入力値が不正です。8桁の半角数字のみを入力してください。");
			}
		}
    }

	//現在時刻設定
	public void setClock() {
		Date clockDate = Calendar.getInstance().getTime();
		String clockText = sdf.format(clockDate);
		clockTextField.setText(clockText);
	}
	
	//テキストエリアに設定
	public void setMsg() {
		String input = inputDateField.getText();
		input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8);
	    DBManager manager = DBManager.getInstance();
	    try {
			setMsgIn(manager.getInHistory(input));
			setMsgOut(manager.getOutHistory(input));
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void setMsgIn(String msg) {
		databaseAreaIn.setText(msg);
	}
	private void setMsgOut(String msg) {
		databaseAreaOut.setText(msg);
	}
	
}
