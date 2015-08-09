package au.com.quaysystems.qrm.server.wpreport;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import au.com.quaysystems.qrm.wp.QRMImport;

public class ConvertFromJSONGSON {


	public static void main(String[] args) {
		try {
			
			ConvertFromJSONGSON x = new ConvertFromJSONGSON();
			String json = readFile("C:\\Users\\Dave\\Desktop\\temp.js" );
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Integer.class, x.new IntegerDeserializer());
			Gson gson = builder.create();
			final QRMImport imp = gson.fromJson(json, QRMImport.class);
			System.out.println(gson.toJson(imp));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static String readFile(String path) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
	
	private class IntegerDeserializer implements JsonDeserializer<Integer> {
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

}
