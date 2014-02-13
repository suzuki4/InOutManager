package frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import database.DBManager;
import database.FilePath;
import database.TableAccount;

public class FrameAccount extends JFrame implements ActionListener{
	//フィールド
	private JButton buttonNew = new JButton("新規");
	private JButton buttonEdit = new JButton("編集");
	private JButton buttonDelete = new JButton("削除");
	private JButton buttonHistory = new JButton("入退室履歴");
	private JButton buttonCsv = new JButton("csv入出力");
	private JButton buttonBack = new JButton("戻る");
	public Panel panelAccount = new Panel();
	private Frame frame;
	private TableAccount tableAccount;
	private JScrollPane tablePane;
	long id = -1L;
	String studentName = "";

	//コンストラクタ
	public FrameAccount(Frame frame) throws SQLException {
		//親フレームをフィールドへ
		this.frame = frame;
		
		//リスナー
		buttonNew.addActionListener(this);
		buttonEdit.addActionListener(this);
		buttonDelete.addActionListener(this);
		buttonHistory.addActionListener(this);
		buttonCsv.addActionListener(this);
		buttonBack.addActionListener(this);
		
		//パネル構成物作成
		panelAccount.setLayout(new BoxLayout(panelAccount, BoxLayout.Y_AXIS));
		//NORTH
			//メニューパネルを作成
		    Panel menuPanel = new Panel();
		    menuPanel.setLayout(new GridLayout(2, 3));
		    menuPanel.add(buttonNew);
		    menuPanel.add(buttonEdit);
		    menuPanel.add(buttonDelete);
		    menuPanel.add(buttonHistory);
		    menuPanel.add(buttonCsv);
		    menuPanel.add(buttonBack);
		    //配置
		    panelAccount.add(menuPanel);
		//SOUTH
		    //テーブル作成
		    tableAccount = new TableAccount();
		    tablePane = tableAccount.getTablePane();
		    //データベース表示用テキストエリアを配置
		    panelAccount.add(tablePane);
	    
		//メインパネルをカードpanelMainに設定
		frame.add(panelAccount, "panelAccount");

	}
	
	//テーブル更新メソッド
	private void tableUpdate() {
		panelAccount.remove(tablePane);
		//更新
		try {
			tablePane = tableAccount.getTablePane();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		panelAccount.repaint();
		panelAccount.add(tablePane);
		panelAccount.revalidate();
	}
			
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonNew) {
			AccountNewDialog dialog = new AccountNewDialog(this);
			dialog.setModal(true);	//操作ブロック
			dialog.setVisible(true);
			if(dialog.completeFlag) tableUpdate();	//新規登録が完了した場合テーブル更新
		} else if(e.getSource() == buttonEdit) {
			ArrayList selectedDataList = tableAccount.getActiveRowData();
			//行が選択されている場合
			if((long) selectedDataList.get(TableAccount.COLUMN_ID) != -1 ) {
				AccountEditDialog dialog = new AccountEditDialog(this, selectedDataList);
				dialog.setModal(true);	//操作ブロック
				dialog.setVisible(true);
				if(dialog.completeFlag) tableUpdate();	//更新が完了した場合テーブル更新
			//行が選択されていない場合
			} else {
				JOptionPane.showMessageDialog(this, "編集する行を選択してください。");
			}
		} else if(e.getSource() == buttonDelete) {
			id = (long) tableAccount.getActiveRowData().get(TableAccount.COLUMN_ID);
			//行が選択されている場合
			if(id != -1 ) {
				String message = 	"ID: " + String.format("%014d", id) + "\n"
								+	"生徒名: " + tableAccount.getActiveRowData().get(TableAccount.COLUMN_STUDENT_NAME) + "\n"
								+	"\n"
								+	"本当に削除しても良いですか？\n"
								+	"(削除すると復元できません)\n";
				//了解が選択された場合
				if(JOptionPane.showConfirmDialog(null, message, "確認画面" , JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					DBManager manager = DBManager.getInstance();
					try {
						manager.deleteData(id);
						manager.closeAll();
				        tableUpdate();	//テーブル更新				
					} catch (SQLException e1) {
						e1.printStackTrace();
					}	
				}	
			//行が選択されていない場合
			} else {
				JOptionPane.showMessageDialog(this, "削除する行を選択してください。");
			}
		} else if(e.getSource() == buttonHistory) {
			//行が選択されている場合
			id = (long) tableAccount.getActiveRowData().get(TableAccount.COLUMN_ID);
			if(id != -1 ) {	
				//ラベルの更新
				studentName = (String) tableAccount.getActiveRowData().get(TableAccount.COLUMN_STUDENT_NAME);
				frame.frameHistory.updateLabel("生徒名: " + studentName);
				//テーブルの更新
				frame.frameHistory.tableUpdate(id);
				//画面遷移
				frame.setTitle("入退室履歴画面");
				frame.cardLayout.show(frame.getContentPane(), "panelHistory");
				frame.pack();
			    frame.setVisible(true);				//行が選択されていない場合
			} else {
				JOptionPane.showMessageDialog(this, "履歴画面を表示する生徒を選択してください。");
			}
		} else if(e.getSource() == buttonCsv) {
			String selectvalues[] = {"CSV入力", "CSV出力", "キャンセル"};
			int select = JOptionPane.showOptionDialog(this,
				      "選択してください", 
				      "CSV入出力画面", 
				      JOptionPane.YES_NO_OPTION,
				      JOptionPane.QUESTION_MESSAGE,
				      null, 						//アイコン
				      selectvalues, 
				      selectvalues[2]				//初期位置
				    );
			//CSV用のパス
			String filePath = null;
				//CSV入力なら
				if(select == 0 && (filePath = FilePath.getFilePath(this)) != null) {
					DBManager manager = DBManager.getInstance();
			        try {
						manager.readCsv(filePath);
				        manager.closeAll();
				        tableUpdate();	//テーブル更新
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				//CSV出力なら
				else if(select == 1 && (filePath = FilePath.getFilePath(this)) != null){
			        DBManager manager = DBManager.getInstance();
			        try {
						manager.writeCsv(filePath);
				        manager.closeAll();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
		} else if(e.getSource() == buttonBack) {
			frame.setTitle("メイン画面");
			frame.cardLayout.show(frame.getContentPane(), "panelMain");
			frame.pack();
		    frame.setVisible(true);	
		}
    }
	
}
