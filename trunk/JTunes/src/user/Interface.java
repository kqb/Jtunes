package user;

import java.io.File;

import model.Library;


public class Interface {
	/**
	 * @param args
	 */
	
	public static void parseUserCommand(String command) {
		if ((command.length() > 4) && (command.substring(0, 4).equals("play"))) {
			
			//TESTCODE: DELETE FOR FINAL SUBMISSION
			System.out.println("Playing song: " + command.substring(5));
			System.out.println("+++++++++++++++++++++++++");
			
			
			//Find Song object with specified title
			
			for(int i = 0; i < Library.songs.size(); i++) {
				if (Library.songs.get(i).getSongTitle().equals(command.substring(5))) {
					System.out.println("Playing song: " + Library.songs.get(i).songTitle);
					System.out.println("+++++++++++++++++++++++++");
					Player.playSong(new File(Library.songFiles.get(i).file.toUri()));
				} else {
					System.out.println("Invalid song name");
				}
			}
			


			} else {
				System.out.println("Invalid command");
				System.out.println("+++++++++++++++++++++++++");
			}
	}
}
