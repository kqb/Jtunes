package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The playable content of a song.
 * 
 * @author Michael Yu
 *
 */
public class SongContent {
	/**
	 * SongContent constructor.
	 * Assigns a File object to the file variable.
	 * 
	 * @param file		the File object that contains the song's .mid pathname (e.g. /folder/song.midi)
	 */
	public SongContent(File file) {
		this.file = file;
	}
	/**
	 * SongContent constructor.
	 * Creates a File object to assign to file using the supplied filepath for the song's .mid file.
	 * 
	 * @param path		a string of the complete filepath to the song's .mid file, relative to the JTunes folder (e.g. "/data/songs/song.midi")
	 */
	public SongContent(String path) {
		file = new File(path);
	}
	
	// Getters and setters
	/**
	 * Getter method. Returns the file variable in this SongContent object.
	 * 
	 * @return the File object containing the song's .mid pathname (e.g. /folder/song.midi)
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Setter method. Replaces the current file object with the one supplied.
	 * 
	 * @param file		the new File object to replace the current file variable
	 */
	void setFile(File file) {
		this.file = file;
	}

	// Members
	private File file;

}