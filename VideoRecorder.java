import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Desktop;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.net.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class VideoRecorder{
	public static void main(String args[]){
		BufferedReader br;
		BufferedWriter bw;
		HashMap<String,String> urlGameIds = new HashMap<String,String>();
		String curLine = "";
		String curURL = "";
		String curGameId = "";
		Iterator itr;
		
		//final String folderPath = "C:\\Kyle\\Videos\\";
		final String folderPath = "D:\\Kyle\\Videos\\EPL\\2016_to_2017\\";
		final String fileExtension = ".flv";
		final String urlsGameIdsFile = "C:\\Kyle\\Gambling\\DFS\\Code\\SiteParser\\ProgramFiles\\VideoRecorder\\VideoLinksGameIdstemp.txt";
		final String logFile = "C:\\Kyle\\Gambling\\DFS\\Code\\SiteParser\\ProgramFiles\\VideoRecorder\\VideoRecorderLog.txt";
		File folder;
	    File[] listOfFiles;
	    String curFileName = "";
	    File file;
	    String command;
	
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		Date date;
		String dateAsString;
		long startTime;
		long curTime;
		long finalTime;
	    
		
		try {
	        Robot robot = new Robot();
	        Desktop desktop = Desktop.getDesktop();
	        
	        bw = new BufferedWriter(new FileWriter(logFile));
	        
	        br = new BufferedReader(new FileReader(urlsGameIdsFile));
	        while((curLine = br.readLine()) != null){
		    	curURL = curLine.substring(0,curLine.indexOf(","));
		    	curGameId = curLine.substring(curLine.indexOf(",")+1);
		        urlGameIds.put(curGameId,curURL);
	        }
	        
	        itr = urlGameIds.keySet().iterator();
	        
	        while(itr.hasNext()){
		        curGameId = (String) itr.next();
		        curURL = urlGameIds.get(curGameId);
		        file = new File(folderPath + curGameId + fileExtension);
		        
		        if(!file.exists()){
			        //open up video in browser
					desktop.browse(new URL(curURL).toURI());
					
					//start recording
					robot.keyPress(KeyEvent.VK_WINDOWS);
			        robot.delay(100);
			        robot.keyPress(KeyEvent.VK_F10);
			        robot.delay(100);
			        robot.keyRelease(KeyEvent.VK_F10);
			        robot.delay(100);
			        robot.keyRelease(KeyEvent.VK_WINDOWS);
			        
			        //get current time to match up with saved video file later
			        date = new Date();
			        dateAsString = dateFormat.format(date);
			        
			        //allow page to load before expanding to full screen
					Thread.sleep(45000);
			        
					//expand video to full screen (click 5 times, once every 15 seconds)
					robot.mouseMove(827,667);
					for(int i = 0; i < 5; i++){
				        robot.mousePress(InputEvent.BUTTON1_MASK);
				        robot.mouseRelease(InputEvent.BUTTON1_MASK);
				        robot.mousePress(InputEvent.BUTTON1_MASK);
				        robot.mouseRelease(InputEvent.BUTTON1_MASK);
				        robot.mousePress(InputEvent.BUTTON1_MASK);
				        robot.mouseRelease(InputEvent.BUTTON1_MASK);
				        
				        Thread.sleep(15000);
			        }
			        
			        //minimize pip
			        //Thread.sleep(1000);
			        //robot.mouseMove(1355,105);
			        //robot.mousePress(InputEvent.BUTTON1_MASK);
			        //robot.mouseRelease(InputEvent.BUTTON1_MASK);
			        
			        //pause execution until video is done recording
			        startTime = System.currentTimeMillis();
			        finalTime = startTime + 7800000;	//this is equivalent to 2hr 10 min. Initially was 2 hrs, but added 10 min buffer so that game is
			        									//definitely recorded and also to get highlights and end of segment
			        finalTime = finalTime - 135000;	//fix to keep recording time in between games to 2hr and 10 min
			        curTime = startTime;
			        do{
				    	if(!netIsAvailable()){
							System.out.println("Lost internet at " + (curTime - startTime) / 60000.0 + " minutes into recording game " + curGameId);
							bw.write("Lost internet at " + (curTime - startTime) / 60000.0 + " minutes into recording game " + curGameId);
							bw.newLine();
						}
						Thread.sleep(5000);
						curTime = System.currentTimeMillis();
			        } while(curTime < finalTime);
			        
			        
			        //stop recording
			        robot.keyPress(KeyEvent.VK_WINDOWS);
			        robot.delay(100);
			        robot.keyPress(KeyEvent.VK_F10);
			        robot.delay(100);
			        robot.keyRelease(KeyEvent.VK_F10);
			        robot.delay(100);
			        robot.keyRelease(KeyEvent.VK_WINDOWS);
			        
			        //exit full screen
			        robot.keyPress(KeyEvent.VK_ESCAPE);
			        robot.keyRelease(KeyEvent.VK_ESCAPE);
			        
			        Thread.sleep(1000);
			        
			        //close tab that current video is in
			        robot.mouseMove(380,10);
			        robot.mousePress(InputEvent.BUTTON1_MASK);
			        robot.mouseRelease(InputEvent.BUTTON1_MASK);
			        
			        //rename video that was just recorded
			        folder = new File(folderPath);
			        listOfFiles = folder.listFiles();
			        for(int i = 0; i < listOfFiles.length; i++){
				       curFileName = listOfFiles[i].getName();
				       file = new File(folderPath + curFileName);
				       if(file.isFile()){
					       if(curFileName.contains(dateAsString)){
						       command = "cmd /c rename \"" + folderPath + curFileName + "\" \"" + curGameId + fileExtension + "\"";
						       System.out.println(command);
						       Process process = Runtime.getRuntime().exec(command);
					       }
				       }
			        }
		        }
	        }
	        
	        bw.close();
		} catch (Exception e) {
		        e.printStackTrace();
		}
		
		
	}
	
	private static boolean netIsAvailable() {                                                                                                                                                                                                 
	    try {                                                                                                                                                                                                                                 
	        final URL url = new URL("http://www.google.com");                                                                                                                                                                                 
	        final URLConnection conn = url.openConnection();                                                                                                                                                                                  
	        conn.connect();                                                                                                                                                                                                                   
	        return true;                                                                                                                                                                                                                      
	    } catch (Exception e) {                                                                                                                                                                                                   
	        return false;                                                                                                                                                                                                                     
	    }                                                                                                                                                                                                                                  
	}    
}
