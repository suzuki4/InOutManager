package database;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FilePath {
	public static String getFilePath(JFrame frame) {
		String filePath = null;
		String path = new File("data/csv").getAbsolutePath();
		JFileChooser filechooser = new JFileChooser(path);
	    int selected = filechooser.showSaveDialog(frame);
	    if (selected == JFileChooser.APPROVE_OPTION){
	    	File file = filechooser.getSelectedFile();
	    	filePath = file.getPath();
	    }
	    return filePath;
	}
	
	public static String getQrPath() {
		String filePath = null;
		String path = new File("data/qr").getAbsolutePath();
		filePath = path + File.separator;
		filePath = filePath.replaceAll(File.separator + File.separator, File.separator + File.separator + File.separator + File.separator);
		return filePath;
	}
}
