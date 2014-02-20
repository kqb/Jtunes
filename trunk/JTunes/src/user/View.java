package user;
import java.util.ArrayList;

import model.Song;


public class View {


	//TEST CODE, DELETE FOR FINAL SUBMISSION
//	public static void main(String[] args) {
//		Song song1 = new Song("I Knew You Were Trouble", "Taylor Swift", "RED");
//		Song song2 = new Song("Diamonds", "Rihanna", "Whatever Album");
//		ArrayList<Song> testList = new ArrayList<Song>();
//		testList.add(song1);
//		testList.add(song2);
//
//		displayAll(testList);
//	}

	public static void displayAll(ArrayList<Song> songList) {
		displaySongs(songList);
		displayInstructions();
	}

	public static void displaySongs(ArrayList<Song> songList) {
		// Given an ArrayList of Song objects, this method will print the names of the songs

		System.out.println(songList.size() + " Available Songs in Library:");
		System.out.println("-------------------------");
		int x = songList.size();
		for (int i = 0; i < x; i++) {
			System.out.println(songList.get(i).getSongTitle());
		} 		System.out.println("-------------------------");
	}

	public static void displayInstructions() {
		System.out.println("play (song title) - plays song");
		System.out.println("quit - exits program");
	}

}
