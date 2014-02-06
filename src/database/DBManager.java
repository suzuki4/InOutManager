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
    
    //全て閉じる
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
	
	//テーブル取得
    public ResultSet showTable() throws SQLException {
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
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS HISTORY(ID LONG, "
            														+ "IN_DATETIME DATETIME, "
            														+ "OUT_DATETIME DATETIME)");
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
			//statement.executeUpdate("UPDATE DATA SELECT * FROM CSVREAD('" + filePath + "', NULL, 'SHIFT_JIS')");
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
    
}

