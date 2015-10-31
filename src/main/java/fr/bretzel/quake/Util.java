package fr.bretzel.quake;

import java.io.File;
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

}
