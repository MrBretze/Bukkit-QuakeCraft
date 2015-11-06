package fr.bretzel.quake;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * Created by MrBretzel on 05/11/2015.
 */
public class JSON {

    private HashMap<String, Object> map = new HashMap<>();

    public JSON(String js) {
        JSONParser parser = new JSONParser();
        try {
            map = (HashMap<String, Object>) parser.parse(new StringReader(js));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public int getInt(String key) {
        return (int) get(key);
    }

    public double getDouble(String key) {
        return (double) get(key);
    }

    public float getFloat(String key) {
        return (float) get(key);
    }


    public String getString(String key) {
        return (String) get(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public boolean save(File file) {
        try {
            JSONObject object = new JSONObject(map);
            JsonElement element = new com.google.gson.JsonParser().parse(object.toJSONString());
            String js = new GsonBuilder().setPrettyPrinting().create().toJson(element);
            Util.write(file, js);
        } catch (Exception e) {
            e.fillInStackTrace();
            return false;
        } finally {
            return true;
        }
    }
}
