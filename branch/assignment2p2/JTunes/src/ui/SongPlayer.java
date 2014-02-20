package ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.sound.midi.*;
import javax.sound.sampled.Clip;

import model.Song;
import model.SongContent;
import model.SongInfo;

class SongPlayer {
	Sequence sequence;  
	private Sequencer sequencer;
	private List<Song> songs;
	private File songFile;
	int audioLength;
	int audioPosition = 0; 


	public void initSong(Song song) {
		{
			try {
				songFile = song.getContent().getFile();
				Sequence sequence = MidiSystem.getSequence(songFile);
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
				sequencer.setSequence(sequence);
				sequencer.start();
				audioLength = (int)sequence.getTickLength( );

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
	
	/**
	 * Check whether the sequencer is empty
	 * 	
	 * @return boolean 		true if Song sequencer is not empty, false otherwise
	 * 
	 */	
	public boolean sequencerIsEmpty() {
		return sequencer == null;
	}
	
	/**
	 * Start playing the sequencer
	 * 	
	 */	
	public void play() {
		sequencer.start();
	}
	
	/**
	 * Stop playing the sequencer, while retaining it's position in the song
	 * 	
	 */	
	public void pause() {
		sequencer.stop();
	}
	
	/**
	 * Stop playing the sequencer, while reseting the position of the song back to start
	 * 	
	 */	
	public void stop() {
		sequencer.stop();
		sequencer.setTickPosition(0);
		audioPosition = 0; 
	}
	public void setAudioPosition(int pos) {
		audioPosition = pos;
	}
	
	 /** 
	  * @return length of the midi
	  */
	 public int getLength( ) {
		 return audioLength; 
		 }
	 
	 /** Getter method
	  * @return Sequencer	The sequencer with the currently loaded midi sequence
	  */
	public Sequencer getSequencer() {
		return sequencer;
	}
}

