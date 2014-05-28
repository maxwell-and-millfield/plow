package plow.libraries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.jaudiotagger.tag.FieldKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import plow.model.Playlist;
import plow.model.Track;

public class TraktorLibraryWriter {

	protected static String MAC_HD_IDENTIFIER = "Macintosh HD";

	public void writeToTraktorLibrary(final MusicLibrary mLib) {
		// back it up!
		final FileSystem fs = FileSystems.getDefault();
		try {
			Files.copy(fs.getPath(mLib.getTraktorLibrary()), fs.getPath(mLib.getTraktorLibrary() + ".bak"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			throw new RuntimeException("Could not backup " + mLib.getTraktorLibrary());
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
		final File f = new File(mLib.getTraktorLibrary());
		Document lib;
		try {
			lib = builder.parse(f);
		} catch (SAXException | IOException e) {
			throw new RuntimeException("Could not parse traktor library.");
		}

		final Element nml = (Element) lib.getFirstChild();
		Element nodeCollection = null, nodePlaylists = null;
		if (nml.getElementsByTagName("COLLECTION").getLength() != 1
				|| nml.getElementsByTagName("PLAYLISTS").getLength() != 1) {
			throw new RuntimeException("Invalid NML");
		}

		nodeCollection = (Element) nml.getElementsByTagName("COLLECTION").item(0);
		nodePlaylists = (Element) nml.getElementsByTagName("PLAYLISTS").item(0);

		final HashSet<String> collection = new HashSet<>();
		for (int i = 0; i < nodeCollection.getChildNodes().getLength(); i++) {
			final Node n = nodeCollection.getChildNodes().item(i);
			if (n.getNodeName() != "ENTRY") {
				continue;
			}
			for (int j = 0; j < n.getChildNodes().getLength(); j++) {
				final Node entryChild = n.getChildNodes().item(j);
				if (entryChild.getNodeName() == "LOCATION") {
					final String filename = entryChild.getAttributes().getNamedItem("VOLUME").getNodeValue()
							+ entryChild.getAttributes().getNamedItem("DIR").getNodeValue()
							+ entryChild.getAttributes().getNamedItem("FILE").getNodeValue();
					collection.add(filename);
				}
			}
		}
		int added = 0;
		for (final Entry<String, Track> track : mLib.getTracks().entrySet()) {
			final String filename = mLib.getLibrary() + track.getKey();
			final String traktorFilename = toTraktorFileName(filename);

			if (!collection.contains(traktorFilename)) {
				final Element e = lib.createElement("ENTRY");
				e.setAttribute("ARTIST", track.getValue().getId3TagValue(FieldKey.ARTIST));
				e.setAttribute("TITLE", track.getValue().getId3TagValue(FieldKey.TITLE));
				decorateEntryWithLocation(e, filename);
				nodeCollection.appendChild(e);
				added++;
			}
		}
		if (added > 0) {
			nodeCollection.setAttribute(
					"ENTRIES",
					Integer.toString(Integer.parseInt(nodeCollection.getAttributes().getNamedItem("ENTRIES")
							.getNodeValue())
							+ added));
		}
		final HashMap<String, Element> playlists = new HashMap<>();
		final XPath xp = XPathFactory.newInstance().newXPath();
		final NodeList playlistNodes = nodePlaylists.getElementsByTagName("NODE");
		Element rootFolder = null;
		for (int i = 0; i < playlistNodes.getLength(); i++) {
			final Element playlistNode = (Element) playlistNodes.item(i);
			if (playlistNode.getAttribute("TYPE").equals("FOLDER") && playlistNode.getAttribute("NAME").equals("$ROOT")) {
				rootFolder = (Element) playlistNode.getElementsByTagName("SUBNODES").item(0);
			}
			if (playlistNode.getAttribute("TYPE").equals("PLAYLIST")) {
				final Element innerPlaylist = (Element) playlistNode.getElementsByTagName("PLAYLIST").item(0);
				playlists.put(innerPlaylist.getAttribute("UUID"), playlistNode);
			}
		}
		for (final Playlist p : mLib.getPlaylists()) {
			Element innerPlaylist;
			Element playlistNode;
			if (playlists.containsKey(p.getId())) {
				playlistNode = playlists.get(p.getId());
				innerPlaylist = (Element) playlistNode.getElementsByTagName("PLAYLIST").item(0);
			} else {
				playlistNode = lib.createElement("NODE");
				playlistNode.setAttribute("TYPE", "PLAYLIST");
				innerPlaylist = lib.createElement("PLAYLIST");
				innerPlaylist.setAttribute("UUID", p.getId());
				innerPlaylist.setAttribute("TYPE", "LIST");
				playlistNode.appendChild(innerPlaylist);
				rootFolder.appendChild(playlistNode);
			}
			playlistNode.setAttribute("NAME", p.getName());
			innerPlaylist.setAttribute("ENTRIES", Integer.toString(p.getTracks().size()));
			final NodeList oldTracks = innerPlaylist.getElementsByTagName("ENTRY");
			for (int i = 0; i < oldTracks.getLength(); i++) {
				innerPlaylist.removeChild(oldTracks.item(i));
			}
			for (final Track t : p.getTracks()) {
				final Element entry = lib.createElement("ENTRY");
				innerPlaylist.appendChild(entry);
				final Element key = lib.createElement("PRIMARYKEY");
				entry.appendChild(key);
				key.setAttribute("TYPE", "TRACK");
				key.setAttribute("KEY", toTraktorFileName(mLib.getLibrary() + t.getFilenameWithPrefix()));
			}
		}

		writeToFile(lib, f);
	}

	protected String toTraktorFileName(final String filename) {
		return filename.replace("\\", "/").replace("/", "/:");
	}

	protected void writeToFile(final Node doc, final File out) {
		final TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			FileOutputStream fos;
			fos = new FileOutputStream(out);
			transformer.transform(new DOMSource(doc), new StreamResult(fos));
		} catch (final FileNotFoundException | TransformerException e2) {
			e2.printStackTrace();
		}

	}

	public void decorateEntryWithLocation(final Element e, final String filename) {
		final Path p = Paths.get(filename);
		if (isWindowsFileSystem(p.getFileSystem())) {
			final String drive = p.getRoot().toString().replace("\\", "").replace("/", "");
			final String dir = toTraktorFileName("/" + p.getRoot().relativize(p.getParent()).toString() + "/");
			String volumeId = "";
			try {
				final FileStore cDriveFS = Files.getFileStore(p.getRoot());
				final Integer volID = (Integer) cDriveFS.getAttribute("volume:vsn");
				volumeId = new BigInteger(volID.toString()).toString(16);
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
			final String trackFilename = p.getFileName().toString();
			final Element location = e.getOwnerDocument().createElement("LOCATION");
			location.setAttribute("DIR", dir);
			location.setAttribute("VOLUMEID", volumeId);
			location.setAttribute("FILE", trackFilename);
			location.setAttribute("VOLUME", drive);
			e.appendChild(location);
		} else {
			final String dir = toTraktorFileName("/" + p.getParent().toString() + "/");
			final String trackFilename = p.getFileName().toString();
			final Element location = e.getOwnerDocument().createElement("LOCATION");
			location.setAttribute("DIR", dir);
			location.setAttribute("VOLUMEID", MAC_HD_IDENTIFIER);
			location.setAttribute("FILE", trackFilename);
			location.setAttribute("VOLUME", MAC_HD_IDENTIFIER);
			e.appendChild(location);
		}
	}

	protected boolean isWindowsFileSystem(final FileSystem fs) {
		// i don't know if i should be proud of this :D
		return fs.getClass().toString().contains("Windows");
	}
}
