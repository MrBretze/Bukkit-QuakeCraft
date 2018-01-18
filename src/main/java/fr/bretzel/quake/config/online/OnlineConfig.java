package fr.bretzel.quake.config.online;

import fr.bretzel.quake.config.Config;

import java.sql.*;

public class OnlineConfig implements Config.IConfig {

    private Connection connection;

    private String hostname, username, password, database, port;

    /**
     * Creates a new MySQL instance for a specific database
     *
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public OnlineConfig(String hostname, String username, String password, String port, String database) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }


    public boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }


    public Connection getConnection() {
        return connection;
    }

    @Override
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        if (checkConnection()) {
            return getConnection();
        }

        String connectionURL = "jdbc:mysql://"
                + this.hostname + ":" + this.port;

        if (database != null || !database.isEmpty()) {
            connectionURL = connectionURL + "/" + this.database + "?autoReconnect=true&useUnicode=yes";
        }

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection(connectionURL, this.username, this.password);

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
