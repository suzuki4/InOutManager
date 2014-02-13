package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableHistory {
	//フィールド
	private final String[] COLUMN_NAMES = {"入室時刻", "退室時刻"};
	public static final int COLUMN_IN = 0;
	public static final int COLUMN_OUT = 1;
	private DefaultTableModel tableModel;
	private JTable table;
	
	//テーブルペインを返す
	public JScrollPane getTablePane(long id) throws SQLException {
		//テーブルモデルの作成
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
		//データベースアクセス
		DBManager manager = DBManager.getInstance();
		ResultSet resultSetIn = manager.showTableHistoryIn(id);
		ResultSet resultSetOut = manager.showTableHistoryOut(id);
		boolean hasMoreResultSetIn = true;
		boolean hasMoreResultSetOut = true;
		while((hasMoreResultSetIn = resultSetIn.next()) | (hasMoreResultSetOut = resultSetOut.next())) {
			//行追加
			tableModel.addRow(new String[]{
											hasMoreResultSetIn ? resultSetIn.getString("IN_TIME") : null,
											hasMoreResultSetOut ? resultSetOut.getString("OUT_TIME") : null,
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
	
	//選択列を返す
	public int getActiveColumnHistory() {
		return table.getSelectedColumn();
	}
	
	//選択セルを返す
	public String getActiveCellHistory() {
		String selectedHistory = null;
		int row = table.getSelectedRow();
		if(row != -1) selectedHistory = (String) tableModel.getValueAt(row, getActiveColumnHistory());
		return selectedHistory;
	}
		
	
}
