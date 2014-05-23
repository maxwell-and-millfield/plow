package plow.libraries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import plow.model.Playlist;
import plow.model.Track;

public class DirectoryScanner {

	public List<Playlist> scanDirectory(String dir) {
		File folder = new File(dir);
		if (!folder.isDirectory())
			throw new RuntimeException("Not a valid library directory");
		ArrayList<Playlist> playlists = new ArrayList<>();
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				playlists.add(getPlaylist(f));
			}
		}
		return playlists;
	}

	private Playlist getPlaylist(File folder) {
		Playlist p = new Playlist();
		p.setName(folder.getName());
		for (File f : folder.listFiles()) {
			if (!f.isDirectory() && isTrackFile(f))
				p.getTracks().add(getTrack(f, folder.getName()));
		}
		return p;
	}

	private Track getTrack(File file, String prefix) {
		AudioFile audioFile = null;
		
		try {
			audioFile = AudioFileIO.read(file);
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
			e.printStackTrace();
		}

		Track track = new Track(audioFile);
		// prefix the file name with the folder name and a slash
		track.setFilenamePrefix(prefix + File.separator);

		return track;
	}

	private boolean isTrackFile(File file) {
		return file.getName().toLowerCase().endsWith(".mp3");
	}

}
