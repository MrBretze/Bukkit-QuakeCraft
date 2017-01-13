package fr.bretzel.quake.api;

import java.io.File;
import java.util.List;

/**
 * Created by MrBretzel on 13/11/15.
 */
public interface IConfig {

    void saveConfig(IConfig config);

    String getString(String reg);

    int getInt(String reg);

    double getDouble(String reg);

    float getFloat(String reg);

    short getShort(String reg);

    Object get(String reg);

    <E> List<E> getList(String reg, E object);

    File getFile();
}
