package qrCode;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;

public class Sound {
	
	public final String SOUND_IN = "data/sound/in.wav";
	public final String SOUND_OUT = "data/sound/out.wav";
	public final String SOUND_TOO_FAST = "data/sound/tooFast.wav";
	public final String SOUND_ERROR = "data/sound/error.wav";
	
	public void play(String path) {
		AudioClip clip;
		try {
			clip = Applet.newAudioClip(new File(path).toURL());
			clip.play();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
}
