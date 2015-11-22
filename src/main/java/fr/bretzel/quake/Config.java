package fr.bretzel.quake;

import fr.bretzel.quake.api.IConfig;

import java.util.List;

/**
 * Created by MrBretzel on 31/10/2015.
 */
public class Config implements IConfig {

    public Config() {
    }

    @Override
    public void saveConfig(IConfig config) {
        //TODO
    }

    @Override
    public String getString(String reg) {
        return null;
    }

    @Override
    public int getInt(String reg) {
        return 0;
    }

    @Override
    public double getDouble(String reg) {
        return 0;
    }

    @Override
    public float getFloat(String reg) {
        return 0;
    }

    @Override
    public short getShort(String reg) {
        return 0;
    }

    @Override
    public Object get(String reg) {
        return null;
    }

    @Override
    public List<Object> getList(String reg) {
        return null;
    }
}
