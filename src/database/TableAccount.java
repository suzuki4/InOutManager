package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableAccount {
	//フィールド
	private final String[] COLUMN_NAMES = {"ID", "生徒名", "保護者名", "送信可否1", "Eメール1", "送信可否2", "Eメール2", "送信可否3", "Eメール3"};
	public static final int COLUMN_ID = 0;
	public static final int COLUMN_STUDENT_NAME = 1;
	public static final int COLUMN_PARENT_NAME = 2;
	public static final int COLUMN_STATUS1 = 3;
	public static final int COLUMN_EMAIL1 = 4;
	public static final int COLUMN_STATUS2 = 5;
	public static final int COLUMN_EMAIL2 = 6;
	public static final int COLUMN_STATUS3 = 7;
	public static final int COLUMN_EMAIL3 = 8;	
	private DefaultTableModel tableModel;
	private JTable table;
	
	//テーブルペインを返す
	public JScrollPane getTablePane() throws SQLException {
		//テーブルモデルの作成
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
		//データベースアクセス
		DBManager manager = DBManager.getInstance();
		ResultSet resultSet = manager.showTableAccount();
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
		//JTableの作成
		table = new JTable(tableModel);
		//編集の禁止
		table.setDefaultEditor(Object.class, null);
		//列の入れ替え禁止
		//table.getTableHeader().setReorderingAllowed(false);
		//列幅の調整
		table.getColumnModel().getColumn(0).setPreferredWidth(120);
		//JScrollPaneの作成
		JScrollPane tablePane = new JScrollPane(table);
		return tablePane;
	}

	//選択行を返す
	public ArrayList getActiveRowData() {
		ArrayList selectedDataList = new ArrayList();
		int row = table.getSelectedRow();
		if(row != -1) {
			selectedDataList.add(Long.parseLong((String) tableModel.getValueAt(row, COLUMN_ID)));
			selectedDataList.add((String) tableModel.getValueAt(row, COLUMN_STUDENT_NAME));
			selectedDataList.add((String) tableModel.getValueAt(row, COLUMN_PARENT_NAME));
			selectedDataList.add(Boolean.parseBoolean((String) tableModel.getValueAt(row, COLUMN_STATUS1)));
			selectedDataList.add((String) tableModel.getValueAt(row, COLUMN_EMAIL1));
			selectedDataList.add(Boolean.parseBoolean((String) tableModel.getValueAt(row, COLUMN_STATUS2)));
			selectedDataList.add((String) tableModel.getValueAt(row, COLUMN_EMAIL2));
			selectedDataList.add(Boolean.parseBoolean((String) tableModel.getValueAt(row, COLUMN_STATUS3)));
			selectedDataList.add((String) tableModel.getValueAt(row, COLUMN_EMAIL3));
		//選択されていなければid=-1、studentName=""のみaddして返す
		} else {
			selectedDataList.add(-1L);
		}
		return selectedDataList;
	}
}
