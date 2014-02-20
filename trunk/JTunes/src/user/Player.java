package user;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.sound.midi.*;

public class Player {

	public static void playSong(File songFile) {
		 try {
		        // From file
		        Sequence sequence = MidiSystem.getSequence(songFile);
		    
		        // Create a sequencer for the sequence
		        Sequencer sequencer = MidiSystem.getSequencer();
		        sequencer.open();
		        sequencer.setSequence(sequence);
		        
		        // Start playing
		        sequencer.start();
		        
		    } catch (MalformedURLException e) {
		    	System.out.println("Caught Malformed URL");
		    } catch (IOException e) {
		    	System.out.println("Caught I/O Exception");
		    } catch (MidiUnavailableException e) {
		    	System.out.println("Caught MidiUnavailableException:");
		    } catch (InvalidMidiDataException e) {
		    	System.out.println("Caught InvalidMidiDataExcepion: Midi file specified is invalid.");
		    }
	}
}
