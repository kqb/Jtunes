package controller;

import ui.GUI;



/**
 * Runs executive functions of the JTunes application.
 * This class has getter methods for key components of the running
 * JTunes application which may be needed by other components; hence
 * each major class has a member that references the running instance of this class.
 * 
 * @author Michael Yu
 */
public class Exec {
	// Interface
	/**
	 * Runs the application.
	 */
	public void run() {
		init();
		User user;
		do {
			user = userInterface.logInUser();
			user.init(this);
		} while (userInterface.runMain(user));
		System.exit(0);
	}
	
	// Getters and setters
	/**
	 * 
	 * @return the current user interface of the running application
	 */
	public ui.UI getUserInterface() {
		return userInterface;
	}
	/**
	 * 
	 * @return the current music player of the running application
	 */
	public ui.Player getPlayer() {
		return player;
	}
	/**
	 * 
	 * @return the current data handler of the running application
	 */
	public persistence.Data getData() {
		return data;
	}
	
	// Member variables
	private ui.UI userInterface;
	private ui.Player player;
	private persistence.Data data;
	
	// Methods
	private void initDefaultUserInterface() {
		userInterface = ui.UI.UIFactory.createDefault();
		userInterface.init(this);
	}
	private void initDefaultPlayer() {
		player = ui.Player.PlayerFactory.createDefault();
		player.init(this);
	}
	private void initDefaultData() {
		data = persistence.Data.DataFactory.createDefault();
		data.init(this);
	}
	private void init() {
		initDefaultData();
		initDefaultUserInterface();
		initDefaultPlayer();
	}


}
