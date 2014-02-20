package persistence;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

import controller.User;

import model.Library;
import model.Song;
import model.SongContent;
import model.SongInfo;

/**
 * *Incomplete* NO TOUCHy PLEASE
 * Deals with all interactions with local persistent data.
 * If the buffering logic in this class becomes too complicated,
 * the buffering functions of this class may be refactored into
 * new model classes, one for the user base and another for all
 * JTunes cloud contents.
 * 
 * @author Michael Yu
 *
 */
class Local {
	// Interface
	void init() {
		usersData = new UnbufferedUsersData();
		songsData = new UnbufferedSongsData();
		users = new TreeMap<String, User>();
		songs = new TreeMap<String, Song>();
		findMetaFile();
		processMetaFile();
	}
	boolean giveUserSongContent(User user, SongInfo songInfo) {
		refreshCachedUser(user);
		Song song = songs.get(songInfo.getId());
		if (song == null) {
			return false;
		}
		if (!cachedUserSongs.containsKey(songInfo.getId())) {
			cachedUserSongs.put(songInfo.getId(), song);
			usersData.giveUserSong(user, song);
		}
		return true;
	}
	SongContent getSongContent(User user, SongInfo songInfo) {
		refreshCachedUser(user);
		if (cachedUserSongs.containsKey(songInfo.getId())) {
			return songs.get(songInfo.getId()).getContent();
		} else {
			assert false;
			// TODO exception!
		}
		return null;
	}
	/**
	 * Returns a stream that overwrites or creates from new the file
	 * to which the song file can be written. Does not check previous
	 * existence of this song file. Also does not check existence
	 * of song in file containing list of songs, hence will result
	 * in corrupt data if called wrong.
	 * @param user
	 * @param songInfo
	 * @return a stream into which the song content can be written
	 */
	OutputStream allocateSongContentStream(User user, SongInfo info) {
		return songsData.allocateSongContentStream(info);
	}
	boolean hasUser(User user) {
		return users.containsKey(user.getId());
	}
	void createUser(User user) {
		if (!users.containsKey(user.getId())) {
			users.put(user.getId(), user);
			usersData.addUser(user);
		}
	}
	Library getLibrary(User user) {
		refreshCachedUser(user);
		Library library = new Library();
		for (Song song : cachedUserSongs.values()) {
			library.addSong(song);
		}
		return library;
	}
	
	// Methods
	/**
	 * Searches for the meta file with name META_FILE_NAME from current directory
	 * and saves this File into member metaFile.
	 * @throws IOException 
	 */
	private void findMetaFile()  {

		try {
			File dir = new File(".").getCanonicalFile();
			while (!(new File(dir, META_FILE_NAME).exists())) {
				dir = dir.getParentFile();
				if (dir == null) {
					//TODO exception
					assert false;
				}
			}
			metaFile = new File(dir, META_FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void processMetaFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(metaFile));
			rootDataDir = new File(br.readLine());
			usersData.setMetaFile(new File(br.readLine()));
			songsData.setMetaFile(new File(br.readLine()));
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void refreshCachedUser(User user) {
		if (users.containsKey(user.getId())) {
			if (cachedUser.getId() != user.getId()) {
				cachedUser = user;
				Iterable<String> songIds = usersData.getUserSongIds(user);
				cachedUserSongs = new TreeMap<String, Song>();
				for (String id : songIds) {
					cachedUserSongs.put(id, songs.get(id));
				}
			}
		} else {
			assert false;
			// TODO exception!
		}
	}
	
	// Members
	private class UnbufferedUsersData {
		void initByMetaFile(File file) {
			metaFile = file;
			processMetaFile();
		}
		public Iterable<String> getUserSongIds(User user) {
			List<String> songIds = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(getUserFile(user)));
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					songIds.add(line);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return songIds;
		}
		/**
		 * Give user access to song. It is unchecked if the user already had song in his library,
		 * and hence corrupt data can result if a bad call to this function is invoked.
		 * @param user
		 * @param song
		 */
		public void giveUserSong(User user, Song song) {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(getUserFile(user), true)));
				pw.println(song.getInfo().getId());
				pw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/**
		 * Do not check that the user does not already exist, and hence will overwrite previous
		 * user data file.
		 * @param user
		 */
		public void addUser(User user) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(getUserFile(user), true));
				bw.write("");
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		void processMetaFile() {
			try {
				BufferedReader br = new BufferedReader(new FileReader(metaFile));
				rootDataDir = new File(Local.this.rootDataDir, br.readLine());
				for (String id = br.readLine(); id != null && id.trim().length() > 0; id = br.readLine()) {
					users.put(id, new User(id));
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * @param metaFile the metaFile to set
		 */
		void setMetaFile(File metaFile) {
			this.metaFile = metaFile;
		}

		private File metaFile;
		private File rootDataDir;
		
		private File getUserFile(User user) {
			return new File(rootDataDir, user.getId());
		}
	}
	private class UnbufferedSongsData {
		void initByMetaFile(File file) {
			metaFile = file;
			processMetaFile();
		}
		public OutputStream allocateSongContentStream(SongInfo info) {
			try {
				File file = getSongFile(info);
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
				pw.println(info.getId());
				pw.println(info.getName());
				pw.println(info.getArtist());
				pw.close();
				return new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		void processMetaFile() {
			try {
				BufferedReader br = new BufferedReader(new FileReader(metaFile));
				rootDataDir = new File(Local.this.rootDataDir, br.readLine());
				String id, title, artist;
				for (id = br.readLine(); id != null && id.trim().length() > 0; id = br.readLine()) {
					id = br.readLine();
					title = br.readLine();
					artist = br.readLine();
					SongInfo info = new SongInfo(id, title, artist);
					songs.put(id, new Song(info, new SongContent(getSongFile(info))));
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * @param metaFile the metaFile to set
		 */
		void setMetaFile(File metaFile) {
			this.metaFile = metaFile;
		}
		File getSongFile(SongInfo info) {
			return new File(rootDataDir, info.getId());
		}

		private File metaFile;
		private File rootDataDir;
	}
	private final String META_FILE_NAME = "JTunes.meta";
	private File metaFile;
	private File rootDataDir;
	
	private UnbufferedUsersData usersData;
	private UnbufferedSongsData songsData;
	
	private Map<String, User> users;
	private User cachedUser = new User(null);
	private SortedMap<String, Song> cachedUserSongs;
	private SortedMap<String, Song> songs;
}