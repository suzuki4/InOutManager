package frame;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import mail.SendMail;
import database.DBManager;
import database.TableAccount;

public class SettingDialog extends JDialog implements ActionListener{
	//フィールド
	/*public static String smtpServer = "mail.denshin-z.co.jp";
    public static String smtpPort = "587";
    public static String accountName = "infokamiikedai";
    public static String password = "Krs7jE42";
    public static String fromAddress = "infokamiikedai@denshin-z.co.jp";
    public static String fromName = "入退室管理システム";
    */
	private JLabel labelCamera = new JLabel("使用Webカメラ:");
	private JComboBox comboCamera = new JComboBox();
	private JLabel labelFromAddress = new JLabel("送信アドレス:");
	private JTextField fieldFromAddress = new JTextField();
	private JLabel labelFromName = new JLabel("表示名:");
	private JTextField fieldFromName = new JTextField();
	private JLabel labelAccountName = new JLabel("アカウント名:");
	private JTextField fieldAccountName = new JTextField();
	private JLabel labelPassword = new JLabel("パスワード:");
	private JPasswordField fieldPassword = new JPasswordField();
	private JLabel labelSmtpServer = new JLabel("SMTPサーバー:");
	private JTextField fieldSmtpServer = new JTextField();
	private JLabel labelSmtpPort = new JLabel("SMTPポート:");
	private JTextField fieldSmtpPort = new JTextField();	
	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("キャンセル");
	private JButton buttonTestMail = new JButton("テストメール");
	private JFrame frame;
	
	//コンストラクタ
	public SettingDialog(JFrame owner) {
		super(owner);
		frame = owner;
		setTitle("設定ダイアログ");
		setBounds(128, 128, 386, 512);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		//各フィールドの設定
		DBManager manager = DBManager.getInstance();
		ResultSet resultSet;
		try {
			resultSet = manager.showMasterData();
			////kamera
			fieldFromAddress.setText(resultSet.getString("FROM_ADDRESS"));
			fieldFromName.setText(resultSet.getString("FROM_NAME"));
			fieldAccountName.setText(resultSet.getString("ACCOUNT_NAME"));
			fieldPassword.setText(resultSet.getString("PASSWORD"));
			fieldSmtpServer.setText(resultSet.getString("SMTP_SERVER"));
			fieldSmtpPort.setText(resultSet.getString("SMTP_PORT"));		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//ダイアログに表示するコンポーネントを設定
		Container c = getContentPane();
		
		Box vBox = Box.createVerticalBox();
		vBox.add(labelCamera);
		vBox.add(comboCamera);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelFromAddress);
		vBox.add(fieldFromAddress);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelFromName);
		vBox.add(fieldFromName);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelAccountName);
		vBox.add(fieldAccountName);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelPassword);
		vBox.add(fieldPassword);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelSmtpServer);
		vBox.add(fieldSmtpServer);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelSmtpPort);
		vBox.add(fieldSmtpPort);
		vBox.add(Box.createVerticalStrut(10));
		Panel input = new Panel();
		input.setLayout(new GridLayout(1, 3));
		input.add(buttonOk);
		input.add(buttonCancel);
		input.add(buttonTestMail);
		vBox.add(input);
		vBox.add(Box.createVerticalStrut(10));
		
		c.add(vBox);
		
		//ボタンにリスナー登録
		buttonOk.addActionListener(this);
		buttonCancel.addActionListener(this);
		buttonTestMail.addActionListener(this);
	}
	
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		//キャンセルなら閉じる
		if(e.getSource() == buttonCancel) {
			dispose();
		//テストメールの場合
		} else if(e.getSource() == buttonTestMail) {
			SendMail sendMail = new SendMail(frame, fieldFromAddress.getText(), fieldFromName.getText(), fieldAccountName.getText(), fieldPassword.getText(), fieldSmtpServer.getText(), fieldSmtpPort.getText());
			String subject = "テストメールの送信";
			String message = "テストメールです。";
			if(sendMail.send(subject, message)) JOptionPane.showMessageDialog(this, "テストメールが送信されました。");
		//そうでなければOKなのでチェック
		} else {
			String message = "";
			if(fieldFromAddress == null || fieldFromAddress.getText().equals("")) {
				message += "送信アドレスを入力してください。\n";
			}			
			if(fieldFromName == null || fieldFromName.getText().equals("")) {
				message += "表示名を入力してください。\n";
			}
			if(fieldAccountName == null || fieldAccountName.getText().equals("")) {
				message += "アカウント名を入力してください。\n";
			}
			if(fieldPassword == null || fieldPassword.getText().equals("")) {
				message += "パスワードを入力してください。\n";
			}
			if(fieldSmtpServer == null || fieldSmtpServer.getText().equals("")) {
				message += "SMTPサーバーを入力してください。\n";
			}
			if(fieldSmtpPort == null || fieldSmtpPort.getText().equals("")) {
				message += "SMTPポートを入力してください。\n";
			}
			//エラーがあるなら
			if(!message.equals("")) {
				JOptionPane.showMessageDialog(this, message);
			//エラーがないなら追加登録
			} else {
				updateDatabase();
			}
		}
	}
	
	//データベースに追加
	protected void updateDatabase() {
		DBManager manager = DBManager.getInstance();
		//接続確認
		try {
			manager.updateMasterData(0, fieldFromAddress.getText(), fieldFromName.getText(), fieldAccountName.getText(), fieldPassword.getText(), fieldSmtpServer.getText(), fieldSmtpPort.getText());
			manager.closeAll();
			//完了メッセージ
			//JOptionPane.showMessageDialog(this, "正常に更新されました。");
			//終了
			dispose();
		} catch (SQLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "データベースに接続できません。");
		}
	}
	
}
