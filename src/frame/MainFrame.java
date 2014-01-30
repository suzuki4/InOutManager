package frame;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainFrame extends Frame implements ActionListener {
	//field
	private Button buttonStart = new Button();
	private Button buttonStop = new Button();
	private Button buttonSetting = new Button();
	private TextField clockTextField = new TextField(20);
	private Choice pastDataChoice = new Choice();
	private Button buttonFind = new Button();
	private TextArea databaseArea = new TextArea(30,60);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日 'HH:mm:ss");
	
	//constructor
	public MainFrame(String title) {
		//タイトル設定
		super(title);
		//ウィンドウを閉じる時
	    addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		System.exit(0);
	    	}
	    });
	    //レイアウト設定
	    setLayout(new BorderLayout());
	    //NORTH1
		    //メニューパネルを作成
		    Panel menuPanel = new Panel();
		    menuPanel.setLayout(new GridLayout(1, 3));
		    menuPanel.add(buttonStart);
		    menuPanel.add(buttonStop);
		    menuPanel.add(buttonSetting);
		    //配置
		    add(menuPanel, BorderLayout.NORTH);
	    
		//CENTER		    
		    //データベース検索パネルを作成
		    Panel databasePanel = new Panel();
		    databasePanel.setLayout(new FlowLayout());
		    databasePanel.add(clockTextField);
		    databasePanel.add(pastDataChoice);
		    databasePanel.add(buttonFind);
		    //配置
		    add(databasePanel, BorderLayout.CENTER);
	    
		//SOUTH
		    //データベース表示用テキストエリアを配置
		    add(databaseArea, BorderLayout.SOUTH);
	    
	    //表示
	    pack();
	    setVisible(true);
	   
	}
	
	//現在時刻設定
	public void setClock() {
		Date clockDate = Calendar.getInstance().getTime();
		String clockText = sdf.format(clockDate);
		clockTextField.setText(clockText);
	}
	
	public void setMsg(String msg) {
		databaseArea.setText(msg);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
