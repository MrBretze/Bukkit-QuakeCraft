package fr.bretzel.quake;

import com.google.gson.*;
import fr.bretzel.quake.api.IConfig;
import fr.bretzel.quake.error.InvalidConfiguration;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Config implements IConfig {

    private JsonObject object;
    private Gson gson = new GsonBuilder().create();
    private File file;

    public Config(File file) {
        if (file.exists() || file.isDirectory()) {
            System.out.println("File is not exit or is a directory");
            Bukkit.shutdown();
            return;
        }

        if (!isJsonFileInit(file)) {
            object = new JsonObject();
        }

        this.file = file;
    }

    @Override
    public void saveConfig(IConfig config) {
        //TODO
    }

    @Override
    public String getString(String reg) {
        return get(reg).getAsString();
    }

    @Override
    public int getInt(String reg) {
        return get(reg).getAsInt();
    }

    @Override
    public double getDouble(String reg) {
        return get(reg).getAsDouble();
    }

    @Override
    public float getFloat(String reg) {
        return get(reg).getAsFloat();
    }

    @Override
    public short getShort(String reg) {
        return get(reg).getAsShort();
    }

    @Override
    public JsonElement get(String reg) {
        if (reg.split(".").length > 0) {
            for (String s : reg.split(".")) {
                JsonElement element1 = object.get(s);
                if (element1 instanceof JsonArray) {
                    //TODO: Implements this
                } else if (element1 instanceof JsonObject) {
                    //TODO: Implements this
                } else {
                    Bukkit.broadcastMessage("************************");
                    Bukkit.broadcastMessage("             ERROR          ");
                    Bukkit.broadcastMessage("************************");
                    Bukkit.broadcastMessage("Object" + element1.toString());
                    Bukkit.broadcastMessage("Class: " + element1.getClass().toString());
                    Bukkit.broadcastMessage("************************");
                }
            }
        } else {
            try {
                return object.get(reg);
            } catch (Exception e) {
                throw new InvalidConfiguration(this, reg);
            }
        }
        return null;
    }

    @Override
    public <E> List<E> getList(String reg, E object) {
        JsonArray array = get(reg).getAsJsonArray();
        List<E> list = new ArrayList<>();
        Iterator<JsonElement> iterator = array.iterator();
        while (iterator.hasNext()) {
            JsonElement element = iterator.next();
            if (element != null && element.getClass().isInstance(object)) {
                list.add((E) element);
            }
        }
        return list;
    }

    @Override
    public File getFile() {
        return file;
    }

    private boolean isJsonFileInit(File file) {
        try {
            this.object = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
