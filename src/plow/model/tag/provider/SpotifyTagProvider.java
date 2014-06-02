package plow.model.tag.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SpotifyTagProvider extends TagProvider {

	protected static final String url = "http://ws.spotify.com/search/1/track.json?q=";

	public SpotifyTagProvider(final String search) {
		super(search);
	}

	@Override
	public List<TagSearchResult> getResults(final String s) {
		System.out.println(s);
		String url = null;
		final List<TagSearchResult> resultList = new ArrayList<>();
		try {
			url = this.url + URLEncoder.encode(s, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			return null;
		}
		final JsonArray res = this.getJson(url).get("tracks").getAsJsonArray();
		for (final JsonElement e : res) {
			resultList.add(toResult(e.getAsJsonObject()));
		}
		return resultList;
	}

	private TagSearchResult toResult(final JsonObject object) {
		String artist = "";
		for (final JsonElement artistElement : object.get("artists").getAsJsonArray()) {
			artist += (artist.isEmpty() ? "" : ", ") + artistElement.getAsJsonObject().get("name").getAsString();
		}
		final String title = object.get("name").getAsString();
		final String album = object.get("album").getAsJsonObject().get("name").getAsString();
		final String year = object.get("album").getAsJsonObject().get("released").getAsString();
		return new TagSearchResult(artist, title, album, year);
	}
}
