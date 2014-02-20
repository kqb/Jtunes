package model;

import java.io.File;
import java.util.*;

import persistence.Cloud;
import persistence.Playlist;

import model.User;

/**
 * Contains all the information pertaining to one user's music library.
 * Has methods for manipulation of the run time volatile data involved with
 * one user's library.
 * 
 * @author Michael Yu/Katie Lo
 *
 */
public class Library {
	/**
	 * Constructor for Library, it initializes the Library and SongsCollections for the user.
	 * 
	 * @param User user.
	 *
	 */
	public Library(User user) {
		cloudSongInfoList = Cloud.getAllSongInfos(user);
		userSongInfoList = Cloud.getSongInfos(user);
		userPlaylists = new ArrayList<Playlist>();
		localSongs = getLocalSongs();
		initUserSongs();
		initUserPlaylists(user);
	}

	/**
	 * Scans for locally stored .mid files and creates Song objects for them.
	 * @return 
	 */
	public static HashMap<List<String>, Song> getLocalSongs() {
		HashMap<List<String>, Song> ls = new HashMap<List<String>, Song>();
		File folder = new File("data/songs");
	    for (File file : folder.listFiles()) {
	    	String fileName = file.getName();
	    	String extension = fileName.substring(fileName.lastIndexOf("."));
	    	//to make sure the extension of the file is .mid
	        if (extension.equals(".mid")){
	    		String[] tempTrackName = fileName.split("-");
	    		String[] tempInfo = tempTrackName[1].trim().split("\\.");
	    		String artistName = tempInfo[0];
	    		String trackName = tempTrackName[0].trim();
	    		Song song = new Song(trackName, artistName, file);
	    		ls.put(makeKeyList(song), song);
	        }
	        
	     }
	    return ls;
	}
	/**
	 * Creates a HashMap of a list of Strings linked to a Song object.
	 * Scans the list of SongInfo objects and creates KeyList objects for them.
	 * If the song is stored locally, adds the songs to the localSongs Map.
	 * Otherwise adds a new Song with SongInfo but no SongContent
	 */
	private void initUserSongs() {
		userSongs = new HashMap<List<String>,Song>();
		for (SongInfo info:userSongInfoList) {
			List<String> key = makeKeyList(info);
			if (localSongs.containsKey(key)) {
				addSong(localSongs.get(key));
			} else {
				addSong(new Song(info));
			}
		}
	}

	/**
	 * Creates a List<String> object using a Song object containing the song's name and artist.
	 * 
	 * @param song			the Song object for which a key list should be created
	 * @return keyList		a List<String> Object containing two elements: the name and the artist of the song
	 */
	private static List<String> makeKeyList(Song song) {
		ArrayList<String> keyList = new ArrayList<String>();
		keyList.add(song.getInfo().getName());
		keyList.add(song.getInfo().getArtist());
		return keyList;
	}
	/**
	 * Creates a List<String> object using a SongInfo object containing the song's name and artist.
	 * 
	 * @param songInfo		the SongInfo object for which a key list should be created
	 * @return keyList		a List<String> Object containing two elements: the name and the artist of the song
	 */
	private List<String> makeKeyList(SongInfo songInfo) {
		ArrayList<String> keyList = new ArrayList<String>();
		keyList.add(songInfo.getName());
		keyList.add(songInfo.getArtist());
		return keyList;
	}
    private void initUserPlaylists(User user) {
    	
    	File folder = new File("data/users/" + user.getId());
    	if (!folder.exists()) {
    		Playlist defualtPlaylist = new Playlist(user.getId(), "defualt playlist");
    		defualtPlaylist.storePlaylist();
    	}
	    for (File file : folder.listFiles()) {
	    	String fileName = file.getName();
	    	String extension = fileName.substring(fileName.lastIndexOf("."));
	    	//to make sure the extension of the file is .txt
	        if (extension.equals(".txt")){
	        	
	        	userPlaylists.add(new Playlist(file));
	        }
	     }
    	}
	// Interface
	/**
	 * Do nothing if song is already in the user's library, otherwise adds the song.
	 * 
	 * @param song		Song to be added.
	 * @return true		if song has been newly added, otherwise return false if song was already in library
	 */
	public boolean addSong(Song song) {
		List<String> key = makeKeyList(song);
		if (userSongs==null||!userSongs.containsKey(key)) {
			userSongs.put(key, song);
			return true;
		} 
		return false;
	}
	/**
	 * Checks to see if the song given already exists in the user's library.
	 * 
	 * @param info		the SongInfo object that must be found in the user's library
	 * @return true		if the song exists in the user's library, false otherwise
	 */
	public boolean hasSong(SongInfo info) {
		List<String> key = makeKeyList(info);
		return userSongs.containsKey(key);
	}
	
	// Getters and setters
	/**
	 * Getter method. Returns the Map of userSongs: the user's library.
	 * 
	 * @return userSongs	the user's library of Song objects
	 */
	public Map<List<String>, Song> getUserSongs() {
		return userSongs;
	}
	/**
	 * Getter method. Returns the list of all songs available to the user on the cloud server
	 * 
	 * @return cloudSongInfoList	a List<SongInfo> object containing all the songs available to the user on the cloud server.
	 */
	public List<SongInfo> getCloudSongInfos() {
		return cloudSongInfoList;
	}
	/**
	 * Getter method. Returns the list of all Playlists the user has previously saved
	 * 
	 * @return cloudSongInfoList a List<Playlist> object containing all the songs available to the user on the cloud server.
	 */
	public List<Playlist> getPlaylists() {
		return userPlaylists;
	}

	// Members
	private Map<List<String>,Song> userSongs;
	private Map<List<String>,Song> localSongs;
	private List<SongInfo> cloudSongInfoList;
	private List<SongInfo> userSongInfoList;
	private List<Playlist> userPlaylists;

}
