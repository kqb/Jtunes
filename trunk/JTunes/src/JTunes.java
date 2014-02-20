import java.io.IOException;
import java.util.Scanner;


import user.*;
import model.*;

public class JTunes {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//TEST CODE: DELETE FOR FINAL SUBMISSION
/*		Song song1 = new Song(1, "I Knew You Were Trouble", "Taylor Swift", "RED");
		Song song2 = new Song(2, "Diamonds", "Rihanna", "Whatever Album");
		ArrayList<Song> testList = new ArrayList<Song>();
		testList.add(song1);
		testList.add(song2);*/
		Library.init();
		System.out.println(Library.songs);
		
		//Boolean to track whether user wants program termination
		Boolean terminateProgram = false;
		//Asks user for input
		Scanner userInput = new Scanner(System.in);
		//TODO refactor UI code into Interface
		//Loops asking user for input until user chooses to terminate
		while (!terminateProgram) {
			//print Display into terminal given ArrayList<Song> from Data
			View.displayAll(Library.songs);
			System.out.println("What would you like to do?");
			
			String x = userInput.nextLine();			
			if (x.equals("quit")) {
				System.out.println("Good-bye!");
				terminateProgram = true;
			} else {
				Interface.parseUserCommand(x);
			}
		}
		userInput.close();
		System.exit(0);
	}
}
