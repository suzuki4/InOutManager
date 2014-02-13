package database;

import java.sql.*;

public final class DBManager {
	//Singleton
	private static final DBManager manager = new DBManager();
    private DBManager() {}
    public static DBManager getInstance() {
    	return manager;
    }
    
    //フィールド
    public Connection connection;
    public Statement statement;
    public ResultSet resultSet;
    
    //全て接続を閉じる
    public void closeAll() throws SQLException {
    	if(connection != null) connection.close();
    	if(statement != null) statement.close();
    	if(resultSet != null) resultSet.close();
    }
    
    //接続
	public Connection connect() throws SQLException {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    connection = DriverManager.getConnection("jdbc:h2:data/data", "sa", "");
	    return connection;
	}
	
//マスター情報
	public ResultSet showMasterData() throws SQLException {
		//接続
		connection = connect();
		statement = connection.createStatement();
        //ＤＢの新規作成時はテーブルがないので作成
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS MASTER_DATA(CAMERA INTEGER, "
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
    		statement.executeUpdate("INSERT INTO MASTER_DATA VALUES(0, 'sample@sample.com', '入退室管理システム', 'sample', NULL, 'mail.sample', '587')");
    		resultSet = statement.executeQuery("SELECT * FROM MASTER_DATA");
    		resultSet.next();
        }
        return resultSet;
	}
	
	//レコード編集
    public void updateMasterData(int camera, String fromAddress, String fromName, String accountName, String password, String smtpServer, String smtpPort) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //更新
		statement.executeUpdate("UPDATE MASTER_DATA SET CAMERA = " + camera);
		statement.executeUpdate("UPDATE MASTER_DATA SET FROM_ADDRESS = '" + fromAddress + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET FROM_NAME = '" + fromName + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET ACCOUNT_NAME = '" + accountName + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET PASSWORD = '" + password + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET SMTP_SERVER = '" + smtpServer + "'");
		statement.executeUpdate("UPDATE MASTER_DATA SET SMTP_PORT = '" + smtpPort + "'");	
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
			                    									+ "EMAIL3 VARCHAR(255))");
            //内容を取得
            resultSet = statement.executeQuery("SELECT * FROM DATA");
            return resultSet;
    }
    
    //csv入力
    public void readCsv(String filePath) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //入力
		try {
			statement.executeUpdate("DELETE FROM DATA");
	        statement.executeUpdate("INSERT INTO DATA SELECT * FROM CSVREAD('" + filePath + "', NULL, 'SHIFT_JIS')");
		} catch(SQLException e) {
			System.out.println("cant read");
			e.printStackTrace();
		}
    }
    
    //csv出力
    public void writeCsv(String filePath) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //出力
        statement.executeUpdate("CALL CSVWRITE('" + filePath + "', 'SELECT * FROM DATA', 'SHIFT_JIS')");
    }
    
    //レコード追加
    public void addData(long id, String studentName, String parentName, boolean status1, String email1, boolean status2, String email2, boolean status3, String email3) throws SQLException {
    	//接続
		connection = connect();
		statement = connection.createStatement();
        //追加
		statement.executeUpdate("INSERT INTO DATA VALUES('" + id + "', '" + studentName + "', '" + parentName + "', " + (email1.equals("") ? null : status1) + ", '" + email1 + "', " + (email2.equals("") ? null : status2) + ", '" + email2 + "', " + (email3.equals("") ? null : status3) + ", '" + email3 + "')");
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
    
    //レコード削除
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

