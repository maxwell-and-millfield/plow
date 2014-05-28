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

import org.jaudiotagger.tag.FieldKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import plow.model.Track;

public class TraktorLibraryWriter {

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
		System.out.println(nodeCollection.getChildNodes().getLength());
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
			// System.out.println(traktorFilename);

			if (!collection.contains(traktorFilename)) {
				final Path p = Paths.get(filename);
				final String drive = p.getRoot().toString().replace("\\", "").replace("/", "");
				final String dir = toTraktorFileName("/" + p.getRoot().relativize(p.getParent()).toString() + "/");
				String volumeId = "";
				try {
					final FileStore cDriveFS = Files.getFileStore(p.getRoot());
					final Integer volID = (Integer) cDriveFS.getAttribute("volume:vsn");
					volumeId = new BigInteger(volID.toString()).toString(16);
				} catch (final IOException e) {
					e.printStackTrace();
				}
				final String trackFilename = p.getFileName().toString();
				final Element e = lib.createElement("ENTRY");
				e.setAttribute("ARTIST", track.getValue().getId3TagValue(FieldKey.ARTIST));
				e.setAttribute("TITLE", track.getValue().getId3TagValue(FieldKey.TITLE));
				nodeCollection.appendChild(e);
				final Element location = lib.createElement("LOCATION");
				location.setAttribute("DIR", dir);
				location.setAttribute("VOLUMEID", volumeId);
				location.setAttribute("FILE", trackFilename);
				location.setAttribute("VOLUME", drive);
				e.appendChild(location);
				added++;
			}
		}
		if (added > 0) {
			((Element) nodeCollection).setAttribute(
					"ENTRIES",
					Integer.toString(Integer.parseInt(nodeCollection.getAttributes().getNamedItem("ENTRIES")
							.getNodeValue())
							+ added));
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
}
