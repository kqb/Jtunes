package model;

/**
 * Any collection of songs that can be played one by one.
 * All UI and Player functions should be built around this interface.
 * If something is needed then add to this interface.
 * A nested class inside Library implements this interface; the
 * implementation needs to be changed if this interface is changed.
 * 
 * @author Michael Yu
 *
 */
public interface PlayableCollection {
	// Interface
	/**
	 * @return an iterable of PlayableSong in the default order
	 */
	Iterable<Song> getDefaultIterable();
	boolean hasSongId(String id);
}
