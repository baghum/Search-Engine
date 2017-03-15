package model;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.LineUnavailableException;

import sound.*;

public class VoiceProcessor{
    private SynthesiserV2 synt = new SynthesiserV2("AIzaSyCmuQHi_iRBxQxmfBdHmfYyyZpvpZDuPMM");
	private javazoom.jl.player.Player  player;
	private Microphone mic = new Microphone(AudioFileFormat.Type.WAVE);
	private Recognizer recognizer = new Recognizer("en-US", "AIzaSyCmuQHi_iRBxQxmfBdHmfYyyZpvpZDuPMM");
   
    static int fileCounter =1;
    
    private String voiceResult;
    private GoogleResponse response;
	
	
	public VoiceProcessor(){
		voiceResult = "";
		response = null;
	}
	
	public String getVoiceResult(){
		return voiceResult;
	}
	
	public GoogleResponse getResponse(){
		return response;
	}
	
	public void captureAudio(){
		mic.open();
		
		try {
			mic.captureAudioToFile("C:\\Users\\andy\\" + fileCounter + ".wave");
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileCounter++;
	}
	
	public void stopCapture(){
		mic.close();
		
		try {
			 
			if(mic.getAudioFile() == null){
			  playResponse("Cant find audio file");
			  return;
			}
		
			response = recognizer.getRecognizedDataForWave(mic.getAudioFile());
			voiceResult = response.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	 public void playResponse(String text) throws IOException, JavaLayerException{
		BufferedInputStream  bis = new BufferedInputStream(synt.getMP3Data(text));
		player = new Player(bis);
		player.play();
	 }
}