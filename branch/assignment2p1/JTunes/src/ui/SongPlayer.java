package ui;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.midi.*;

import model.Song;

class SongPlayer extends Player{

	private Sequencer sequencer;

	@Override
	public boolean playSong(Song playableSong) {
		{
			try {
				Sequence sequence = MidiSystem.getSequence(playableSong.getContent().getFile());
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
				sequencer.setSequence(sequence);
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
		return true;
	}

	@Override
	public boolean stop() {
		sequencer.stop();
		return true;
	}


}
