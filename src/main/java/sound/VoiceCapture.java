package sound;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class VoiceCapture extends Thread{
	
	protected TargetDataLine line = null; 
	public static AudioFormat soundFormat = null;
	
	public boolean stopped = false;
	public boolean readyToPlay = false; 
	private ByteArrayOutputStream stream = null;
	
	
	public VoiceCapture(){
		this.stream  = new ByteArrayOutputStream();
	}
	
	@Override
	public void run() {
		 AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
	      float rate = 16000.0f;
	      int channels = 1;
	     
	      int sampleSize = 16;
	      boolean bigEndian = false;

	      AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
	          * channels, rate, bigEndian);
	 
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
	        System.out.println("Line matching " + info + " not supported."); 
	        return;
	      }
		try {
			line = (TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
			System.out.println(line.isOpen());
			
			byte[] data = new byte [line.getBufferSize()];
			int bytesRead =0;
			
			
			line.start();
			System.out.println("Capture started...");
			while(!stopped){
				bytesRead =line.read(data, 0, data.length);
//				System.out.println("VoiceCapture: Bytes read: " + bytesRead);
				if(bytesRead ==-1){System.out.println("Stopping the capture"); break;}
				stream.write(data, 0, data.length);
			
			}
			line.drain();
			readyToPlay = true;
		//	System.out.println("Stopping the capture");
		//	System.out.println("The length of the recoreded stream is: " + stream.toByteArray().length);
			line.stop();
			line.close();
		} catch (LineUnavailableException e) {

			line.close();
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public  ByteArrayOutputStream getSound(){
		return stream;
	
	}
	

}
