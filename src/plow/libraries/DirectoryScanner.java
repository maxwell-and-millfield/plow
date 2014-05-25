package plow.libraries;

import java.io.File;

import javafx.application.Platform;

import org.eclipse.core.runtime.Path;

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
			final String filenameWithPrefix = ((prefix != "") ? prefix + Path.SEPARATOR : "") + f.getName();
			if (f.isDirectory()) {
				readAndSynchronize(lib, f, filenameWithPrefix);
			} else if (isTrackFile(f)) {
				if (!lib.getTracks().containsKey(filenameWithPrefix)) {
					final Track t = new Track(lib, prefix + Path.SEPARATOR, f.getName());
					t.setLastModified(f.lastModified());
					lib.addTrack(t);
					if (potentialPlaylist == null) {
						for (final Playlist p : lib.getPlaylists()) {
							if (p.getName().equals(prefix)) {
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
					final Playlist addToPlaylist = potentialPlaylist;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							addToPlaylist.getTracks().add(t);
						}
					});

					System.out.println("Added " + filenameWithPrefix);

				} else {
					final Track track = lib.getTracks().get(filenameWithPrefix);

					if (track.getLastModified() != f.lastModified()) {
						track.setLastModified(f.lastModified());
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								System.out.println(track.getLastModified() + " != " + f.lastModified());
								System.out.println("Updated: " + f.getName());
								track.updateTags();
								track.setLastModified(f.lastModified());
							}
						});
					}
				}

			}
		}
	}

	private boolean isTrackFile(final File file) {
		return file.getName().toLowerCase().endsWith(".mp3");
	}

}
