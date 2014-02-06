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
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	
	//コンストラクタ
	public FrameAccount(Frame frame) throws SQLException {
		//親フレームをフィールドへ
		this.frame = frame;
		
		//リスナー
		buttonNew.addActionListener(this);
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
		    TableAccount tableAccount = new TableAccount();		    
		    //データベース表示用テキストエリアを配置
		    panelAccount.add(tableAccount.getTablePane());
	    
		//メインパネルをカードpanelMainに設定
		frame.add(panelAccount, "panelAccount");

	}
			
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonNew) {
			AccountNewEditDialog dialog = new AccountNewEditDialog(this);
			dialog.setModal(true);	//操作ブロック
			dialog.setVisible(true);
			
		} else if(e.getSource() == buttonHistory) {
			frame.setTitle("入退室履歴画面");
			frame.cardLayout.show(frame.getContentPane(), "panelHistory");
			frame.pack();
		    frame.setVisible(true);	
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
