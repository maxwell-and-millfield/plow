package plow.libraries.serializer;

import java.lang.reflect.Type;

import javafx.beans.property.StringProperty;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class StringPropertySerializer implements JsonSerializer<StringProperty> {

	@Override
	public JsonElement serialize(final StringProperty arg0, final Type arg1, final JsonSerializationContext arg2) {
		return new JsonPrimitive(arg0.get());
	}

}
