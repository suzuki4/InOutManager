package frame;

import java.awt.Frame;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import database.DBManager;
import database.TableAccount;

public class AccountEditDialog extends AccountNewDialog {
	//フィールド
	private long exId;
	
	//コンストラクタ
	public AccountEditDialog(Frame owner, ArrayList selectedDataList) {
		super(owner);
		exId = (long) selectedDataList.get(TableAccount.COLUMN_ID);
		fieldId.setText(String.format("%014d", (long) selectedDataList.get(TableAccount.COLUMN_ID)));
		fieldStudentName.setText((String) selectedDataList.get(TableAccount.COLUMN_STUDENT_NAME));
		fieldParentName.setText((String) selectedDataList.get(TableAccount.COLUMN_PARENT_NAME));
		comboStatus1.setSelectedItem((boolean) selectedDataList.get(TableAccount.COLUMN_STATUS1));
		fieldEmail1.setText((String) selectedDataList.get(TableAccount.COLUMN_EMAIL1));
		comboStatus2.setSelectedItem((boolean) selectedDataList.get(TableAccount.COLUMN_STATUS2));
		fieldEmail2.setText((String) selectedDataList.get(TableAccount.COLUMN_EMAIL2));
		comboStatus3.setSelectedItem((boolean) selectedDataList.get(TableAccount.COLUMN_STATUS3));
		fieldEmail3.setText((String) selectedDataList.get(TableAccount.COLUMN_EMAIL3));
	}
	
	//データベースを更新
	protected void updateDatabase(long id) {
		DBManager manager = DBManager.getInstance();
		try {
			//idに変更が無い場合
			if(exId == id) {
				manager.updateData(id, fieldStudentName.getText(), fieldParentName.getText(), (boolean) comboStatus1.getSelectedItem(), fieldEmail1.getText(), (boolean) comboStatus2.getSelectedItem(), fieldEmail2.getText(), (boolean) comboStatus3.getSelectedItem(), fieldEmail3.getText());
			//idに変更が有る場合
			} else {
				//新idで追加して、旧idを削除
				manager.addData(id, fieldStudentName.getText(), fieldParentName.getText(), (boolean) comboStatus1.getSelectedItem(), fieldEmail1.getText(), (boolean) comboStatus2.getSelectedItem(), fieldEmail2.getText(), (boolean) comboStatus3.getSelectedItem(), fieldEmail3.getText());
				manager.deleteData(exId);
			}
			manager.closeAll();
			//完了メッセージ
			JOptionPane.showMessageDialog(this, "正常に更新されました。");
			//完了フラグを立てる
			completeFlag = true;
			//終了
			dispose();
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, "IDが重複しているかデータベースに接続できません。");
		}
	}
}
