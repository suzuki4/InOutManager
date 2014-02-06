package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableAccount {
	//フィールド
	private final String[] COLUMN_NAMES = {"ID", "生徒名", "保護者名", "ON/OFF", "Eメール1", "ON/OFF", "Eメール2", "ON/OFF", "Eメール3"};
	private DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
	private JTable table = new JTable(tableModel);
	private JScrollPane tablePane = new JScrollPane(table);
	
	//コンストラクタ
	public TableAccount() throws SQLException {
		getData();
	}
	
	//
	public void getData() throws SQLException {
		//データベースアクセス
		DBManager manager = DBManager.getInstance();
		ResultSet resultSet = manager.showTable();
		while(resultSet.next()) {
			//行追加
			tableModel.addRow(new String[]{
											String.format("%014d", resultSet.getLong("ID")),
											resultSet.getString("STUDENT_NAME"),
											resultSet.getString("PARENT_NAME"),
											resultSet.getString("STATUS1"),
											resultSet.getString("EMAIL1"),
											resultSet.getString("STATUS2"),
											resultSet.getString("EMAIL2"),
											resultSet.getString("STATUS3"),
											resultSet.getString("EMAIL3"),
											});
		}
		//閉じる
		manager.closeAll();
	}

	//ゲッター
	public JScrollPane getTablePane() {
		return tablePane;
	}

	public void setTablePane(JScrollPane tablePane) {
		this.tablePane = tablePane;
	}
	
}
