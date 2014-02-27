package frame;

import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.JobAttributes;
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
import javax.swing.SwingConstants;

import qrCode.QrReader;
import mail.SendMail;
import database.DBManager;
import database.TableAccount;

public class SettingDialog extends JDialog implements ActionListener{
	//フィールド
	private JLabel labelCamera = new JLabel("使用Webカメラ:");
	private JComboBox comboCamera = new JComboBox(QrReader.getInstance().getWebcamNames());
	private JLabel labelOfficeName = new JLabel("教室名：");
	private JTextField fieldOfficeName = new JTextField();
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
	private JButton buttonMailMessage = new JButton("メール文設定");
	private JButton buttonTestMail = new JButton("テストメール送信");
	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("キャンセル");
	private JFrame frame;
	private String exFieldOfficeName;
	
	//コンストラクタ
	public SettingDialog(JFrame owner) {
		super(owner);
		frame = owner;
		setTitle("設定ダイアログ");
		setBounds(128, 64, 386, 640);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		//各フィールドの設定
		DBManager manager = DBManager.getInstance();
		ResultSet resultSet;
		try {
			resultSet = manager.showMasterData();
				//カメラ番号が接続カメラ数を超えていたらカメラ番号0で対応
				int cameraNumber = resultSet.getInt("CAMERA");
				if(cameraNumber >= QrReader.getInstance().getWebcamNames().length) cameraNumber = 0;
			comboCamera.setSelectedIndex(cameraNumber);
			fieldOfficeName.setText(resultSet.getString("OFFICE_NAME"));
			exFieldOfficeName = fieldOfficeName.getText();
			fieldFromAddress.setText(resultSet.getString("FROM_ADDRESS"));
			fieldFromName.setText(resultSet.getString("FROM_NAME"));
			fieldAccountName.setText(resultSet.getString("ACCOUNT_NAME"));
			fieldPassword.setText(resultSet.getString("PASSWORD"));
			fieldSmtpServer.setText(resultSet.getString("SMTP_SERVER"));
			fieldSmtpPort.setText(resultSet.getString("SMTP_PORT"));		
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//ダイアログに表示するコンポーネントを設定
		Container c = getContentPane();
		
		Box vBox = Box.createVerticalBox();
		vBox.add(labelCamera);
		vBox.add(comboCamera);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelOfficeName);
		vBox.add(fieldOfficeName);
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
		input.setLayout(new GridLayout(2, 2));
		input.add(buttonMailMessage);
		input.add(buttonTestMail);
		input.add(buttonOk);
		input.add(buttonCancel);
		vBox.add(input);
		vBox.add(Box.createVerticalStrut(10));
		
		c.add(vBox);
		
		//ボタンにリスナー登録
		buttonMailMessage.addActionListener(this);
		buttonTestMail.addActionListener(this);
		buttonOk.addActionListener(this);
		buttonCancel.addActionListener(this);
	}
	
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		//キャンセルなら閉じる
		if(e.getSource() == buttonCancel) {
			dispose();
		//メール文設定の場合
		} else if(e.getSource() == buttonMailMessage) {
			MailMessageSettingDialog dialog = new MailMessageSettingDialog(frame);
			dialog.setModal(true);	//操作ブロック
			dialog.setVisible(true);
		//テストメールの場合
		} else if(e.getSource() == buttonTestMail) {
			SendMail sendMail;
			try {
				sendMail = new SendMail(frame);
				ArrayList<String> toAddress = new ArrayList<String>();
				toAddress.add(fieldFromAddress.getText());
				if(sendMail.send(toAddress, SendMail.IN)) JOptionPane.showMessageDialog(this, "入室メールを送信名アドレスに送信しました。");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, e1.toString());				
			}
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
			//エラーがない場合
			} else {
				int option = JOptionPane.YES_OPTION;
				//教室名が変更されている場合
				if(!fieldOfficeName.getText().equals(exFieldOfficeName)) {
					option = JOptionPane.showConfirmDialog(this, "教室名を変更した場合、全QRコードが書き換わります。\n更新して良いですか？", "最終確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				}
				//教室名が変更無し、または、教室名変更がOKの場合
				if(option == JOptionPane.YES_OPTION) {
					updateDatabase();
				}
			}
		}
	}
	
	//データベースに追加
	protected void updateDatabase() {
		DBManager manager = DBManager.getInstance();
		//接続確認
		try {
			manager.updateMasterData(comboCamera.getSelectedIndex(), fieldOfficeName.getText(), fieldFromAddress.getText(), fieldFromName.getText(), fieldAccountName.getText(), fieldPassword.getText(), fieldSmtpServer.getText(), fieldSmtpPort.getText());
			manager.closeAll();
			//完了メッセージ
			//JOptionPane.showMessageDialog(this, "正常に更新されました。");
			//終了
			dispose();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, "データベースに接続できません。\n" + e1.toString());
		}
	}
	
}
