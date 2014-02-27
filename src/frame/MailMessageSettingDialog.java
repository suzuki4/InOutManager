package frame;

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
import javax.swing.JTextArea;
import javax.swing.JTextField;

import qrCode.QrReader;
import mail.SendMail;
import database.DBManager;
import database.TableAccount;

public class MailMessageSettingDialog extends JDialog implements ActionListener{
	//フィールド
	private JLabel labelInSubject = new JLabel("入室時・件名:");
	private JTextField fieldInSubject = new JTextField();
	private JLabel labelInMessage = new JLabel("入室時・本文：");
	private JTextArea areaInMessage = new JTextArea(10, 0);
	private JLabel labelOutSubject = new JLabel("退室時・件名:");
	private JTextField fieldOutSubject = new JTextField();
	private JLabel labelOutMessage = new JLabel("退室時・本文：");
	private JTextArea areaOutMessage = new JTextArea(10, 0);
	
	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("キャンセル");
	private JFrame frame;
	
	//コンストラクタ
	public MailMessageSettingDialog(JFrame owner) {
		super(owner);
		frame = owner;
		setTitle("設定ダイアログ");
		setBounds(512, 64, 386, 512);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		//各フィールドの設定
		DBManager manager = DBManager.getInstance();
		ResultSet resultSet;
		try {
			resultSet = manager.showMailMessageData();
			fieldInSubject.setText(resultSet.getString("IN_SUBJECT"));
			areaInMessage.setText(resultSet.getString("IN_MESSAGE"));
			fieldOutSubject.setText(resultSet.getString("OUT_SUBJECT"));
			areaOutMessage.setText(resultSet.getString("OUT_MESSAGE"));
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//ダイアログに表示するコンポーネントを設定
		Container c = getContentPane();
		
		Box vBox = Box.createVerticalBox();
		vBox.add(labelInSubject);
		vBox.add(fieldInSubject);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelInMessage);
		vBox.add(areaInMessage);
		vBox.add(Box.createVerticalStrut(20));
		vBox.add(labelOutSubject);
		vBox.add(fieldOutSubject);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelOutMessage);
		vBox.add(areaOutMessage);
		vBox.add(Box.createVerticalStrut(10));
		Panel input = new Panel();
		input.setLayout(new GridLayout(1, 2));
		input.add(buttonOk);
		input.add(buttonCancel);
		vBox.add(input);
		vBox.add(Box.createVerticalStrut(10));
		
		c.add(vBox);
		
		//ボタンにリスナー登録
		buttonOk.addActionListener(this);
		buttonCancel.addActionListener(this);
	}
	
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		//キャンセルなら閉じる
		if(e.getSource() == buttonCancel) {
			dispose();
		//そうでなければOK
		} else {
			updateDatabase();
		}
	}
	
	//データベースに追加
	protected void updateDatabase() {
		DBManager manager = DBManager.getInstance();
		//接続確認
		try {
			manager.updateMailMessageData(fieldInSubject.getText(), areaInMessage.getText(), fieldOutSubject.getText(), areaOutMessage.getText());
			manager.closeAll();
			//終了
			dispose();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, "データベースに接続できません。\n" + e1.toString());
			e1.printStackTrace();
		}
	}
	
}
