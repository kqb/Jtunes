package model;

/**
 * A song that can be played.
 * 
 * @author Michael Yu
 *
 */
public class Song {
	// Constructors
	public Song(SongInfo info) {
		this.info = info;
	}
	public Song(SongInfo info, SongContent content) {
		this.info = info;
		this.content = content;
	}
	public Song(String songId, String name, String artist) {
		this.info = new SongInfo(songId, name, artist);
	}
	
	// Getters and setters
	public SongInfo getInfo() {
		return info;
	}
	public SongContent getContent() {
		return content;
	}
	
	// Members
	private SongInfo info;
	private SongContent content;
	
}