package model;

import java.nio.file.*;

public class SongWithFile {
	public final Song song;
	public Path file;
	
	public SongWithFile(Song aSong, Path aPath) {
		song = aSong;
		file = aPath;
	}

}
