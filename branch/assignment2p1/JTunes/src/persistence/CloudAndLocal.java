package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import model.Library;
import model.Song;
import model.SongContent;
import model.SongInfo;
import controller.Exec;
import controller.User;

/**
 * Handles persistent data by utilizing the JTunes cloud and
 * the local hard drive. This is the current working implementation of
 * the abstract Data class.
 * 
 * @author Michael Yu
 *
 */
class CloudAndLocal extends Data {
	// Constructors
	public CloudAndLocal() {
		local = new Local();
		cloud = new Cloud();
	}
	// Interface
	/* (non-Javadoc)
	 * @see persistence.Data#init(controller.Exec)
	 */
	@Override
	public void init(Exec exec) {
		super.init(exec);
		local.init();
		cloud.init();
	}

	/* (non-Javadoc)
	 * @see persistence.Data#createUserLibrary(controller.User)
	 */
	@Override
	public Library createLibrary(User user) {
		Library library = new Library();
		if (!local.hasUser(user)) {
			local.createUser(user);
			for(SongInfo songInfo : cloud.getSongInfos(user)) {
				library.addSong(new Song(songInfo, acquireSongContent(user, songInfo)));
			}
		} else {
			library = local.getLibrary(user);
		}
		return library;
	}

	/* (non-Javadoc)
	 * @see persistence.Data#getAvailableSongInfos(controller.User)
	 */
	@Override
	public Iterable<SongInfo> getAvailableSongInfos(User user) {
		try {
			return cloud.getAllSongInfos(user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see persistence.Data#acquireSongContent(controller.User, model.SongInfo)
	 */
	@Override
	public SongContent acquireSongContent(User user, SongInfo songInfo) {
		if (!local.giveUserSongContent(user, songInfo)) {
			InputStream is = cloud.getSongContentStream(user, songInfo);
			OutputStream os = local.allocateSongContentStream(user, songInfo);
			writeSong(is, os);
		} else {
			/* Because JTunes cloud insists on having the user download a song
			 * to claim the song in his library
			 */
			InputStream is = cloud.getSongContentStream(user, songInfo);
			try {
				is.close();
			} catch (IOException e) {
				/* What the hell is this? What in god's name would I expect to go wrong with
				 * closing a file and how in the world would I fix it? Whatever.
				 */
			}
		}
		return local.getSongContent(user, songInfo);
		
	}
	@Override
	public int checkUserValidity(User user) {
		return cloud.checkUserValidity(user);
	}
	@Override
	public int createUser(User user) {
		return cloud.createUser(user);
	}
	// Methods
	
	private void writeSong(InputStream is, OutputStream os){
	// Copies the content of is into os
		byte[] buffer = new byte[4096];
		int bit = -1;
		
		try {
			while ((bit = is.read(buffer)) != -1) {
				if (bit > 0){
				os.write(buffer, 0, bit);
				}
			}
			is.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// Members
	private Cloud cloud;
	private Local local;

}