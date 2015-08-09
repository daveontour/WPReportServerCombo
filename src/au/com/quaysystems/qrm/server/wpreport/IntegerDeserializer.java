package au.com.quaysystems.qrm.server.wpreport;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

class IntegerDeserializer implements JsonDeserializer<Integer> {
	@Override
	public Integer deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		
		try {
			return arg0.getAsInt();
		} catch (Exception e) {
			return 0;
		}
		
	}
}