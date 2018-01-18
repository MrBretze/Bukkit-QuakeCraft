package fr.bretzel.quake.config.local;

import fr.bretzel.quake.config.Config;

import java.io.File;
import java.sql.*;

public class LocalConfig implements Config.IConfig {

    private Connection connection;
    private File db;

    public LocalConfig(File db) {
        this.db = db;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    @Override
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        if (checkConnection()) {
            return getConnection();
        }

        String connectionURL = "jdbc:sqlite:" + db;

        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection(connectionURL);

        return connection;
    }

    @Override
    public Config.IConfig init(Config config) throws SQLException, ClassNotFoundException {
            openConnection();

            try {
                Statement statement = openConnection().createStatement();
                statement.executeUpdate(Config.SQL_CREATE_QUAKE_TABLE);
            } catch (Exception e) {}
        return this;
    }
}
