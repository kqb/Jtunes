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
	public SongContent(File file) {
		this.file = file;
	}
	public OutputStream getOutputStream() {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return os;
	}
	public InputStream getInputStream() {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}
	// Getters and setters
	/**
	 * @return the file containing the song (e.g. /folder/song.midi)
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * @param file the file to set
	 */
	void setFile(File file) {
		this.file = file;
	}

	// Members
	private File file;

}