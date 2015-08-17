package au.com.quaysystems.qrm.server;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DoubleDeserializer implements JsonDeserializer<Double> {
	@Override
	public Double deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {		
		try {
			return arg0.getAsDouble();
		} catch (Exception e) {
			return 0.0;
		}
		
	}
}