package model;

import java.io.File;

/**
 * A song that can be played.
 * 
 * @author Michael Yu/Katie Lo
 *
 */
public class Song {
	// Constructors
	/**
	 * Song object constructor.
	 * Assigns info variable to a SongInfo object and content to null.
	 * 
	 * @param info	a SongInfo object that contains relevant information about the song ## is this correct? - Senisa
	 */
	public Song(SongInfo info) {
		this.info = info;
		this.content = null;
	}
	/**
	 * Song object constructor.
	 * Assigns info variable to a SongInfo object and content to SongContent object.
	 * 
	 * @param info		a SongInfo object that contains relevant information about the song
	 * @param content	a SongContent object that contains path file of the locally stored song
	 */
	public Song(SongInfo info, SongContent content) {
		this.info = info;
		this.content = content;
	}	
	/**
	 * Song object constructor. To be used when parsing from XML.
	 * Assigns info variable to a new SongInfo object using parameters and content to null.
	 * 
	 * @param songId	ID assigned to the song by the JTunes Cloud server
	 * @param name		name of the song
	 * @param artist	artist of the song
	 */
	public Song(String songId, String name, String artist) {
		this.info = new SongInfo(songId, name, artist);
		this.content = null;
	}
	/**
	 * Song object constructor.
	 * Assigns info variable to a new SongInfo object using parameters and content to a specified pathname.
	 * 
	 * @param name		name of the song
	 * @param artist	artist of the song
	 * @param file		a File object containing the pathname of the .mid file
	 */
	public Song(String name, String artist, File file) {
		this.info = new SongInfo(name, artist);
		this.content = new SongContent(file);
	}
	
	// Getters and setters
	/**
	 * Getter method to return the SongInfo object associated to the Song object
	 * 
	 * @return	the info variable: the SongInfo object associated with this Song object
	 */
	public SongInfo getInfo() {
		return info;
	}
	/**
	 * Getter method to return the SongContent object associated to the Song object
	 * 
	 * @return	the content variable: the SongContent object associated with this Song object
	 */
	public SongContent getContent() {
		return content;
	}
	/**
	 * Setter method. Replaces the info variable for this Song object.
	 * 
	 * @param info		a SongInfo object that contains relevant information for this Song object
	 */
	public void setInfo(SongInfo info) {
		this.info = info;
	}
	/**
	 * Setter method. Replaces the content variable for this Song object.
	 * 
	 * @param content	a SongContent object that contains the pathname of the locally stored .mid file
	 */
	public void setContent(SongContent content) {
		this.content = content;
	}
	// Interface
	public boolean hasContent() {
		return !(content == null);
	}
	// Members
	private SongInfo info;
	private SongContent content;
	
}