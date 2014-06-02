package plow.model.tag.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javafx.concurrent.Task;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

abstract public class TagProvider extends Task<List<TagSearchResult>> {

	public TagProvider(final String search) {
		this.search = search;
	}

	abstract public List<TagSearchResult> getResults(final String s);

	protected String search;

	protected String httpRequest(final String url) {
		String result = "";
		try {
			final URL u = new URL(url);
			final BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
			String line;
			while ((line = in.readLine()) != null && !isCancelled()) {
				result = result + line;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected JsonObject getJson(final String url) {
		return new Gson().fromJson(httpRequest(url), JsonObject.class);
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(final String search) {
		this.search = search;
	}

	@Override
	protected List<TagSearchResult> call() throws Exception {
		final List<TagSearchResult> results = this.getResults(search);
		System.out.println(results);
		this.updateValue(results);

		return results;
	}

}
