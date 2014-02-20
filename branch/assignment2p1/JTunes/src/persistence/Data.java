package persistence;

import ui.UI;
import controller.User;

/**
 * All tasks directly dealing with persistent data is done via here.
 * Currently, this class actually acts as a mediator between the run time state
 * and persistent data, storing some buffer data structure as needed.
 * 
 * @author Michael Yu
 *
 */
public abstract class Data {
	// Interface
	public void init(controller.Exec exec) {
		this.exec = exec;
	}
	/**
	 * Create a user's library from persistent data.
	 * 
	 * @param user the user
	 * @return the created library
	 */
	public abstract model.Library createLibrary(controller.User user);
	/**
	 * Get information on all the songs available to be added to user's library.
	 * 
	 * @param user the user
	 * @return all the available songs
	 */
	public abstract Iterable<model.SongInfo> getAvailableSongInfos(controller.User user);
	/**
	 * Allow a user to acquire a song's content so that the user can play it.
	 * 
	 * @param user the user
	 * @param songInfo the song's information
	 * @return the song's content
	 */
	public abstract model.SongContent acquireSongContent(controller.User user, model.SongInfo songInfo);
	public abstract int checkUserValidity(controller.User user);
	public abstract int createUser(User user);
	// Members
	public static class DataFactory {
		public static Data createDefault() {
			return new CloudAndLocal();
		}
	}
	/**
	 * Execute the entire JTunes application.
	 */
	private controller.Exec exec;

}