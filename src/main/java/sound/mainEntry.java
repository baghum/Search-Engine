//package sound;
//
////import recognizer.*;
//import java.util.*; 
//import java.net.*;
// 
//import com.twilio.sdk.*; 
//import com.twilio.sdk.resource.factory.*; 
//import com.twilio.sdk.resource.instance.*; 
//import com.twilio.sdk.resource.list.*; 
//
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;
//import java.io.*;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Scanner;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//
//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//
//import javax.sound.sampled.AudioFileFormat;
//import javax.sound.sampled.LineUnavailableException;
//
//import org.json.JSONException;
//
//
//
//public class mainEntry {
//	static float longitude;
//    static float latitude; 
//    static JsonReader jsonReader;
//    static JsonObject jsonObject;
//    
//    static SynthesiserV2 synt = new SynthesiserV2("AIzaSyCmuQHi_iRBxQxmfBdHmfYyyZpvpZDuPMM");
//	static javazoom.jl.player.Player  player;
//	static Microphone mic = new Microphone(AudioFileFormat.Type.WAVE);
//	static Recognizer recognizer = new Recognizer("en-US", "AIzaSyCmuQHi_iRBxQxmfBdHmfYyyZpvpZDuPMM");
//	
//	static String userName;
//	
//    static boolean hasName = false;
//    static final String WEATHER_API_KEY = "58f3af2ace64545948763a769baf347b";
//    static final String GOOGLE_API_KEY = "AIzaSyBgSfLdZCk8wxbp8ulNlJOEGT-jSHH5eqw";
//    static MessageHandler messageHandler;
//    static MusicPlayer musicPlayer;
//
//    
//    static int fileCounter =1;
//    static boolean sendingMessage = false;
//    static boolean creatingContact = false;
//    static boolean playMusic;
//    static String contactToSendMsg;
//    
//	 public static void main(String[] args) throws IOException, LineUnavailableException, JavaLayerException, JSONException {
//		Scanner sc = new Scanner(System.in);
//		int choice;
//	  
//		//welcome message
//		String message = ",welcome to Nav Eye. Please say a command.";
//		String greet = "Hello, please state your name so I can refer you with your name.";
//		playResponse(greet);
//		
//
//	    //get geographic coordinates of the user
//	    URL amazon = new URL("http://checkip.amazonaws.com");
//        URLConnection yc = amazon.openConnection();
//        BufferedReader in = new BufferedReader(new InputStreamReader(
//                                    yc.getInputStream()));
//        String ip =in.readLine();
//       
//    	
//        URL geo = new URL("http://ip-api.com/json/" + ip);
//        URLConnection connection = geo.openConnection();
//        BufferedReader read = new BufferedReader(new InputStreamReader(
//        				connection.getInputStream()));
//       System.out.println();
//         jsonReader = Json.createReader(read);
//         jsonObject = jsonReader.readObject();
//         
//          longitude = Float.parseFloat(jsonObject.getJsonNumber("lon").toString());
//          latitude = Float.parseFloat(jsonObject.getJsonNumber("lat").toString());
//         
//
//
//       
//		System.out.println("Please enter your choice: \n1: Say command\n2: Process command");
//		
//		//
//		do{
//			choice = sc.nextInt();
//			 
//			if(choice ==1){		
//			mic.open();
//				
//				mic.captureAudioToFile("C:\\Users\\Sevo\\Downloads\\install\\sound" + fileCounter + ".wave");
//				fileCounter++;
//			}
//			else if(choice ==2){
//				mic.close();
//				
//				try {
//					 GoogleResponse command;
//					if(mic.getAudioFile() == null){
//					  
//					  playResponse("Cant find audio file");
//					  return;
//					}
//				
//					command = recognizer.getRecognizedDataForWave(mic.getAudioFile());
//					
//					
//				if(hasName)
//					processCommand(command.getResponse());		
//				 else{
//					 
//					 	String tempName = command.getResponse();
//					 	if(tempName != null){
//					 		userName = tempName;
//					 		hasName = true;
//					 		userName = command.getResponse();
//					 		playResponse(userName + message);
//					 		
//						
//					 		//initialize MessagePlayer
//					 		messageHandler = new MessageHandler(userName);
//					 		messageHandler.run();
//					 		
//
//					 	}
//					 	else{
//					 		playResponse("I'm sorry, but I didn't quite get that. Please repeat your name again.");					 		
//					 	}
//				 } 
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}		
//		
//		}while(choice !=0);
//
//	
//	}
//	 
//	 public static void processCommand(String words) throws IOException, JavaLayerException, LineUnavailableException, JSONException{
//		 	//System.out.println(words);
//			if(words == null){
//				playResponse("I'm sorry, but I didn't quite get that. Please repeat that again.");
//				return;
//			}
//			//find fast food restaurants
//			else if(words.contains("fast") || words.contains("food")){
//				//System.out.println("in fast food...");
//				/****************test goole Api for food************************/
//				URL google;
//				BufferedReader foodReader;
//				
//					google = new URL("https://maps.googleapis.com/maps/api/place/"
//							+ "nearbysearch/json?key=" + GOOGLE_API_KEY
//							+  "&location=" + latitude + "," + longitude +
//							"&radius=1000&keyword=fastfood");
//					URLConnection googleConnection = google.openConnection();
//					foodReader = new BufferedReader(new InputStreamReader(googleConnection.getInputStream()));
//		       
//					jsonReader = Json.createReader(foodReader);
//			        jsonObject = jsonReader.readObject();
//			       // System.out.println("read json");
//			        foodReader.close();
//			        
//			        HashMap<Float, String>places = new HashMap<Float, String>();
//			        //System.out.println("array size is: " + jsonObject.getJsonArray("results").size());
//			        for(int i =0; i < jsonObject.getJsonArray("results").size(); ++i){
//			        	JsonObject obj = (JsonObject)jsonObject.getJsonArray("results").get(i);
//			        	String placeName = obj.getJsonString("name").toString();
//					    float placeLat = 	Float.parseFloat(obj.getJsonObject("geometry").getJsonObject("location")
//				        		.get("lat").toString());
//					    float placeLon = Float.parseFloat(obj.getJsonObject("geometry").getJsonObject("location")
//				        		.get("lng").toString());
//			        	String placeAddress = obj.getString("vicinity");
//			        	float distance = distFrom(latitude, longitude, placeLat, placeLon);
//				        
//			        	//System.out.println("before checking container...");
//			        	if(!places.containsValue(placeName)){
//			        		places.put(new Float(distFrom(latitude, longitude, placeLat, placeLon)), placeName);
//			        		
//			        		String found = "I found";
//			        		String foundAgain = "I also found";
//			        		if(i >0){
//			        			System.out.println(foundAgain + placeName 
//				        				+ (int)(distance * 20) + " minutes away located at"
//				        				+ ": " + placeAddress);
//			        			playResponse(foundAgain + placeName 
//			        				+ (int)(distance * 20) + " minutes away located at"
//			        				+ ": " + placeAddress);
//			        		}
//			        		else{
//			        			System.out.println(found + placeName 
//				        				+ (int)(distance * 20) + " minutes away located at"
//				        				+ ": " + placeAddress);
//			        			
//			        			playResponse(found + placeName 
//				        				+ (int)(distance * 20) + " minutes away located at"
//				        				+ ": " + placeAddress );
//						        		
//			        		}
//			        	}	
//			        }
//			}
//	//sending message
//			else if(sendingMessage){
//				
//				if(messageHandler.sendMessage(contactToSendMsg, words)){
//	 				playResponse(userName + ", your message was successfully sent to " + contactToSendMsg);
//	 			}
//	 			else{
//	 				playResponse(userName + "I'm sorry but I was not able to send your text to " + contactToSendMsg 
//	 							+ " at this time. Please try again.");
//	 			}
//				sendingMessage = false;
//			}
//			else if(words.contains("send") && words.contains("message")){
//		 		String [] wordArray = words.split(" ");
//		 		contactToSendMsg = wordArray[wordArray.length -1];
//		 		
//		 		//check if this is a number or contact name
//		 		String supposedNumber = contactToSendMsg.replace("-", "");
//		 		long number=0;
//		 		try{
//		 			number = Long.parseLong(supposedNumber);
//		 			
//		 		}catch(NumberFormatException e){
//		 			
//		 		}
//		 		
//		 		if(number ==0){
//		 			if(messageHandler.hasContact(contactToSendMsg)){
//		 				playResponse("Okay, please speak the text you want to send to " + contactToSendMsg);	
//		 				sendingMessage = true;
//		 			}
//		 			else{
//		 				playResponse(userName + ", you do not have " + contactToSendMsg +
//		 						" in your address book. Do you want to add " + contactToSendMsg +
//		 						" to your address book?");
//		 			//to be implemented
//		 			}
//		 		}
//		 		else{
//		 			playResponse("Okay, please speak the text you want to send to " + contactToSendMsg);	
//	 				sendingMessage = true;
//		 		}
//		 	}
//	//create new conatct
//			else if(creatingContact){
//				String newContactName = words.split(" ")[0];
//				String phoneNumber = words.split(" ")[1];
//				playResponse("Okay, adding " + newContactName + " to your address book as " + phoneNumber);
//				messageHandler.createContact(newContactName, "+1" + phoneNumber);
//				playResponse(userName + ", " + newContactName + " was successfully added to your address book.");
//				creatingContact = false;
//			}
//			else if(words.contains("create") && words.contains("contact")){
//				playResponse("Okay, let's create a new contact. \nPlease speak the name of your new contact followed by"
//						+ " their phone number.");
//				creatingContact = true;
//			}
//	//play music
//			else if(playMusic){
//				//System.out.println(words);
//				mainEntry.playResponse("Okay " + mainEntry.userName + ", let me play that for you.");
//				musicPlayer = new MusicPlayer(words);
//		 		musicPlayer.start();
//			
//				//System.out.println("out..................................");
//				playMusic = false;
//			}
//			else if(words.contains("play")){
//				
//				playResponse("Okay, let's play some music. Please speak the name of the artist followed by the song name");
//				playMusic = true;
//			}
//			else if(words.contains("stop")){
//				musicPlayer.stopMusic();
//			}
//	//tell time
//		 	else if(words.contains("time")){
//				//DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//				DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
//				Date date = new Date();
//				String time = dateFormat.format(date);
//			
//				if(time.startsWith("0")) time = time.substring(1, time.length());
//				if(time.contains(":")) time.replace(":", " ");
//				System.out.println( time);
//				playResponse("Current time is " + time);
//					
//				
//			}
//	
//	//check weather
//			else if(words.contains("weather") || words.contains("outside")){
//				 
//			       /****************Test Api for weather********************/	   
//					playResponse("Checking the weather..");
//			        URL openWeather;
//					BufferedReader reader;
//					
//
//						openWeather = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + 
//														latitude + "&lon=" + longitude
//														+ "&appid=" + WEATHER_API_KEY);
//						URLConnection openWeatherConnection = openWeather.openConnection();
//						reader = new BufferedReader(new InputStreamReader(openWeatherConnection.getInputStream()));
//
//						jsonReader = Json.createReader(reader);
//				        jsonObject = jsonReader.readObject();
//						
//				        reader.close();
//				        
//				        System.out.println(jsonObject);
//								
//				      double temp = (jsonObject.getJsonObject("main")
//								.getJsonNumber("temp").intValue() -273 )* 1.8 + 32;
//				      
//				      String condition = jsonObject.getJsonArray("weather")
//				        		.getJsonObject(0).getJsonString("description").toString();
//				      condition.replace("\"",  "");
//						System.out.println("It is " +temp  + " degrees outside," + condition);
//						
//						playResponse("It is " + (int)temp  + " " + "degrees outside." + condition);
//						
//					
//			}
//			
//			else
//				playResponse("Invalid command. Available commands are: " +
//								"check the weather, find a place, get current time, and send message");
//			
//	 }
//	 
//	 public static void playResponse(String text) throws IOException, JavaLayerException{
//		BufferedInputStream  bis = new BufferedInputStream(synt.getMP3Data(text));
//		player = new Player(bis);
//		player.play();
//	 }
//	 
//	 private static float distFrom(float lat1, float lng1, float lat2, float lng2) {
//		 
//		    double earthRadius = 3958.755866; //miles
//		    double dLat = Math.toRadians(lat2-lat1);
//		    double dLng = Math.toRadians(lng2-lng1);
//		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//		               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//		               Math.sin(dLng/2) * Math.sin(dLng/2);
//		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//		    float dist = (float) (earthRadius * c);
//
//		    return dist;
//		    }
//	 
//	
//	        
//}
//
