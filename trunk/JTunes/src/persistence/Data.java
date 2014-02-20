package persistence;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import model.*;

public class Data {
	/**
	 * @param args
	 */
	protected static String ROOT_META = "root.meta";
	protected static Path rootPath;
	protected static void initRootPath() {
		if(rootPath != null) {
			return;
		}
		Path dir = Paths.get(".").toAbsolutePath();
		while (!Files.exists(dir.resolve(ROOT_META))) {
			dir = dir.getParent();
		}
		rootPath = dir;
	}
	/**TODO processMetaData is the more general future
	 * implementation of processing all the information
	 * on songs, playlists, and artists stored in the
	 * file at rootPath.resolve(ROOT_META).
	 */
	protected static void processMetaData() {
		
	}
	
	public static class songFilesIterable
	implements Iterable<SongWithFile> {

		@Override
		public Iterator<SongWithFile> iterator() {
			return new AllSongFilesIterator();
		}
		class AllSongFilesIterator
		implements Iterator<SongWithFile> {
			BufferedReader songListFile;
			int numSongsRemaining = 0;
			
			public AllSongFilesIterator() {
				try {
					songListFile = new BufferedReader(new FileReader(rootPath.resolve(ROOT_META).toString())) ;//Files.newBufferedReader(rootPath.resolve(ROOT_META), Charset.forName("US-ASCII"));
					numSongsRemaining = Integer.parseInt(songListFile.readLine());
				} catch (IOException ex) {
					System.err.println("Failed to read songs list from file.");
					numSongsRemaining = 0;  // not necessary because of initialization
				}
			}
			
			@Override
			public boolean hasNext() {
				return numSongsRemaining > 0;
			}
	
			@Override
			public SongWithFile next() {
				if (numSongsRemaining == 0) {
					throw new NoSuchElementException();
				}
				numSongsRemaining--;
				try {
					/*return new SongWithFile(new Song(Integer.parseInt(songListFile.readLine()), songListFile.readLine(), songListFile.readLine()),
											rootPath.resolve(songListFile.readLine()));*/
					String idString = songListFile.readLine();
					String title = songListFile.readLine();
					String album = songListFile.readLine();
					String relativePath = songListFile.readLine();
					int id = Integer.parseInt(idString);
					return new SongWithFile(new Song(id, title, album), rootPath.resolve(relativePath));
				} catch (IOException ex) {
						System.err.println("Invalid list of songs file.");
						throw new AssertionError();
				}
			}
	
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}
	
	
	public static void init() {
		initRootPath();
		processMetaData();
	}
	public static void main(String[] args) {
		initRootPath();
		System.out.println(rootPath.toAbsolutePath());
	}
}
