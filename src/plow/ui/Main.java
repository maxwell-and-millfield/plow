package plow.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import plow.libraries.DirectoryScanner;
import plow.libraries.TraktorLibraryWriter;
import plow.model.ArgumentSettings;
import plow.model.Playlist;
import plow.model.Playlists;
import plow.model.Settings;
import plow.model.Track;

public class Main {
	private Binding artistBinding;
	private DataBindingContext m_bindingContext;

	protected Shell shlPlow;
	private final Playlists playlists = new Playlists();
	private Table tblTracks;
	private ListViewer listViewer;
	private TableViewer tableViewer;
	private Group grpTrack;
	private Text text;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = Display.getDefault();
		final Settings settings = new ArgumentSettings(args);
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			@Override
			public void run() {
				try {
					final Main window = new Main();
					final TraktorLibraryWriter tw = new TraktorLibraryWriter();
					tw.writeToTraktorLibrary(settings.getTraktorLibraryPath(),
							settings.getLibraryPath(), null);
					final DirectoryScanner ds = new DirectoryScanner();
					for (final Playlist p : ds.scanDirectory(settings.getLibraryPath())) {
						window.playlists.addPlaylist(p);
					}
					window.open();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		final Display display = Display.getDefault();
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

		shlPlow.setSize(450, 300);
		shlPlow.setText("Plow");
		final FillLayout fl_shlPlow = new FillLayout(SWT.HORIZONTAL);
		fl_shlPlow.marginWidth = 3;
		fl_shlPlow.marginHeight = 3;
		shlPlow.setLayout(fl_shlPlow);

		final SashForm sashForm = new SashForm(shlPlow, SWT.NONE);

		listViewer = new ListViewer(sashForm, SWT.BORDER | SWT.V_SCROLL);
		final org.eclipse.swt.widgets.List lstPlaylists = listViewer.getList();

		final SashForm sashForm_1 = new SashForm(sashForm, SWT.VERTICAL);

		final Composite composite = new Composite(sashForm_1, SWT.NONE);
		final TableColumnLayout tcl_composite = new TableColumnLayout();
		composite.setLayout(tcl_composite);

		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tblTracks = tableViewer.getTable();
		tblTracks.setHeaderVisible(true);
		tblTracks.setLinesVisible(true);

		final TableColumn tblclmnArtist = new TableColumn(tblTracks, SWT.NONE);
		tcl_composite.setColumnData(tblclmnArtist, new ColumnPixelData(300, true, true));
		tblclmnArtist.setText("Artist");

		final TableColumn tblclmnTitle = new TableColumn(tblTracks, SWT.NONE);
		tcl_composite.setColumnData(tblclmnTitle, new ColumnPixelData(150, true, true));
		tblclmnTitle.setText("Title");

		final TableColumn tblclmnFilename = new TableColumn(tblTracks, SWT.NONE);
		tcl_composite.setColumnData(tblclmnFilename, new ColumnPixelData(150, true, true));
		tblclmnFilename.setText("Filename");

		grpTrack = new Group(sashForm_1, SWT.NONE);
		grpTrack.setText("Track");

		text = new Text(grpTrack, SWT.BORDER);
		text.setBounds(134, 27, 76, 21);

		final Button btnNewButton = new Button(grpTrack, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				artistBinding.updateTargetToModel();
			}
		});
		btnNewButton.setBounds(134, 92, 75, 25);
		btnNewButton.setText("Speichern");
		sashForm_1.setWeights(new int[] { 1, 1 });
		sashForm.setWeights(new int[] { 1, 3 });

		m_bindingContext = initDataBindings();
	}

	protected DataBindingContext initDataBindings() {
		final DataBindingContext bindingContext = new DataBindingContext();
		//
		final ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		final IObservableMap[] observeMaps = BeansObservables.observeMaps(
				listContentProvider_1.getKnownElements(), Track.class, new String[] { "artist",
						"title", "filename" });
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider_1);
		//
		final IObservableValue observeSingleSelectionListViewer = ViewerProperties
				.singleSelection().observe(listViewer);
		final IObservableList listViewerTracksObserveDetailList = BeanProperties.list(
				Playlist.class, "tracks", Track.class).observeDetail(
				observeSingleSelectionListViewer);
		tableViewer.setInput(listViewerTracksObserveDetailList);
		//
		final ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		final IObservableMap observeMap = BeansObservables.observeMap(
				listContentProvider.getKnownElements(), Playlist.class, "name");
		listViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		listViewer.setContentProvider(listContentProvider);
		//
		final IObservableList playlistsPlaylistsObserveList = BeanProperties.list("playlists")
				.observe(playlists);
		listViewer.setInput(playlistsPlaylistsObserveList);
		//
		final IObservableValue observeEnabledTextObserveWidget = WidgetProperties.enabled()
				.observe(text);
		final IObservableValue observeSingleSelectionIndexTblTracksObserveWidget_1 = WidgetProperties
				.singleSelectionIndex().observe(tblTracks);
		final UpdateValueStrategy strategy_1 = new UpdateValueStrategy();
		strategy_1.setConverter(new IntegerToBooleanConverter());
		bindingContext.bindValue(observeEnabledTextObserveWidget,
				observeSingleSelectionIndexTblTracksObserveWidget_1, null, strategy_1);
		//
		final IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify)
				.observe(text);
		final IObservableValue observeSingleSelectionTableViewer = ViewerProperties
				.singleSelection().observe(tableViewer);
		final IObservableValue tableViewerArtistObserveDetailValue = BeanProperties.value(
				Track.class, "artist", String.class).observeDetail(
				observeSingleSelectionTableViewer);
		artistBinding = bindingContext.bindValue(observeTextTextObserveWidget,
				tableViewerArtistObserveDetailValue, new UpdateValueStrategy(
						UpdateValueStrategy.POLICY_ON_REQUEST), null);
		//
		return bindingContext;
	}
}
