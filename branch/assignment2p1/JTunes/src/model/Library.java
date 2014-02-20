package model;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Contains all the information pertaining to one user's music library.
 * Has methods for manipulation of the run time volatile data involved with
 * one user's library.
 * 
 * @author Michael Yu
 *
 */
public class Library {
	// Interface
	/**
	 * Do nothing if song is already in library, otherwise adds the song.
	 * 
	 * @param song Song to be added.
	 * @return true if song has been newly added, otherwise return false if song
	 * was already in library
	 */
	public boolean addSong(Song song) {
		if (!allSongs.songs.containsKey(song.getInfo().getId())) {
			allSongs.songs.put(song.getInfo().getId(), song);
			return true;
		} else {
			return false;
		}
	}
	public boolean hasSong(SongInfo info) {
		return allSongs.songs.containsKey(info.getId());
	}
	
	// Getters and setters
	public SongsCollection getAllSongs() {
		return allSongs;
	}

	// Members
	/**
	 * A hidden implementation of PlayableCollection. Change this
	 * implementation as needed when PlayableCollection changes.
	 * 
	 * @author Michael Yu
	 *
	 */
	private static class SongsCollection
	implements PlayableCollection {
		public Iterable<Song> getDefaultIterable() {
			return songs.values();
		}
		private SongsCollection() {
			songs = new TreeMap<String, Song>();
		}
		@Override
		public boolean hasSongId(String id) {
			return songs.containsKey(id);
		}
		private SortedMap<String, Song> songs;
	}
	private SongsCollection allSongs;

}
