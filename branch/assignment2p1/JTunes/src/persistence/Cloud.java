package persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import controller.User;
import model.SongInfo;

/**
 * An instance of this class will be created when JTunes runs.
 * This instance is responsible for all interactions with the
 * JTunes cloud.
 *
 */
public class Cloud {
	/**
	 * Do initiation stuff (which you might not need to do).
	 */
	void init() {
		
	}
	
	/**
	 * 
	 * @param URL the url of the song list that info will be taken from
	 * 
	 * @return an Iterable containing all the SongInfo of songs the JTunes cloud offers to user
	 * @throws IOException 
	 */
	private static List<SongInfo> xmlSongInfoGenerator(URL songListUrl) throws SAXException, IOException, ParserConfigurationException{
		List<SongInfo> songInfoList = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(songListUrl.openStream());
		doc.getDocumentElement().normalize();
		NodeList songs = doc.getElementsByTagName("song");
		songInfoList = new ArrayList<SongInfo>();
		for(int i=0;i<songs.getLength();i++){
			String name = songs.item(i).getAttributes().item(1).getNodeValue();
			String artist = songs.item(i).getAttributes().item(0).getNodeValue();
			String id = songs.item(i).getTextContent().trim();
			songInfoList.add(new SongInfo(id, name, artist));
		}
		return songInfoList;
	}
	/**
	 * 
	 * @param URL the url of the song list that info will be taken from
	 * 
	 * @return an Iterable containing all the SongInfo of songs the JTunes cloud offers to user
	 * @throws IOException 
	 */
	private List<SongInfo> songInfoGenerator(URL songListUrl) throws IOException{
	    BufferedReader in = new BufferedReader(
	    new InputStreamReader(songListUrl.openStream()));
	    String inputLine;
	    List<SongInfo> songInfoList = new ArrayList<SongInfo>();
	    
	    while ((inputLine = in.readLine()) != null){
	    	String songId = inputLine;
	    	String artist = in.readLine();
	    	String name = in.readLine();
	    	songInfoList.add(new SongInfo(songId, name, artist));
	    	in.readLine();
	    }
		return songInfoList;
	}
	/**
	 * 
	 * @return an Iterable containing all the SongInfo of songs the JTunes cloud offers to user
	 * @throws IOException 
	 */
	Iterable<SongInfo> getAllSongInfos(User user) throws IOException {
        URL songUrl;
        List<SongInfo> songInfoList = null;

        	try {
        		songUrl = new URL("http://greywolf.cdf.toronto.edu:1337/syzygy/listSongs");
				songInfoList = xmlSongInfoGenerator(songUrl);
		} catch (SAXException sae) {
			// TODO Auto-generated catch block
			sae.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// TODO Auto-generated catch block
			pce.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
        return songInfoList;
	}
	/**
	 * 
	 * @param user the user who is requesting this function
	 * @param songInfo tells what song to download from cloud
	 * @return an InputStream whose content is the song file
	 */
	InputStream getSongContentStream(User user, SongInfo songInfo) {
		URL midiUrl = null;
		InputStream songContent = null;
		try {
			midiUrl = new URL("http://greywolf.cdf.toronto.edu:1359/syzygy/getSong"+"?email="+user.getId()+"&password="+user.getCredential()+"&songid="+songInfo.getId());
			songContent = midiUrl.openStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return songContent;
	}
	/**
	 * 
	 * @param user the user who is requesting the function
	 * @return an Iterable containing all the SongInfo of songs the user has in her library
	 *         (In JTunes' current implementation, this would be all the songs user has downloaded)
	 * @throws IOException 
	 */
	Iterable<SongInfo> getSongInfos(User user){
        URL songUrl;
        List<SongInfo> songInfoList = null;

    	try {
    		songUrl = new URL("http://greywolf.cdf.toronto.edu:1359/syzygy/listSongs"+"?email="+ user.getId() +"&password=" + user.getCredential());
			songInfoList = xmlSongInfoGenerator(songUrl);
		} catch (SAXException sae) {
			// TODO Auto-generated catch block
			sae.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// TODO Auto-generated catch block
			pce.printStackTrace();
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		}
        return songInfoList;
	}

	/**
	 * Checks if the information in user describes a valid JTunes user.
	 * TODO: use an enum for the return and change other affected methods
	 * @return 0 if the user with email user.getId() exists and has password user.getCredential();
	 *         1 otherwise (wrong email or password)
	 */
	public int checkUserValidity(User user) {
		try{
			URL link = new URL("http://greywolf.cdf.toronto.edu:1359/syzygy/" +
					"listSongs?email="+user.getId()+"&password="+user.getCredential());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(link.openStream()));    
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			return 1;
		}
		
		return 0;
	}
	/**
	 * Create a new user with name user.getName(), email user.getId(), password user.getCredential()
	 * on the JTunes cloud website.
	 * TODO: use an enum for the return and change other affected methods
	 * @param user
	 * @return 0 if the user is successfully created.
	 *         1 if the user already exists.
	 */         
	public int createUser(User user) {
		try{
			URL link = new URL("http://greywolf.cdf.toronto.edu:1359/syzygy/" +
					"createUser?name="+user.getName()+"&email="+user.getId()+"&password="+user.getCredential());
		    URLConnection myURLConnection = link.openConnection();
		    myURLConnection.connect();
		    BufferedReader in = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
		    in.readLine();
			}
		catch(Exception e){
			// TODO Auto-generated catch block
			return 1;
		}
		
		return 0;
	}
}

	