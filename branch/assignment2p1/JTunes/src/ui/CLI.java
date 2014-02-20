/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import model.SongInfo;


import controller.Exec;
import controller.User;

/**
 * @author Michael Yu
 *
 */
public class CLI extends UI {



	/* (non-Javadoc)
	 * @see ui.UI#init(controller.Exec)
	 */
	@Override
	public void init(Exec exec) {
		super.init(exec);
		br = new BufferedReader(new InputStreamReader(System.in));
		sc = new Scanner(br);
		pw = new PrintWriter(System.out);
		state = null;
		
	}

	/* (non-Javadoc)
	 * @see ui.UI#logInUser()
	 */
	@Override
	public User logInUser() {
		return (User) run(new TitleMenu(this));
	}

	/* (non-Javadoc)
	 * @see ui.UI#runMain(controller.User)
	 */
	@Override
	public boolean runMain(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	// Members
	private static abstract class State {
		protected State(CLI cli) {
			this.cli = cli;
		}
		protected abstract State getNextState();
		protected abstract Object output();
		CLI cli;
	}

	private static class TitleMenu extends State {

		protected TitleMenu(CLI cli) {
			super(cli);
		}
		private enum Command {
			login ("Returning user? Log in to your account."),
			signup ("First time? It's easy to get started using JTunes");
			
			Command(String instr) {
				this.instruction = instr;
			}
			String getInstruction() {
				return instruction;
			}
			private String instruction;
		}
		protected State getNextState() {
			String cmdstr;
			cli.pw.println("Log in or sign up to JTunes.");
			cli.pw.println("Commands:");
			for (Command cmd : Command.values()) {
				cli.pw.println("[" + cmd.toString() + "]" + cmd.getInstruction());
			}
			Command cmd;
			while (true) {
				try {
					cmdstr = cli.sc.nextLine();
					cmd = Enum.valueOf(Command.class, cmdstr);
					break;
				} catch (IllegalArgumentException e) {
					
				}
			}
			switch (cmd) {
			case login:
				return new LogIn(cli);
			case signup:
				return new SignUp(cli);
			default:
				assert false; //TODO exception
				return null;
			}
		}
		@Override
		protected Object output() {
			return null;
		}
	}
	private static class LogIn extends State {
		protected LogIn(CLI cli) {
			super(cli);
		}
		protected State getNextState() {
			String email, password;
			cli.pw.println("Enter your email");
			email = cli.sc.nextLine();
			cli.pw.println("Enter your password");
			password = cli.sc.nextLine();
			user = new User(email, password);
			switch (user.checkValidity()) {
			case 0:
				cli.pw.println("Log in successful!");
				return null;
			default:
				cli.pw.println("The given email and password combination is invalid.");
				return new TitleMenu(cli);
			}
		}
		@Override
		protected Object output() {
			return user;
		}
		private User user;
	}
	private static class SignUp extends State {
		protected SignUp(CLI cli) {
			super(cli);
		}
		protected State getNextState() {
			String email, password, name;
			cli.pw.println("Tell us your email. This will act as your JTunes username.");
			email = cli.sc.nextLine();
			cli.pw.println("Choose your password. Do not forget it, as it cannot be recovered.");
			password = cli.sc.nextLine();
			cli.pw.println("What is your name?");
			name = cli.sc.nextLine();
			user = new User(email, password, name);
			switch (user.createAccount(name)) {
			case 0:
				cli.pw.println("Account creation successful!");
				return null;
			case 1:
				cli.pw.println("An account with that email already exists.");
				return new TitleMenu(cli);
			default:
				assert false; //TODO exception
				return null;
			}
		}
		@Override
		protected Object output() {
			return user;
		}
		private User user;
	}
	private static class LoggedInMenu extends State {
		protected LoggedInMenu(CLI cli, User user) {
			super(cli);
			this.user = user;
		}
		private enum Command {
			library ("Play a song from your personal music library."),
			cloud ("Look at all the songs you can download from the JTunes cloud.");
			
			Command(String instr) {
				this.instruction = instr;
			}
			String getInstruction() {
				return instruction;
			}
			private String instruction;
		}
		protected State getNextState() {
			String cmdstr;
			cli.pw.println("Welcome " + user.getName() + " !");
			cli.pw.println("Commands:");
			for (Command cmd : Command.values()) {
				cli.pw.println("[" + cmd.toString() + "]" + cmd.getInstruction());
			}
			Command cmd;
			while (true) {
				try {
					cmdstr = cli.sc.nextLine();
					cmd = Enum.valueOf(Command.class, cmdstr);
					break;
				} catch (IllegalArgumentException e) {
					
				}
			}
			switch (cmd) {
			case library:
				return null; //new SelectSongToPlay(cli, user);
			case cloud:
				return new ViewAllSongs(cli, user);
			default:
				assert false; //TODO exception
				return null;
			}
		}
		@Override
		protected Object output() {
			return null;
		}
		private User user;
	}
	private static class ViewAllSongs extends State {
		protected ViewAllSongs(CLI cli, User user) {
			super(cli);
			this.user = user;
		}

		protected State getNextState() {
			String cmdstr;
			Map<String, SongInfo> availables = new HashMap<String, SongInfo>();
			cli.pw.println("Song ID | Song title | Song artist");
			for (SongInfo info : cli.getExec().getData().getAvailableSongInfos(user)) {
				cli.pw.println(info.getId() + " | " + info.getName() + " | " + info.getArtist());
				availables.put(info.getId(), info);
			}
			cli.pw.println("Write down the song ID of the song you would like to add to your library.");
			cli.pw.println("Otherwise, write anything else to go back");
			cmdstr = cli.sc.nextLine();
			if (availables.containsKey(cmdstr)) {
				user.makeSongPlayable(availables.get(cmdstr));
				cli.pw.println("Song added to your library!");
				return new ViewAllSongs(cli, user);
			} else {
				cli.pw.println("No song ID matches, going back to menu.");
				return new LoggedInMenu(cli, user);
			}
		}
		@Override
		protected Object output() {
			return null;
		}
		private User user;
	}
//	private static class SelectSongToPlay extends State {
//		
//	}
//	private static class ViewLibrary extends State {
//		
//	}
//	private static class PlayingSong extends State {
//		
//	}
	
	
	PrintWriter pw;
	Scanner sc;
	BufferedReader br;
	State state;
	private Object run(State state) {
		if (state == null) {
			return null;
		}
		State next;
		while (true) {
			next = state.getNextState();
			if (next == null) {
				break;
			}
			state = next;
		}
		return state.output();
	}
	// main
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	Exec getExec() {
		return exec;
	}

}
