package ui;

/**
 * Abstract user interface of JTunes application specifying
 * the public methods interface of any implementation of a
 * user interface class.
 * 
 * @author Michael Yu
 *
 */

public abstract class UI {
	// Interface
	public void init(controller.Exec exec) {
		this.exec = exec;
	}
	/**
	 * Elicit user to log in.
	 * 
	 * @return logged in user
	 */
	abstract public controller.User logInUser();
	/**
	 * Run main loop of JTunes.
	 * 
	 * @param user represents current user
	 * @return true if user wants to log back in, otherwise false to quit JTunes
	 */
	abstract public boolean runMain(controller.User user);
	
	// Members
	public static class UIFactory {
		
		public static UI createDefault() {
			return new CLI();
		}
	}
	/**
	 * Execute the entire JTunes application.
	 */
	protected controller.Exec exec;

}
