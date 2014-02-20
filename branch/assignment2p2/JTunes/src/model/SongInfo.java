package model;

/**
 * Contains the information of a song.
 * 
 * @author Michael Yu
 *
 */
public class SongInfo {
	// Constructors
	/**
	 * SongInfo constructor. Assigns a value to the songId variable.
	 * Does not assign value to the name and artist variables.
	 * 
	 * @param songId	the Id of the song on the cloud server ## is this correct? - Senisa
	 */
	public SongInfo(String songId) {
		this.songId = songId;
	}
	/**
	 * SongInfo constructor. Assigns a value to the songId, name and artist variables.
	 * 
	 * @param songId	the Id of the song on the cloud server
	 * @name			the name of the song
	 * @artist			the artist of the song
	 */
	public SongInfo(String songId, String name, String artist) {
		this.songId = songId;
		this.name = name;
		this.artist = artist;
	}
	/**
	 * SongInfo constructor. Assigns a value to the name and artist variable.
	 * Does not assign value to the songId variable.
	 * 
	 * @name			the name of the song
	 * @artist			the artist of the song	
	 */
	public SongInfo(String name, String artist) {
		this.name = name;
		this.artist = artist;
	}
	
	// Getters and setters
	/**
	 * Getter method. Returns the Id of the song on the cloud server.
	 * 
	 * @return		the songId varible of the SongInfo object
	 */
	public String getId() {
		return songId;
	}
	/**
	 * Getter method. Returns the name of the song.
	 * 
	 * @return		the name variable of the SongInfo object
	 */
	public String getName() {
		return name;
	}
	/**
	 * Getter method. Returns the artist of the song.
	 * 
	 * @return		the artist variable of the SongInfo object
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * Replaces the songId variable of the SongInfo object.
	 * 
	 * @param songId	the new songId to replace the old value, should match the cloud server's Id
	 */
	public void setId(String songId) {
		this.songId = songId;
	}
	/**
	 * Replaces the name variable of the SongInfo object.
	 * 
	 * @param name		the new name to replace the old value
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Replaces the artist variable of the SongInfo object.
	 * 
	 * @param name		the new artist to replace the old value
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	// Members
	private String songId;
	private String name;
	private String artist;
}
