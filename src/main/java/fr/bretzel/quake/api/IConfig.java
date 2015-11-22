package fr.bretzel.quake.api;

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

    List<Object> getList(String reg);

    /**
     * TODO: SQL api pour tout c'est truc !
     */
}
