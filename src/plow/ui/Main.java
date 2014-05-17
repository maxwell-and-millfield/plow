package plow.ui;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import plow.model.Playlist;
import plow.model.Playlists;
import plow.model.Track;

public class Main {
	private DataBindingContext m_bindingContext;

	protected Shell shlPlow;
	private final Playlists playlists = new Playlists();
	private Table tblTracks;
	private ListViewer listViewer;
	private TableViewer tableViewer;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			@Override
			public void run() {
				try {
					Main window = new Main();
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPlow.open();
		shlPlow.layout();
		while (!shlPlow.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPlow = new Shell();
		Playlist p = new Playlist();
		playlists.getPlaylists().add(p);
		Track t = new Track();
		t.setTitle("Atemlos");
		t.setArtist("Helene Fischer");
		p.setName("Schlager");
		p.getTracks().add(t);
		p = new Playlist();
		p.setName("Grindcore");
		t = new Track();
		t.setArtist("Excrementory Grindfuckers");
		t.setTitle("Looking for grindcore");
		p.getTracks().add(t);
		shlPlow.setSize(450, 300);
		shlPlow.setText("Plow");
		shlPlow.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(shlPlow, SWT.NONE);

		listViewer = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);
		org.eclipse.swt.widgets.List lstPlaylists = listViewer.getList();

		SashForm sashForm_1 = new SashForm(sashForm, SWT.VERTICAL);

		Composite composite = new Composite(sashForm_1, SWT.NONE);
		TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);

		tableViewer = new TableViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION);
		tblTracks = tableViewer.getTable();
		tblTracks.setHeaderVisible(true);
		tblTracks.setLinesVisible(true);

		TableColumn tblclmnArtist = new TableColumn(tblTracks, SWT.NONE);
		tcl_composite.setColumnData(tblclmnArtist, new ColumnPixelData(150,
				true, true));
		tblclmnArtist.setText("Artist");

		TableColumn tblclmnTitle = new TableColumn(tblTracks, SWT.NONE);
		tcl_composite.setColumnData(tblclmnTitle, new ColumnPixelData(150,
				true, true));
		tblclmnTitle.setText("Title");

		Group grpTrack = new Group(sashForm_1, SWT.NONE);
		grpTrack.setText("Track");
		sashForm_1.setWeights(new int[] { 1, 1 });
		sashForm.setWeights(new int[] { 1, 1 });

		m_bindingContext = initDataBindings();
		playlists.addPlaylist(p);

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(
				listContentProvider_1.getKnownElements(), Track.class,
				new String[] { "artist", "title" });
		tableViewer
				.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider_1);
		//
		IObservableValue observeSingleSelectionListViewer = ViewerProperties
				.singleSelection().observe(listViewer);
		IObservableList listViewerTracksObserveDetailList = BeanProperties
				.list(Playlist.class, "tracks", Track.class).observeDetail(
						observeSingleSelectionListViewer);
		tableViewer.setInput(listViewerTracksObserveDetailList);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(
				listContentProvider.getKnownElements(), Playlist.class, "name");
		listViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		listViewer.setContentProvider(listContentProvider);
		//
		IObservableList playlistsPlaylistsObserveList = BeanProperties.list(
				"playlists").observe(playlists);
		listViewer.setInput(playlistsPlaylistsObserveList);
		//
		return bindingContext;
	}
}
