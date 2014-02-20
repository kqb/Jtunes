package persistence;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import model.Library;
import model.Song;
import model.SongContent;
import model.SongInfo;
import model.User;

/**
 * Responsible for: 
 * 1. Creating playlist file for a specific user
 * 2. Storing the contents of a playlist file locally
 * 3. Reading contents of a playlist files stored at the directory data/users/'email'/
 * 4. Adding songs to a playlist
 * 5. Deleting songs from a playlist
 * 6. Deleting a playlist file
 * @author Eva Kung/Senisa Soenardjo
 *
 */
public class Playlist {

	//members
	private User user; 
	private String userEmail;
	private HashMap<List<String>,Song> myPlaylist; //Number of songs in playlist constantly varies , must be mutable
	private String playlistName = "Playlist"; //default name for all playlists


	//constructors
	/**
	 * Create a new Playlist object for stored Playlist files
	 * 
	 * Assigns values to local variables user, userEmail and myPlaylist.
	 * 
	 * @param file Playlist(.txt format) file
	 */
	public Playlist(File file) {
		this.myPlaylist = new HashMap<List<String>,Song>();
		parsePlaylist(file);
	}

	/**
	 * Create a new Playlist object for user of corresponding email.
	 * 
	 * Assigns values to local variables user, userEmail and myPlaylist.
	 * 
	 * @param email		the email of the user who owns this playlist
	 */
	public Playlist(String email){
		this.user = new User(email);
		this.userEmail = email;
		this.myPlaylist = new HashMap<List<String>,Song>();
	}
	

	/**
	 * Create Playlist Object for user of corresponding email and preference of playlist name
	 * 
	 * @param email			the email of the user who owns this playlist
	 * @param playlistName	the name that will be assigned to the new playlist
	 */

	public Playlist(String email, String playlistName){

		this.user = new User(email);
		this.userEmail = email;
		this.myPlaylist = new HashMap<List<String>,Song>();
		this.playlistName = playlistName;
	}
	//setters and getters
	/**
	 * Getter method to retrieve myPlaylist
	 * 
	 * @return HashMap<List<String>,Song>	returns a map of List<String>, Songs
	 */
	public HashMap<List<String>,Song> getPlaylist(){
		return myPlaylist;

	}
	/**
	 * Getter method returns the name of the playlist.
	 * 
	 * @return string	returns the name of the playlist
	 */
	public String getPlaylistName(){
		return playlistName;

	}
	/**
	 * Getter method returns the email of the user who owns this paylist.
	 * 
	 * @return string	returns the user who owns this playlist
	 */
	public String getPlaylistUser(){
		return userEmail;
	}
	/**
	 * Deletes the locally stored files for the playlist if they exist then renames the playlistName and stores the playlist locally under the new name.
	 * 
	 * @param song 		the Song class file that is to be added to the arrayList
	 */
	public void setPlaylistName(String name){
		File oldPlaylist = new File("data/users/" + userEmail +"/" + playlistName + ".txt");
		oldPlaylist.delete();
		playlistName = name;
		storePlaylist();
	}

	/**
	 * Stores the playlist into the specified local directory
	 * 
	 * @param playlist		constructor object
	 * @return true			if playlist is successfully stored, otherwise false if playlist is not stored or errors are thrown
	 */
	public boolean storePlaylist(){
		File dir = new File("data/users/" + userEmail); // Create File to story user directory
		String filename = playlistName;
		File tagFile = new File(dir,filename + ".txt"); // Create File to store playlist directory
		try{
			if (!dir.exists()){ //if does not exist
				boolean created = dir.mkdir();
				if (created){ //if the directory is created
					if(!tagFile.exists()){ // Check to see if the playlist already exists
						tagFile.createNewFile(); // If it doesn't, create it. otherwise do nothing?
						writePlaylist();
						return true;
					} 
				} 
			}
			else {
				if(!tagFile.exists())
				tagFile.createNewFile();
				writePlaylist();
				return false; //since storing playlist failed !! Should it still return false if it successfully updates the .txt file? - Senisa
			}

		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return false;

	}

	/**
	 * Writes the playlist into a .txt file that is stored locally
	 * 
	 * @param Playlist		constructor object
	 * @throws Exception 	thrown if an input/output error occurs
	 */	
	public void writePlaylist() {
		File dir = new File("data/users/" + userEmail); // Create File to story user directory
		String filename = playlistName;
		File tagFile = new File(dir,filename + ".txt"); // Create File to store playlist directory
		if (myPlaylist.size() > 0) {
			try {
				FileWriter outFile = new FileWriter(tagFile);
				PrintWriter out = new PrintWriter(outFile);
				Set<List<String>> attributes = myPlaylist.keySet();
				for (List<String> info : attributes) {
					out.println(info.get(0) + " - " + info.get(1));
				}
				out.close();
			} catch (IOException e){
				e.printStackTrace();
			} 

		}
	}
	
	/**
	 * Check whether a song exist given the SongInfo
	 * 
	 * @param SongInfo		
	 * @return boolean true if Song exists, false otherwise
	 */	
	public boolean hasSong(SongInfo info) {
		List<String> key = new ArrayList<String>();
		key.add(info.getName());
		key.add(info.getArtist());
		return myPlaylist.containsKey(key);
	}
	
	/**
	 * Reading an existing playlist .txt file and initialize a Playlist object
	 * 
	 * 
	 * @param File		constructor object 
	 */	
	public void parsePlaylist(File file){
		playlistName = file.getName().split("\\.")[0];
		userEmail = file.getParentFile().getName();
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String readLine;
			while ((readLine = br.readLine()) != "") {
				String[] info = readLine.split(" - ");
				List<String> attributes = new ArrayList<String>();
				attributes.add(info[0]);
				attributes.add(info[1]);
				if (Library.getLocalSongs().containsKey(attributes)) {
					myPlaylist.put(attributes, Library.getLocalSongs().get(attributes));
				} else {
					myPlaylist.put(attributes, new Song(new SongInfo(info[0], info[1])));
				}
				
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (java.lang.NullPointerException npe) {
			return;
		} catch (java.lang.ArrayIndexOutOfBoundsException obe) {
			return;
		}
	}

	//methods 

	/**
	 * Adds a Song to the playlist
	 * 
	 * @param song 	the Song class file that is to be added to the arrayList
	 */
	public void addSong(Song song){
		List<String> attributes = new ArrayList<String>();
		attributes.add(song.getInfo().getName());
		attributes.add(song.getInfo().getArtist());
		myPlaylist.put(attributes, song);
		storePlaylist();
	}

	/**
	 * Deletes a Song from the playlist
	 * 
	 * @param song 	the Song class file that is to be removed from the arrayList
	 */
	public void delSong(String name, String artist){
		List<String> key = new ArrayList<String>();
		key.add(name);
		key.add(artist);
		myPlaylist.remove(key);
		storePlaylist();
	}

}
