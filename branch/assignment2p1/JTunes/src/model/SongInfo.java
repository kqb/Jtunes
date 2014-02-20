package model;

/**
 * Contains the information of a song.
 * 
 * @author Michael Yu
 *
 */
public class SongInfo {
	// Constructors
	public SongInfo(String songId) {
		this.songId = songId;
	}
	public SongInfo(String songId, String name, String artist) {
		this.songId = songId;
		this.name = name;
		this.artist = artist;
	}
	
	// Getters and setters
	public String getId() {
		return songId;
	}
	public String getName() {
		return name;
	}
	public String getArtist() {
		return artist;
	}
	
	// Members
	private String songId;
	private String name;
	private String artist;
}
