package persistence;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Song;
import model.SongContent;
import model.User;
import model.SongInfo;

/**
 * An instance of this class will be created when JTunes runs.
 * This instance is responsible for all interactions with the
 * JTunes cloud.
 */
public class Cloud {

	/**
	 * Getter method. Returns a list of the all songs available on the JTunes cloud server.
	 * 
	 * @param user				the user currently accessing the cloud server
	 * @return songInfoList		a List<SongInfo> object of all the songs available on the JTunes cloud server
	 */
	public static List<SongInfo> getAllSongInfos(User user) {
        List<SongInfo> songInfoList = new ArrayList<SongInfo>();

        	try {
        		URL songListUrl = new URL("http://greywolf.cdf.toronto.edu:1337/syzygy/listSongs");
        		Document doc = Adapters.parseXml(songListUrl);
        		NodeList songs = doc.getElementsByTagName("song");
        		for(int i=0;i<songs.getLength();i++){
        			String name = songs.item(i).getAttributes().item(1).getNodeValue();
        			String artist = songs.item(i).getAttributes().item(0).getNodeValue();
        			String id = songs.item(i).getTextContent().trim();
        			songInfoList.add(new SongInfo(id, name, artist));
        		};
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        return songInfoList;
	}
	/**
	 *Reads the list of available songs on the JTunes cloud server and puts their SongInfo objects and their Ids into an Map object.
	 *
	 *@param user	the User object of the account currently accessing the program
	 * @return 		a map of the songs linked to their Ids on the server
	 */
	public static Map<String,SongInfo> idSongMap(User user) {
		Map<String,SongInfo> idMap = new HashMap<String,SongInfo>();
		for (SongInfo info:getAllSongInfos(user)) {
			String id = info.getId();
			idMap.put(id, info);
		}
		return idMap;
	}
	/**
	 * Downloads a song from the cloud server.
	 * 
	 * @param user		the user currently accessing the program
	 * @param songInfo	the SongInfo object associated with the song to be downloaded
	 */
	public static void downloadSong(User user, SongInfo songInfo){

		try {
			URL url = new URL( 
					"http://greywolf.cdf.toronto.edu:1337/syzygy/getSong?email="
			        + user.getId() + "&password=" + user.getCredential()
			        + "&songid=" + songInfo.getId());
			Document doc = Adapters.parseXml(url);
			String rawData = doc.getElementsByTagName("rawdata").item(0).getTextContent();
			byte[] byteData = Adapters.decodeHex(rawData);
			File osFile = new File(String.format("data/songs/%s - %s.mid", songInfo.getName(), songInfo.getArtist()));
			osFile.createNewFile();
			OutputStream os = new BufferedOutputStream(new FileOutputStream(osFile));
			os.write(byteData);
			os.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param user	the user who is requesting the function
	 * @return 		an Iterable containing all the SongInfo of songs the user has in her library
	 *         		(In JTunes' current implementation, this would be all the songs user has downloaded)
	 * @throws 		IOException 
	 */
	public static List<SongInfo> getSongInfos(User user){
        List<SongInfo> songInfoList = new ArrayList<SongInfo>();

    	try {
    		URL songListUrl = new URL("http://greywolf.cdf.toronto.edu:1337/syzygy/listSongs"+
    				"?email="+ user.getId() +"&password=" + user.getCredential());
    		Document doc = Adapters.parseXml(songListUrl);
    		NodeList songs = doc.getElementsByTagName("song");
    		for(int i=0;i<songs.getLength();i++){
    			String id = songs.item(i).getTextContent().trim();
    			SongInfo info = idSongMap(user).get(id);
    			songInfoList.add(info);
    		};
	} catch (IOException ioe) {
		ioe.printStackTrace();
	}
    return songInfoList;
	}
	

	/**
	 * Checks if the information in user describes a valid JTunes user.
	 * 
	 * @return true	if the user with email user.getId() exists and has password user.getCredential();
	 *         		false otherwise (wrong email or password)
	 */
	public static boolean checkUserValidity(User user) {
		boolean success = false;
		try {
			URL url = new URL("http://greywolf.cdf.toronto.edu:1337/syzygy/" +
					"listSongs?email="+user.getId()+"&password="+user.getCredential());
			Document doc = Adapters.parseXml(url);
			success = Boolean.parseBoolean(
					doc.getElementsByTagName("response").item(0).getAttributes().item(0).getNodeValue());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return success;
	}
	/**
	 * Creates a new user on the JTunes cloud server using the information given to the program.
	 * 
	 * @param user	the User object created by the program from the input given
	 * @return true	if the user is successfully created,
	 * 				false if the user already exists.
	 */         
	public static boolean createUser(User user) {
		boolean success = false;
		try{
			URL url = new URL("http://greywolf.cdf.toronto.edu:1337/syzygy/" +
					"createUser?name="+user.getName()+"&email="+user.getId()+"&password="+user.getCredential());
			Document doc = Adapters.parseXml(url);
			success = Boolean.parseBoolean(
					doc.getElementsByTagName("response").item(0).getAttributes().item(0).getNodeValue());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success;
	}
	/**
	 * Contains methods to adapt information from the cloud server to be readable by the program.
	 */  
	/**
	 * Adaptor methods for carrying out conversion
	 * 
	 * @author Michael Yu
	 *
	 */
	
	private static class Adapters {

		/**
		 * Decodes the string of hexadecimal digits parsed from the XML sent from the cloud server into an array of bytes
		 * 
		 * @param s	a string parsed from the XML returned by the server of a song in the cloud
		 * @return byte[]	an array of bytes able to be written into a playable .mid file 
		 */ 
		
		public static byte[] decodeHex(String s) {
	        int len = s.length();
	        byte[] r = new byte[len / 2];
	        for (int i = 0; i < r.length; i++) {
	            int digit1 = s.charAt(i * 2), digit2 = s.charAt(i * 2 + 1);
	            if (digit1 >= '0' && digit1 <= '9')
	                digit1 -= '0';
	            else if (digit1 >= 'a' && digit1 <= 'f')
	                digit1 -= 'a' - 10;
	            if (digit2 >= '0' && digit2 <= '9')
	                digit2 -= '0';
	            else if (digit2 >= 'a' && digit2 <= 'f')
	                digit2 -= 'a' - 10;

	            r[i] = (byte) ((digit1 << 4) + digit2);
	        }
	        return r;
	    }
		/**
		 * Parses the XML received from the cloud server into commands readable by the program
		 * 
		 * @param url	the URL of the page requested by the program from the cloud server
		 * @return 		a Document object that is able to be read by the program
		 */  
		/**
		 * Parse xml file given the url
		 * @param URL	url of the XML file
		 * @return Document 	document of the parsed XML file (XML represented in tree form)
		 * 
		 */         
		private static Document parseXml(URL url){
			Document doc = null;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(url.openStream());
				doc.getDocumentElement().normalize();
			} catch(SAXException se) {
				se.printStackTrace();
				
			} catch(IOException ioe) {
				ioe.printStackTrace();
				
			} catch(ParserConfigurationException pce) {
				pce.printStackTrace();	
			}
			return doc;
		}
	}
}
	

	