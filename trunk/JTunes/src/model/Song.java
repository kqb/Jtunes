package model;

public class Song {
	//TODO incorporate Artist
	public final int id;
	public final String songTitle;
//	public final Artist songArtist;
	public final String songAlbum;

	public Song(int id, String title, /*String artist, */String album) {
		this.id = id;
		this.songTitle = title;
		this.songAlbum = album;
	}
	
	public String getSongTitle() {
		return this.songTitle;
	}
	
}
