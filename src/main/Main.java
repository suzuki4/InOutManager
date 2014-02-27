package main;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import database.DBManager;
import frame.Frame;

public class Main {

	public static void main(String[] args) throws Exception {
		try {
			DBManager manager = DBManager.getInstance();
			manager.connect();
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JFrame(), "データベースに接続できません！\n起動を中止します。");
			System.exit(0);
		}
		new Frame();
	}
}
