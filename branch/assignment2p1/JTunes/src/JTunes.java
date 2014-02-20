
/**
 * Entry point of JTunes application.
 * 
 * @author Michael Yu
 *
 */
public class JTunes {

	
		/**
		 * @param args does nothing
		 */
	public static void main(String[] args) {
		controller.Exec exec = new controller.Exec();
		exec.run();
		System.exit(0);
	}
}