package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Represents a single user. Responsible for high level functions specifically pertaining
 * to one user. Contains an instance of
 * model.Library, which is the user's library.
 * 
 * @author Michael Yu
 */
public class User {
	// Constructors
	public User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}
	//constructor w/o the name
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	public User(String email) {
		this.email = email;
	}
	
	// Interface
	/**
	 * Check if two users have equal IDs
	 * @param that the other user
	 * @return
	 */
	public boolean hasEqualIds(User that) {
		return this.email.equals(that.email);
	}
	/**
	 * Check if the information in this instance describes a valid user.
	 * 
	 * @return 0 if the user with email this.email exists and has password this.password;
	 *         1 if the user with email this.email exists but this.password is not that user's password
	 *         2 if no user with email this.email exists.
	 */
	public int checkValidity() {
		return exec.getData().checkUserValidity(this);
	}
	/**
	 * 
	 * @param name what the user wants her name to be
	 * @return 0 if the user is successfully created.
	 *         1 if the user already exists.
	 *         2 if the user information providing is missing, or partly missing.
	 */   
	public int createAccount(String name){
		
		return exec.getData().createUser(this);
		
	}
	/**
	 * Initiate User instance so it can do things.
	 * @param exec instance that controls the running JTunes application
	 */
	public void init(Exec exec) {
		this.exec = exec;
		library = exec.getData().createLibrary(this);
	}
	/**
	 * Add a song to user's library; download from the JTunes cloud if necessary.
	 * @param songInfo
	 */
	public void makeSongPlayable(model.SongInfo songInfo) {
		if (!library.hasSong(songInfo)) {
			model.Song song = new model.Song(songInfo,
					exec.getData().acquireSongContent(this, songInfo));
			library.addSong(song);
		}
	}
	/**
	 * 
	 * @return a collection of songs that the user can play immediately
	 */
	public model.PlayableCollection getAllSongs() {
		return library.getAllSongs();
	}
	
	// Getters and setters
	public String getId() {
		return email;
	}
	public String getCredential() {
		return password;
	}
	public String getName() {
		return name;
	}
	public model.Library getLibrary() {
		return library;
	}
	
	// Member variables
	private String email;
	private String password;
	private String name;
	private model.Library library;
	private Exec exec;
}