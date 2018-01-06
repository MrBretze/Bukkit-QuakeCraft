package fr.bretzel.quake.config.local;

import fr.bretzel.quake.config.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocalConfig implements Config.IConfig {

    @Override
    public Config.IConfig init(Config config) {
        return this;
    }

    @Override
    public String getString(String query) {
        return null;
    }

    @Override
    public int getInt(String query) {
        return 0;
    }

    @Override
    public double getDouble(String query) {
        return 0;
    }

    @Override
    public void setString(String query, String value) {

    }

    @Override
    public void setInt(String query, int value) {

    }

    @Override
    public void setDouble(String query, double value) {

    }

    @Override
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        return null;
    }
}
