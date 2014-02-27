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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.qos.logback.core.pattern.color.YellowCompositeConverter;
import qrCode.Sound;
import mail.SendMail;
import database.DBManager;
import database.TableAccount;
import database.TableHistory;

public class FrameHistory extends JFrame implements ActionListener{
	//フィールド
	private final String REGULAR_EXPRESSION = "^\\d{12}$";
	private JButton buttonNew = new JButton("新規");
	private JButton buttonEdit = new JButton("編集");
	private JButton buttonDelete = new JButton("削除");
	private JButton buttonSendMail = new JButton("メール送信");
	private JButton buttonCsv = new JButton("CSV出力");
	private JButton buttonBack = new JButton("戻る");
	public Panel panelHistory = new Panel();
	private Frame frame;
	private TableHistory tableHistory;
	private JScrollPane tablePane;
	private JLabel labelStudentName = new JLabel();
	
	//コンストラクタ
	public FrameHistory(Frame frame) throws SQLException {
		//親フレームをフィールドへ
		this.frame = frame;
		
		//リスナー
		buttonNew.addActionListener(this);
		buttonEdit.addActionListener(this);
		buttonDelete.addActionListener(this);
		buttonSendMail.addActionListener(this);
		buttonCsv.addActionListener(this);
		buttonBack.addActionListener(this);
		
		//パネル構成物作成
		panelHistory.setLayout(new BoxLayout(panelHistory, BoxLayout.Y_AXIS));
		//1行
			//メニューパネルを作成
		    Panel menuPanel = new Panel();
		    menuPanel.setLayout(new GridLayout(2, 3));
		    menuPanel.add(buttonNew);
		    menuPanel.add(buttonEdit);
		    menuPanel.add(buttonDelete);
		    menuPanel.add(buttonSendMail);
		    menuPanel.add(buttonCsv);
		    menuPanel.add(buttonBack);
		    //配置
		    panelHistory.add(menuPanel);
		//2行
		    //ラベル作成
		    labelStudentName.setText(frame.frameAccount.studentName);
		    panelHistory.add(labelStudentName);
		    
		//3行
		    //テーブル作成
		    tableHistory = new TableHistory();
		    tablePane = tableHistory.getTablePane(frame.frameAccount.id);
		    //データベース表示用テキストエリアを配置
		    panelHistory.add(tablePane);
		    
		//メインパネルをカードpanelMainに設定
		frame.add(panelHistory, "panelHistory");

	}
	
	//ラベル更新
	void updateLabel(String studentName) {
		labelStudentName.setText(studentName);
	}
	
	//テーブル更新
	void tableUpdate(long id) {
		panelHistory.remove(tablePane);
		//更新
		try {
			tablePane = tableHistory.getTablePane(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		panelHistory.repaint();
		panelHistory.add(tablePane, BorderLayout.SOUTH);
		panelHistory.revalidate();
	}
	
	//アクションイベント
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonNew) {
			String selectvalues[] = {"新規入室時刻", "新規退室時刻", "キャンセル"};
			int select = JOptionPane.showOptionDialog(this,
				      "選択してください", 
				      "新規登録画面", 
				      JOptionPane.YES_NO_OPTION,
				      JOptionPane.QUESTION_MESSAGE,
				      null, 						//アイコン
				      selectvalues, 
				      selectvalues[2]				//初期位置
				    );
			//選択された場合
			if(select != 2) {
				String input;
				String initialTime = new SimpleDateFormat("yyyyMMddHHmm", Locale.JAPAN).format(new Date());
				String inOrOut = select == 0 ? "入室" : "退室";
				while(true) {
						//入力値を取得
						input = JOptionPane.showInputDialog(this,
								inOrOut + "時刻を入力してください。\n" +
										"例：2014年1月20日午前9時30分\n" +
										"　　→201401200930",
										initialTime);
						if(isDone(select, input)) break;	//入力が済んだら終了
						initialTime = input;
				}
			}
		} else if(e.getSource() == buttonEdit) {
			JOptionPane.showMessageDialog(this, "未実装。削除して新規してね。");
		} else if(e.getSource() == buttonDelete) {
			int selectedColumn = tableHistory.getActiveColumnHistory();
			String inOrOut = selectedColumn == TableHistory.COLUMN_IN ? "入室" : "退室";
			String selectedHistory = tableHistory.getActiveCellHistory();
			//セルが選択されている場合
			if(selectedColumn != -1 && selectedHistory != null) {
				String message = 	inOrOut + ": " + selectedHistory + "\n"
								+	"\n"
								+	"本当に削除しても良いですか？\n"
								+	"(削除すると復元できません)\n";
				//了解が選択された場合
				if(JOptionPane.showConfirmDialog(null, message, "確認画面" , JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					DBManager manager = DBManager.getInstance();
					try {
						if(selectedColumn == TableHistory.COLUMN_IN) manager.deleteInHistory(frame.frameAccount.id, selectedHistory);
						else manager.deleteOutHistory(frame.frameAccount.id, selectedHistory);
						manager.closeAll();
				        tableUpdate(frame.frameAccount.id);	//テーブル更新				
					} catch (SQLException e1) {
						e1.printStackTrace();
					}	
				}	
			//セルが選択されていない場合
			} else {
				JOptionPane.showMessageDialog(this, "削除するセルを選択してください。");
			}
		} else if(e.getSource() == buttonSendMail) {
			int selectedColumn = tableHistory.getActiveColumnHistory();
			String inOrOut = selectedColumn == TableHistory.COLUMN_IN ? "入室" : "退室";
			String selectedHistory = tableHistory.getActiveCellHistory();
			//セルが選択されている場合
			if(selectedColumn != -1 && selectedHistory != null) {
				String message = 	inOrOut + ": " + selectedHistory + "\n"
						+	"\n"
						+	"本当に送信しても良いですか？\n";
				//了解が選択された場合
				if(JOptionPane.showConfirmDialog(null, message, "確認画面" , JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					DBManager manager = DBManager.getInstance();
					try {
						ArrayList<String> toAddress = manager.getEmail(frame.frameAccount.id);
						//送信先アドレスがある場合
						if(!toAddress.isEmpty()) {
							SendMail sendMail = new SendMail(this);							
							sendMail.send(toAddress, selectedColumn == TableHistory.COLUMN_IN ? SendMail.IN : SendMail.OUT);
							JOptionPane.showMessageDialog(this, "送信が完了しました。");
						//送信先アドレスがない場合
						} else {
							JOptionPane.showMessageDialog(this, "送信先メールアドレスがありません。\n登録メール・送信可否を確認してください。");					
						}
						manager.closeAll();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						//メールが遅れなかった場合
						Sound sound = new Sound();
						sound.play(sound.SOUND_ERROR);
						JOptionPane.showMessageDialog(frame, e1.getMessage());
					}	
				}
			//セルが選択されていない場合
			} else {
				JOptionPane.showMessageDialog(this, "メール送信するセルを選択してください。");	
			}
		} else if(e.getSource() == buttonCsv) {
			JOptionPane.showMessageDialog(this, "未実装。コピペしてね。");
		} else if(e.getSource() == buttonBack) {
			frame.setTitle("登録情報画面");
			frame.cardLayout.show(frame.getContentPane(), "panelAccount");
			frame.pack();
		    frame.setVisible(true);	
		}
	}
	
	//メソッド：新規入退室時間
	private boolean isDone(int select, String input) {
		//入力が有る場合
		if(input != null) {
			//入力がおかしい場合
			if(!input.matches(REGULAR_EXPRESSION)) {
				JOptionPane.showMessageDialog(this, "入力値が不正です。12桁の半角数字のみを入力してください。");
				return false;
			}
			//きちんと数字12桁の場合
			input = input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8) + " " + input.substring(8, 10) + ":" + input.substring(10) + ":00";
			DBManager manager = DBManager.getInstance();
			try {
				if(select == 0)	manager.addInHistory(frame.frameAccount.id, input);	//入室の場合
				else manager.addOutHistory(frame.frameAccount.id, input);	//退室の場合
				manager.closeAll();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			tableUpdate(frame.frameAccount.id);		
		}
		return true;
	}
}
