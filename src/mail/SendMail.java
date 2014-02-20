package mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import frame.FrameMain;
import qrCode.QrReader;

public class SendMail {
    // 日本語メールの場合には ISO-2022-JPがオススメ。
    // UTF-8だと受信時に文字化けしてしまうメーラが世の中には依然として存在しています。
    private static final String ENCODE = "ISO-2022-JP";
    public static final String subject = "自動メール";
    public static final String inMailMessage = "入室しました。";
    public static final String outMailMessage = "退室しました。";    
    private JFrame frame;
    private String fromAddress;
    private String fromName;
    private String accountName;
    private String password;
    private String smtpServer;
    private String smtpPort;
    

	//コンストラクタ
    public SendMail(JFrame frame, String fromAddress, String fromName, String accountName,
			String password, String smtpServer, String smtpPort) {
		super();
		this.frame = frame;
		this.fromAddress = fromAddress;
		this.fromName = fromName;
		this.accountName = accountName;
		this.password = password;
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
	}
    
    //
    public boolean send(ArrayList<String> toAddress, String subject, String msg) {
        final Properties props = new Properties();

        // 基本情報。ここでは niftyへの接続例を示します。
        props.setProperty("mail.smtp.host", smtpServer);
        props.setProperty("mail.smtp.port", smtpPort);

        // タイムアウト設定
        props.setProperty("mail.smtp.connectiontimeout", "60000");
        props.setProperty("mail.smtp.timeout", "60000");

        // 認証
        props.setProperty("mail.smtp.auth", "true");

        final Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(accountName, password);
            }
        });

        // デバッグを行います。標準出力にトレースが出ます。
        session.setDebug(true);

        // メッセージ内容の設定。
        final MimeMessage message = new MimeMessage(session);
        try {
            final Address addrFrom = new InternetAddress(fromAddress, fromName, ENCODE);//送信元アドレス、送信者の表示名、エンコード
            message.setFrom(addrFrom);

            final Address[] addrTo = new Address[3];
            for(int i = 0; i < toAddress.size(); i++) {
            	addrTo[i] = new InternetAddress(toAddress.get(i), "", ENCODE);//送信先アドレス、受信者の表示名、エンコード	
                message.addRecipient(Message.RecipientType.TO, addrTo[i]);
            }

            // メールのSubject
            message.setSubject(subject, ENCODE);

            // メール本文。setTextを用いると 自動的に[text/plain]となる。
            message.setText(msg, ENCODE);

            // 仮対策: 開始
            // setTextを呼び出した後に、ヘッダーを 7bitへと上書きします。
            // これは、一部のケータイメールが quoted-printable を処理できないことへの対策となります。
            message.setHeader("Content-Transfer-Encoding", "7bit");
            // 仮対策: 終了

            // その他の付加情報。
            //message.addHeader("X-Mailer", "blancoMail 0.1");
            message.setSentDate(new Date());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // メール送信。
        try {
            Transport.send(message);
            return true;
        } catch (AuthenticationFailedException e) {
            // 認証失敗は ここに入ります。
        	////oto
        	QrReader.getInstance().isWorking = false;
        	FrameMain.cardLayout.show(FrameMain.cardPanel, FrameMain.OFF);
        	JOptionPane.showMessageDialog(frame, "指定のユーザ名・パスワードでの認証に失敗しました。\n" + e.toString());
        } catch (MessagingException e) {
            // smtpサーバへの接続失敗は ここに入ります。
        	////oto
        	QrReader.getInstance().isWorking = false;
        	FrameMain.cardLayout.show(FrameMain.cardPanel, FrameMain.OFF);
        	JOptionPane.showMessageDialog(frame, "指定のsmtpサーバへの接続に失敗しました。\n" + e.toString());
            e.printStackTrace();
        }
        return false;
    }
}