package ui;
import persistence.Cloud;
import controller.Exec;
import controller.User;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * Prompts the user for inputs for logging in and to run the JTunes function
 * using the abstract class UI
 * @author Eva Kung
 */
class UserPrompt implements UI {
	private static String retry = "retry";
	private static String email;
	private static String password;
	private static String name;
	private static String listAll = "list all songs";
	private static String list = "my songs";
	private static User user;
	private static User newUser;
	private static String quit = "quit";
	private static String help = "Type 'quit' to exit the library, type 'my songs' to" +
			" list songs on your account, type 'list all songs' to list all songs on " +
			"the cloud service, type 'play' to play the song, type 'stop' to stop the song.";
	private static boolean runMainValue;
	
	
	
	/*
	 * Obtain user prompt and log in information and what they want to do based on personal 
	 * preference.
	 * @throws Exception
	 * @throws IOException
	 */
	public static void userInfo() throws Exception, IOException{
		Scanner scan = new Scanner( System.in );
		String input = "";
		System.out.println("Please log in");
		System.out.println("Email:");
		UserPrompt UserPrompt = new UserPrompt();
		while (!input.contains(quit) || input.contains(retry)){		
			input = scan.next();
			String emailMatch = ".+\\@.+\\..+";

			int check;
			boolean correctEmail = Pattern.matches(emailMatch, input);
			//regex to match if emails in the form of "xxx@yyy.zzz"
			if(!correctEmail){
				System.out.println("Invalid email address, please try again: ");
				input = scan.next();
				runMainValue = true; //try to log in again of user wants to attempt to log in
			}else if(correctEmail){
				email = input;
				System.out.println("Password: " +"");
				input = scan.next();
				password = input;
				user = new User(email, password);
				Cloud cloud1 = new Cloud();
				int a = cloud1.checkUserValidity(user);
				System.out.println(a); //printing int 
				if (a == 0){
					System.out.println("You have succesfully logged in! ");
					System.out.println("What would you like to do? (Type 'help' for instructions)");
					input = scan.next();
					if (input.contains("help")){
						System.out.println(help);
						System.out.println("What would you like to do?");
						input = scan.next();
						if (input.contains("play")){
							System.out.println("playing song....");
							input = scan.next();
							//play the music class , call methods
						}else if(input.contains(quit)){
							break;
						}else if(input.contains(listAll)){
							System.out.println("listing all songs....");
							input = scan.next();
							// list all the songs. 
						}else if (input.contains(list)){
							//list songs in your acc
							System.out.println("listing my songs....");
							input = scan.next();
						}else{
							System.out.println("Sorry , invalid command. What would you like to do?");
							input = scan.next();
						}
					
					}
				}else if(a == 1){
					System.out.println("User does not exist, would you like to create a new account?('Y' or 'N'):");
					input = scan.next();
					if(input.contains("Y")){
						System.out.println("Please type in the following format: email<space>password");
						input = scan.next();
						String[] info = input.split(" "); //Separate new info into [email,password]
						String newEmail = info[0]; // email 
						String newPassword = info[1]; //password
						newUser = new User(newEmail, newPassword); //create new user using new typed information
						int createUser = cloud1.createUser(newUser);
						if (createUser == 0){
							System.out.println("Congratulations! You have succesfully created a new account!");
							System.out.println("You are now automatically logged in, what would you like to do?");
							input = scan.next();
						}else if(createUser == 1){
							System.out.println("Sorry user already exists or your input is invalid, try again:");
							input = scan.next();
						}
						
						
					}
				}
			}
		}
		
		
		if(input.contains(quit)){
			runMainValue = false;
			System.exit(0);
			System.out.println("You have quit the program.");
			System.out.println(UserPrompt.runMain(user));
		}
	}

	@Override
	public User logInUser() throws IOException, Exception {
		userInfo();
		return user;
	}


	@Override
	public boolean runMain(User user) {
		return runMainValue;
	}	
	


}