package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import qrCode.QrWriter;

public final class DBManager {
	//Singleton
	private static final DBManager manager = new DBManager();
    private DBManager() {}
    public static DBManager getInstance() {
    	return manager;
    }
    
    //フィールド
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    
    //全て接続を閉じる
    public void closeAll() {
    	try {
    		if(connection != null) connection.close();
        	if(statement != null) statement.close();
        	if(resultSet != null) resultSet.close();
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    //接続
	public Connection connect() throws SQLException {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    connection = DriverManager.getConnection("jdbc:h2:data/database/data", "sa", "");
	    return connection;
	}
	
//マスター情報
	public ResultSet showMasterData() throws SQLException {
		//接続
		connection = connect();
		statement = connection.createStatement();
        //ＤＢの新規作成時はテーブルがないので作成
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS MASTER_DATA(CAMERA INTEGER, "
																	+ "OFFICE_NAME VARCHAR(255), "
																	+ "FROM_ADDRESS VARCHAR(255), "
			                    									+ "FROM_NAME VARCHAR(255), "
			                    									+ "ACCOUNT_NAME VARCHAR(255), "
			                    									+ "PASSWORD VARCHAR(255), "
			                    									+ "SMTP_SERVER VARCHAR(255), "
			                    									+ "SMTP_PORT VARCHAR(255))");
        //内容を取得
        resultSet = statement.executeQuery("SELECT * FROM MASTER_DATA");
        //内容が無い場合
        if(!resultSet.next()) {
    		statement.executeUpdate("INSERT INTO MASTER_DATA VALUES(0, 'サンプル塾・A教室', 'sample@sample.com', '入退室管理システム', 'sample', NULL, 'mail.sample', '587')");
    		resultSet = statement.executeQuery("SELECT * FROM MASTER_DATA");
    		resultSet.next();
        }
        return resultSet;
	}
	
	//OFFICE_NAMEを返す
	public String getOfficeName() throws SQLException {
		//内容を取得
        resultSet = showMasterData();
        //resultSetからStringへ
        return resultSet.getString("OFFICE_NAME");
	}
	
	//CAMERA_INTEGERを返す
	public int getCamera() throws SQLException {
		//内容を取得
        resultSet = showMasterData();
        //resultSetからintへ
        return resultSet.getInt("CAMERA");
	}
	
	//レコード編集
    public void updateMasterData(int camera, String officeName, String fromAddress, String fromName, String accountName, String password, String smtpServer, String smtpPort) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //更新
		statement.executeUpdate("UPDATE MASTER_DATA SET CAMERA = " + camera);
		statement.executeUpdate("UPDATE MASTER_DATA SET OFFICE_NAME = '" + officeName + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET FROM_ADDRESS = '" + fromAddress + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET FROM_NAME = '" + fromName + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET ACCOUNT_NAME = '" + accountName + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET PASSWORD = '" + password + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET SMTP_SERVER = '" + smtpServer + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET SMTP_PORT = '" + smtpPort + "'");	
    }
	
//メール内容情報
  	public ResultSet showMailMessageData() throws SQLException {
  		//接続
  		connection = connect();
  		statement = connection.createStatement();
          //ＤＢの新規作成時はテーブルがないので作成
          statement.executeUpdate("CREATE TABLE IF NOT EXISTS MAIL_MESSAGE_DATA(IN_SUBJECT VARCHAR(255), "
  																	+ "IN_MESSAGE VARCHAR(1023), "
  			                    									+ "OUT_SUBJECT VARCHAR(255), "
  			                    									+ "OUT_MESSAGE VARCHAR(1023))");
          //内容を取得
          resultSet = statement.executeQuery("SELECT * FROM MAIL_MESSAGE_DATA");
          //内容が無い場合
          if(!resultSet.next()) {
      		statement.executeUpdate("INSERT INTO MAIL_MESSAGE_DATA VALUES('入退室管理システム', '入室しました。', '入退室管理システム', '退室しました。')");
      		resultSet = statement.executeQuery("SELECT * FROM MAIL_MESSAGE_DATA");
      		resultSet.next();
          }
          return resultSet;
  	}

	//レコード編集
    public void updateMailMessageData(String inMailSubject, String inMailMessage, String outMailSubject, String outMailMessage) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //更新
		statement.executeUpdate("UPDATE MAIL_MESSAGE_DATA SET IN_SUBJECT = '" + inMailSubject + "'");
		statement.executeUpdate("UPDATE MAIL_MESSAGE_DATA SET IN_MESSAGE = '" + inMailMessage + "'");
		statement.executeUpdate("UPDATE MAIL_MESSAGE_DATA SET OUT_SUBJECT = '" + outMailSubject + "'");
		statement.executeUpdate("UPDATE MAIL_MESSAGE_DATA SET OUT_MESSAGE = '" + outMailMessage + "'");
    }

  	
//TableAccount
	//TableAccount表示
    public ResultSet showTableAccount() throws SQLException {
            //接続
    		connection = connect();
    		statement = connection.createStatement();
            //ＤＢの新規作成時はテーブルがないので作成
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS DATA(ID LONG PRIMARY KEY, "
			                    									+ "STUDENT_NAME VARCHAR(255), "
			                    									+ "PARENT_NAME VARCHAR(255), "
			                    									+ "STATUS1 BOOLEAN, "
			                    									+ "EMAIL1 VARCHAR(255), "
			                    									+ "STATUS2 BOOLEAN, "
			                    									+ "EMAIL2 VARCHAR(255), "
			                    									+ "STATUS3 BOOLEAN, "
			                    									+ "EMAIL3 VARCHAR(255), "
			                    									+ "QR_CODE VARCHAR(1023))");
            //内容を取得
            resultSet = statement.executeQuery("SELECT * FROM DATA");
            return resultSet;
    }
    
    //IDが存在するか
    public boolean isId(long id) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //内容を取得
        resultSet = statement.executeQuery("SELECT ID FROM DATA WHERE ID = " + id);
        return resultSet.next();
    }
    
    //csv入力
    public void readCsv(String filePath) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
		//入力
		try {
			statement.executeUpdate("BEGIN TRANSACTION");
			statement.executeUpdate("DELETE FROM DATA");
	        statement.executeUpdate("INSERT INTO DATA SELECT * FROM CSVREAD('" + filePath + "', NULL, 'SHIFT_JIS')");
			statement.executeUpdate("COMMIT");
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(new JFrame(), "CSVファイルを読み込めません。\n" + e.toString());
		} finally {
			statement.executeUpdate("ROLLBACK");
		}
		//取り込んだCSVにIDがなくなっていたら、データからそのIDの履歴を削除
		try {
			resultSet = statement.executeQuery("SELECT ID FROM IN_HISTORY WHERE ID NOT IN(SELECT ID FROM DATA)");
	        while(resultSet.next()) {
	        	connection.createStatement().executeUpdate("DELETE FROM IN_HISTORY WHERE ID = " + resultSet.getLong("ID"));
	        }
			resultSet = statement.executeQuery("SELECT ID FROM OUT_HISTORY WHERE ID NOT IN(SELECT ID FROM DATA)");
	        while(resultSet.next()) {
	        	connection.createStatement().executeUpdate("DELETE FROM OUT_HISTORY WHERE ID = " + resultSet.getLong("ID"));
	        }			
		} catch(SQLException e) {
			JOptionPane.showMessageDialog(new JFrame(), e.toString());
			e.printStackTrace();
		}
    }
    
    //csv出力
    public void writeCsv(String filePath) throws SQLException {
    	String officeName = getOfficeName();
    	//接続
		connection = connect();
		statement = connection.createStatement();
		//QRコード書き出し
		statement.executeUpdate("UPDATE DATA SET QR_CODE = '" + FilePath.getQrPath() + officeName + "' || CAST(ID AS VARCHAR(14)) || '.png'");
        //出力
        statement.executeUpdate("CALL CSVWRITE('" + filePath + "', 'SELECT * FROM DATA', 'SHIFT_JIS')");
    }
    
    //
    public void writeQr() throws SQLException {
    	QrWriter qrWriter = new QrWriter();
    	//接続
		connection = connect();
		statement = connection.createStatement();
		//QRファイル書き出し
		resultSet = statement.executeQuery("SELECT ID FROM DATA");
		while(resultSet.next()) {
			qrWriter.writeQr(resultSet.getLong("ID"));
		}
	}
    
    //レコード追加
    public void addData(long id, String studentName, String parentName, boolean status1, String email1, boolean status2, String email2, boolean status3, String email3) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //追加
		statement.executeUpdate("INSERT INTO DATA VALUES('" + id + "', '" + studentName + "', '" + parentName + "', " + (email1.equals("") ? null : status1) + ", '" + email1 + "', " + (email2.equals("") ? null : status2) + ", '" + email2 + "', " + (email3.equals("") ? null : status3) + ", '" + email3 + "', " + null + ")");
    }
    
    //レコード編集
    public void updateData(long id, String studentName, String parentName, boolean status1, String email1, boolean status2, String email2, boolean status3, String email3) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //更新
		statement.executeUpdate("UPDATE DATA SET STUDENT_NAME = '" + studentName + "' WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET PARENT_NAME = '" + parentName + "' WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET STATUS1 = " + (email1.equals("") ? null : status1) + " WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET EMAIL1 = '" + email1 + "' WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET STATUS2 = " + (email2.equals("") ? null : status2) + " WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET EMAIL2 = '" + email2 + "' WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET STATUS3 = " + (email3.equals("") ? null : status3) + " WHERE ID = " + id);
		statement.executeUpdate("UPDATE DATA SET EMAIL3 = '" + email3 + "' WHERE ID = " + id);
    }
    //レコード削除
    public void deleteData(long id) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //削除
		statement.executeUpdate("DELETE FROM DATA WHERE ID = " + id);
	}
    
    //idからメールアドレスを返す
    public ArrayList<String> getEmail(long id) throws SQLException {
        //接続
		connection = connect();
		statement = connection.createStatement();
        //内容を取得
        resultSet = statement.executeQuery("SELECT * FROM DATA WHERE ID = " + id);
        resultSet.next();
        ArrayList<String> emailList = new ArrayList<String>();
        if(resultSet.getBoolean("STATUS1")) emailList.add(resultSet.getString("EMAIL1"));
        if(resultSet.getBoolean("STATUS2")) emailList.add(resultSet.getString("EMAIL2"));
        if(resultSet.getBoolean("STATUS3")) emailList.add(resultSet.getString("EMAIL3"));
		return emailList;    
    }


//TableHistory
    //TableHistory表示
    public ResultSet showTableHistoryIn(long id) throws SQLException {
            //接続
    		connection = connect();
    		statement = connection.createStatement();
            //ＤＢの新規作成時はテーブルがないので作成
    		statement.executeUpdate("CREATE TABLE IF NOT EXISTS IN_HISTORY(ID LONG, IN_TIME SMALLDATETIME)");
            //内容を取得
            resultSet = statement.executeQuery("SELECT IN_TIME FROM IN_HISTORY WHERE ID = " + id + " ORDER BY IN_TIME");
            return resultSet;
    }
    public ResultSet showTableHistoryOut(long id) throws SQLException {
        //接続
		connection = connect();
		statement = connection.createStatement();
        //ＤＢの新規作成時はテーブルがないので作成
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS OUT_HISTORY(ID LONG, OUT_TIME SMALLDATETIME)");
        //内容を取得
        resultSet = statement.executeQuery("SELECT OUT_TIME FROM OUT_HISTORY WHERE ID = " + id + " ORDER BY OUT_TIME");
        return resultSet;
    }
    
    //最新の入退室時間を返す
    public Timestamp getLastInHistory(long id) throws SQLException {
    	Timestamp inTime = null;
    	ResultSet resultSet = showTableHistoryIn(id);
    	if(resultSet.last()) inTime = resultSet.getTimestamp("IN_TIME");
    	return inTime;
    }
    public Timestamp getLastOutHistory(long id) throws SQLException {
    	Timestamp outTime = null;
    	ResultSet resultSet = showTableHistoryOut(id);
    	if(resultSet.last()) outTime = resultSet.getTimestamp("OUT_TIME");
    	return outTime;
    }
    
    //指定日付の入退室情報を返す
    public String getInHistory(String inDate) throws SQLException {
    	//接続
    	connection = connect();
    	statement = connection.createStatement();
    	//内容を表示
    	String msg = "　入室時刻\t生徒名\n";
    	ResultSet resultSetHistory = statement.executeQuery("SELECT ID, IN_TIME FROM IN_HISTORY WHERE TRUNC(IN_TIME) = '" + inDate + "' ORDER BY IN_TIME");
    	while(resultSetHistory.next()) {
        	//inTime
            String inTime = new SimpleDateFormat("HH:mm").format(resultSetHistory.getTime("IN_TIME"));
        	//id
        	long id = resultSetHistory.getLong("ID");
        	//studentName
        	Statement statement2 = connection.createStatement();
        	ResultSet resultSetData = statement2.executeQuery("SELECT STUDENT_NAME FROM DATA WHERE ID = " + id);
            resultSetData.next();
            String studentName = resultSetData.getString("STUDENT_NAME");
        	//msg
            msg += "　" + inTime + "\t" + studentName + "\n";
        }
    	return msg;
    }
    public String getOutHistory(String outDate) throws SQLException {
    	//接続
    	connection = connect();
    	statement = connection.createStatement();
    	//内容を表示
    	String msg = "　退室時刻\t生徒名\n";
    	ResultSet resultSetHistory = statement.executeQuery("SELECT ID, OUT_TIME FROM OUT_HISTORY WHERE TRUNC(OUT_TIME) = '" + outDate + "' ORDER BY OUT_TIME");
    	while(resultSetHistory.next()) {
        	//outTime
            String outTime = new SimpleDateFormat("HH:mm").format(resultSetHistory.getTime("OUT_TIME"));
        	//id
        	long id = resultSetHistory.getLong("ID");
        	//studentName
        	Statement statement2 = connection.createStatement();
        	ResultSet resultSetData = statement2.executeQuery("SELECT STUDENT_NAME FROM DATA WHERE ID = " + id);
            resultSetData.next();
            String studentName = resultSetData.getString("STUDENT_NAME");
        	//msg
            msg += "　" + outTime + "\t" + studentName + "\n";
        }
    	return msg;
    }

    
    //レコード追加
    //入室時刻
    public void addInHistory(long id, String inTime) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //追加
		statement.executeUpdate("INSERT INTO IN_HISTORY VALUES('" + id + "', '" + inTime + "')");
    }
    //退室時刻
    public void addOutHistory(long id, String outTime) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //追加
		statement.executeUpdate("INSERT INTO OUT_HISTORY VALUES('" + id + "', '" + outTime + "')");
    }
    
    //レコード編集
    //id変更
    public void editIdHistory(long id, long exId) throws SQLException {
    	//接続
    	connection = connect();
    	statement = connection.createStatement();
    	//編集
    	statement.executeUpdate("UPDATE IN_HISTORY SET ID = " + id + " WHERE ID = " + exId);
    	statement.executeUpdate("UPDATE OUT_HISTORY SET ID = " + id + " WHERE ID = " + exId);
	}
    
    //レコード削除
    //全削除（登録情報削除の時）
    public void deleteHistory(long id) throws SQLException {
    	//接続
    	connection = connect();
    	statement = connection.createStatement();
    	//History削除
    	statement.executeUpdate("DELETE FROM IN_HISTORY WHERE ID = " + id);
    	statement.executeUpdate("DELETE FROM OUT_HISTORY WHERE ID = " + id);   
    }    
    //入室時刻
    public void deleteInHistory(long id, String inTime) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //削除
		statement.executeUpdate("DELETE FROM IN_HISTORY WHERE ID = " + id + "AND IN_TIME = '" + inTime + "'");
    }
    //退室時刻
    public void deleteOutHistory(long id, String outTime) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //削除
		statement.executeUpdate("DELETE FROM OUT_HISTORY WHERE ID = " + id + "AND OUT_TIME = '" + outTime + "'");
    }
}

