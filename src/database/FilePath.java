package database;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FilePath {
	public static String getFilePath(JFrame frame) {
		String filePath = null;
	    JFileChooser filechooser = new JFileChooser();
	    int selected = filechooser.showSaveDialog(frame);
	    if (selected == JFileChooser.APPROVE_OPTION){
	    	File file = filechooser.getSelectedFile();
	    	filePath = file.getPath();
	    }
	    return filePath;
	}
}
