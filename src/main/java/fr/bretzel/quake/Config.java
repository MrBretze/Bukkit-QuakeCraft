package fr.bretzel.quake;

import java.io.File;
import java.util.HashMap;

/**
 * Created by MrBretzel on 31/10/2015.
 */
public class Config implements Savable {

    private String fileName = "null";
    private File file;
    private File directory;

    public Config(File directory, String confName) {
        this.directory = directory;
        if (!confName.endsWith(".conf"))
            confName += ".conf";
        if (!directory.exists())
            directory.mkdir();

        this.file = new File(directory, confName);

        if (!file.exists())
            Util.create(file);

    }

    @Override
    public HashMap<String, Object> save() {
        return null;
    }
}
