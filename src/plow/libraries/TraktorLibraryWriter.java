package plow.libraries;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import plow.model.Playlist;

public class TraktorLibraryWriter {

	public void writeToTraktorLibrary(final String traktorLibraryFile, final String libraryPath,
			final List<Playlist> playlists) {
		// back it up!
		final FileSystem fs = FileSystems.getDefault();
		try {
			Files.copy(fs.getPath(traktorLibraryFile), fs.getPath(traktorLibraryFile + ".bak"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new RuntimeException("Could not backup " + traktorLibraryFile);
		}

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(false);
		final DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		final File f = new File(traktorLibraryFile);
		Document lib;
		try {
			lib = builder.parse(f);
		} catch (SAXException | IOException e) {
			throw new RuntimeException("Could not parse traktor library.");
		}
		System.out.println("XML read");
		// find Collection
		final Node nml = lib.getFirstChild();
		Node nodeCollection = null, nodePlaylists = null;
		for (int i = 0; i < nml.getChildNodes().getLength(); i++) {
			final Node n = nml.getChildNodes().item(i);
			if (n.getNodeName() == "COLLECTION") {
				nodeCollection = n;
			}
			if (n.getNodeName() == "PLAYLISTS") {
				nodePlaylists = n;
			}
		}
		if (nodeCollection == null || nodePlaylists == null) {
			throw new RuntimeException("Invalid NML");
		}
		System.out.println(nodeCollection.getAttributes().getNamedItem("ENTRIES").getNodeValue());
		System.out.println(nodeCollection.getChildNodes().getLength());

	}
}
