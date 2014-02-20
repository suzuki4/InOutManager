package qrCode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import database.DBManager;

public class QrWriter {
	//フィールド
	private String officeName = null;
	
	//コンストラクタ
	public QrWriter() {
		DBManager manager = DBManager.getInstance();
		try {
			officeName = manager.getOfficeName();
			manager.closeAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void writeQr(long id) {
        // エンコードのための付加情報を設定
        Hashtable<EncodeHintType, Object> encodeHint = new Hashtable<EncodeHintType, Object>();
        encodeHint.put(EncodeHintType.CHARACTER_SET, "shift_jis");
        encodeHint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        // QRコード用の出力ストリームを作成
        Writer writer = new QRCodeWriter();
        try {
            // エンコードを実行
            BitMatrix bitData = writer.encode(officeName + id, BarcodeFormat.QR_CODE, 129, 129, encodeHint);
            // ファイルに出力
            FileOutputStream output = new FileOutputStream("data/qr/" + officeName + id + ".png");
            MatrixToImageWriter.writeToStream(bitData, "png", output);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
    }

	/*public void writeQr(ArrayList<Long> ids) {
		for(int i = 0; i < ids.size(); i++) {
			writeQr(ids.get(i));
		}
	}
	*/
	
}
