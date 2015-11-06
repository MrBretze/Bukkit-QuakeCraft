package fr.bretzel.quake;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by MrBretzel on 31/10/2015.
 */
public class Util {

    public static void create(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
        }
    }

    public static void write(File file, String value) throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter output = new BufferedWriter(fw);
        output.write(value);
        output.flush();
        output.close();
    }

    public static Location toLocationString(String string) {
        String[] strings = string.split(";");
        return new Location(Bukkit.getWorld(strings[0]),
                Double.valueOf(strings[1]),
                Double.valueOf(strings[2]),
                Double.valueOf(strings[3]),
                Float.valueOf(strings[4]),
                Float.valueOf(strings[5]));
    }

    public static String toStringLocation(Location location) {
        return (location.getWorld().getName() + ";") +
                location.getBlockX() + ";" +
                location.getBlockY() + ";" +
                location.getBlockZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch();
    }
}
