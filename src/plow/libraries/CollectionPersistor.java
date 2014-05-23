package plow.libraries;

import java.lang.reflect.Type;
import java.nio.file.Path;

import plow.model.Playlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CollectionPersistor implements JsonDeserializer<Collection>, JsonSerializer<Playlist> {

	private Gson gson;

	public CollectionPersistor() {
		final GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Collection.class, this);

	}

	public void save(final Collection c, final Path f) {

	}

	@Override
	public JsonElement serialize(final Playlist arg0, final Type arg1,
			final JsonSerializationContext arg2) {
		return null;
	}

	@Override
	public Collection deserialize(final JsonElement arg0, final Type arg1,
			final JsonDeserializationContext arg2) throws JsonParseException {
		return null;
	}

}
