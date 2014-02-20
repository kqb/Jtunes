package model;

import java.util.*;

import persistence.*;
import persistence.*;


public class Library {
	public static ArrayList<Song> songs;
	public static ArrayList<SongWithFile> songFiles;
	
	public static void getAllSongsAndFiles() {
		songs = new ArrayList<>();
		songFiles = new ArrayList<>();
		for (SongWithFile songFile: new Data.songFilesIterable()) {
			songFiles.add(songFile);
			songs.add(songFile.song);
		}
	}
	
	
	
	public static void init() {
		Data.init();
		getAllSongsAndFiles();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
