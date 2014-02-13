package frame;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import database.DBManager;
import database.TableAccount;

public class AccountNewDialog extends JDialog implements ActionListener{
	//フィールド
	private JLabel labelId = new JLabel("ID:");
	protected JTextField fieldId = new JTextField();
	private JLabel labelStudentName = new JLabel("生徒名:");
	protected JTextField fieldStudentName = new JTextField();
	private JLabel labelParentName = new JLabel("保護者名:");
	protected JTextField fieldParentName = new JTextField();
	private JLabel labelEmail1 = new JLabel("送信可否1 / Eメール1:");
	protected JComboBox comboStatus1 = new JComboBox(new Boolean[]{Boolean.FALSE, Boolean.TRUE});
	protected JTextField fieldEmail1 = new JTextField();
	private JLabel labelEmail2 = new JLabel("送信可否2 / Eメール2:");
	protected JComboBox comboStatus2 = new JComboBox(new Boolean[]{Boolean.FALSE, Boolean.TRUE});
	protected JTextField fieldEmail2 = new JTextField();
	private JLabel labelEmail3 = new JLabel("送信可否3 / Eメール3:");
	protected JComboBox comboStatus3 = new JComboBox(new Boolean[]{Boolean.FALSE, Boolean.TRUE});
	protected JTextField fieldEmail3 = new JTextField();
	private JButton buttonOk = new JButton("OK");
	private JButton buttonCancel = new JButton("キャンセル");
	public boolean completeFlag = false;
	
	//コンストラクタ
	public AccountNewDialog(Frame owner) {
		super(owner);
		setTitle("ダイアログのサンプル");
		setBounds(128, 128, 256, 512);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		// ダイアログに表示するコンポーネントを設定
		Container c = getContentPane();
		
		Box vBox = Box.createVerticalBox();
		labelId.setHorizontalAlignment(JLabel.LEFT);
		vBox.add(labelId);
		vBox.add(fieldId);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelStudentName);
		vBox.add(fieldStudentName);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelParentName);
		vBox.add(fieldParentName);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(labelEmail1);
		Panel email1 = new Panel();
		email1.setLayout(new BoxLayout(email1, BoxLayout.X_AXIS));
		email1.add(comboStatus1);
		email1.add(fieldEmail1);
		vBox.add(email1);
		vBox.add(Box.createVerticalStrut(40));
		vBox.add(labelEmail2);
		Panel email2 = new Panel();
		email2.setLayout(new BoxLayout(email2, BoxLayout.X_AXIS));
		email2.add(comboStatus2);
		email2.add(fieldEmail2);
		vBox.add(email2);
		vBox.add(Box.createVerticalStrut(40));
		vBox.add(labelEmail3);
		Panel email3 = new Panel();
		email3.setLayout(new BoxLayout(email3, BoxLayout.X_AXIS));
		email3.add(comboStatus3);
		email3.add(fieldEmail3);
		vBox.add(email3);
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
		if(e.getSource() == buttonCancel) dispose();
		//そうでなければOKなのでチェック
		else {
			String message = "";
			long id = -1;
			//id
			if(fieldId == null || fieldId.getText().equals("")) {
				message += "IDを入力してください。\n";
			} else {
				try {
					id = Long.parseLong(fieldId.getText());
				} catch (Exception e1) {
					message += "IDは14桁以内の半角数字で指定してください。\n";
				}
			}			
			//studentName
			if(fieldStudentName == null || fieldStudentName.getText().equals("")) {
				message += "生徒名を入力してください。\n";
			}
			//parentName
			if(fieldParentName == null || fieldParentName.getText().equals("")) {
				message += "保護者名を入力してください。\n";
			}
			//エラーがあるなら
			if(!message.equals("")) {
				JOptionPane.showMessageDialog(this, message);
			//エラーがないなら追加登録
			} else {
				updateDatabase(id);
			}
		}
	}
	
	//データベースに追加
	protected void updateDatabase(long id) {
		DBManager manager = DBManager.getInstance();
		//ID重複と接続確認
		try {
			manager.addData(id, fieldStudentName.getText(), fieldParentName.getText(), (boolean) comboStatus1.getSelectedItem(), fieldEmail1.getText(), (boolean) comboStatus2.getSelectedItem(), fieldEmail2.getText(), (boolean) comboStatus3.getSelectedItem(), fieldEmail3.getText());
			manager.closeAll();
			//完了メッセージ
			JOptionPane.showMessageDialog(this, "正常に登録されました。");
			//完了フラグを立てる
			completeFlag = true;
			//終了
			dispose();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, "IDが重複しているかデータベースに接続できません。");
		}
	}
	
}
