package plow.libraries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import plow.model.Playlist;
import plow.model.Track;

public class DirectoryScanner {

	public void synchronizeLibrary(final MusicLibrary lib) {
		final File folder = new File(lib.getLibrary());
		if (!folder.isDirectory()) {
			throw new RuntimeException("Not a valid library directory");
		}
		readAndSynchronize(lib, new File(lib.getLibrary()), "");
	}

	public void readAndSynchronize(final MusicLibrary lib, final File folder, final String prefix) {
		Playlist potentialPlaylist = null;
		for (final File f : folder.listFiles()) {
			final String filenameWithPrefix = ((prefix != "") ? prefix + "/" : "") + f.getName();
			System.out.println(filenameWithPrefix);
			if (f.isDirectory()) {
				readAndSynchronize(lib, f, filenameWithPrefix);
			} else if (isTrackFile(f)) {
				if (!lib.getTracks().containsKey(filenameWithPrefix)) {
					final Track t = getTrack(f, prefix);
					lib.addTrack(t);
					if (potentialPlaylist == null) {
						for (final Playlist p : lib.getPlaylists()) {
							if (p.getName() == prefix) {
								potentialPlaylist = p;
							}
						}
					}
					if (potentialPlaylist == null) {
						potentialPlaylist = new Playlist();
						potentialPlaylist.setName(prefix);
						final Playlist addToPlaylist = potentialPlaylist;
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								lib.getPlaylists().add(addToPlaylist);
							}
						});

					}
					potentialPlaylist.getTracks().add(t);
				}
			}
		}
	}

	public List<Playlist> scanDirectory(final String dir) {
		final File folder = new File(dir);
		if (!folder.isDirectory()) {
			throw new RuntimeException("Not a valid library directory");
		}
		final ArrayList<Playlist> playlists = new ArrayList<>();
		for (final File f : folder.listFiles()) {
			if (f.isDirectory()) {
				playlists.add(getPlaylist(f));
			}
		}
		return playlists;
	}

	private Playlist getPlaylist(final File folder) {
		final Playlist p = new Playlist();
		p.setName(folder.getName());
		for (final File f : folder.listFiles()) {
			if (!f.isDirectory() && isTrackFile(f)) {
				p.getTracks().add(getTrack(f, folder.getName()));
			}
		}
		return p;
	}

	private Track getTrack(final File file, final String prefix) {
		AudioFile audioFile = null;

		try {
			audioFile = AudioFileIO.read(file);
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
			e.printStackTrace();
		}

		final Track track = new Track(audioFile);
		// prefix the file name with the folder name and a slash
		track.setFilenamePrefix(prefix + File.separator);

		return track;
	}

	private boolean isTrackFile(final File file) {
		return file.getName().toLowerCase().endsWith(".mp3");
	}

}
