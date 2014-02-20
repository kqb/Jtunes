package ui;

/**
 * Plays songs.
 * 
 * @author Michael Yu
 *
 */

public abstract class Player {
	public void init(controller.Exec exec) {
		this.exec = exec;
	}
	public abstract boolean playSong(model.Song playableSong);
	public abstract boolean stop();
	
	public static class PlayerFactory {
		public static Player createDefault() {
			return new SongPlayer();
		}
	}
	private controller.Exec exec;
}
