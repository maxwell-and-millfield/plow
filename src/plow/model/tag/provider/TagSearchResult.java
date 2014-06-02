package plow.model.tag.provider;

public class TagSearchResult {

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getAlbum() {
		return album;
	}

	public String getYear() {
		return year;
	}

	public TagSearchResult(final String artist, final String title, final String album, final String year) {
		super();
		this.artist = artist;
		this.title = title;
		this.album = album;
		this.year = year;
	}

	private final String artist, title, album, year;

}
