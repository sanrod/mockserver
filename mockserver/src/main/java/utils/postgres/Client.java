package utils.postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private static Connection connection;

    private static void setUpConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://postgresql:5432/postgres";
        String username = "postgres";
        String password = "";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    public static void closeConnection() throws SQLException {
        connection.close();
    }

    private static void createTable(String createTableScript) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(createTableScript);
        statement.close();
        closeConnection();
    }

    private static void executeUpdate(String script) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(script);
        statement.close();
        closeConnection();
    }

    private static List<HashMap<String, String>> executeSelect(String script) throws SQLException {
        List<HashMap<String, String>> result = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(script);

        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();

        while (resultSet.next()) {
            HashMap<String, String> temp = new HashMap<>();
            for (int i=1; i<=columnCount; i++) {
                temp.put(rsmd.getColumnName(i), resultSet.getString(i));
            }
            result.add(temp);
        }
        statement.close();
        closeConnection();
        return result;
    }

    public static void createBirds() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Birds ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "canSign BOOLEAN,"
                + "canSpeak BOOLEAN,"
                + "size VARCHAR(255),"
                + "price INT)";
        createTable(createTableSQL);
    }

    public static void createCats() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Cats ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "paws INT,"
                + "carnivore BOOLEAN,"
                + "crazy BOOLEAN,"
                + "price INT,"
                + "weight INT)";
        createTable(createTableSQL);
    }

    public static void createDogs() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Dogs ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "paws INT,"
                + "carnivore BOOLEAN,"
                + "aggressive BOOLEAN,"
                + "barkingALot BOOLEAN,"
                + "price INT,"
                + "weight INT)";
        createTable(createTableSQL);
    }

    public static void createCars() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Cars ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "sideOfSteeringWheel VARCHAR(255),"
                + "horsePowers INT,"
                + "availableSince VARCHAR(255),"
                + "discount INT,"
                + "price INT"
                + ")";
        createTable(createTableSQL);
    }

    public static void createFoods() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Foods ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "expirationDate VARCHAR(255),"
                + "calories INT,"
                + "sugarAmount INT,"
                + "fat INT,"
                + "price INT"
                + ")";
        createTable(createTableSQL);
    }

    public static void createToys() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Toys ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "forAdults BOOLEAN,"
                + "price INT)";
        createTable(createTableSQL);
    }

    public static void createShops() throws SQLException {
        setUpConnection();
        String createTableSQL = "CREATE TABLE Shops ("
                + "id serial PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "address VARCHAR(255),"
                + "goods VARCHAR(6000))";
        createTable(createTableSQL);
    }

    public static void deleteFrom(String tableName, String condition) throws SQLException {
        setUpConnection();
        String script = String.format("DELETE FROM %s WHERE %s", tableName, condition);
        executeUpdate(script);
    }

    public static void updateRow(String tableName, String condition, HashMap<String, String> values) throws SQLException {
        setUpConnection();
        StringBuilder scriptUpdate = new StringBuilder(String.format("UPDATE %s SET ", tableName));
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            scriptUpdate.append(key).append("=").append("'").append(value).append("'").append(",");
        }
        scriptUpdate.replace(scriptUpdate.lastIndexOf(","), scriptUpdate.length(), "");
        scriptUpdate.append(String.format(" WHERE %s", condition));
        executeUpdate(scriptUpdate.toString());
    }

    public static void insert(String tableName, List<String> values) throws SQLException {
        setUpConnection();
        StringBuilder insertScript = new StringBuilder(String.format("INSERT INTO %s VALUES (", tableName));
        for (String val : values) {
            insertScript.append(String.format("'%s', ", val));
        }
        insertScript.replace(insertScript.lastIndexOf(","), insertScript.length(), "");
        insertScript.append(")");
        executeUpdate(insertScript.toString());
    }

    public static List<HashMap<String, String>> select(String tableName, String condition) throws SQLException {
        setUpConnection();
        String selectScript = String.format("SELECT * FROM %s WHERE %s", tableName, condition);
        return executeSelect(selectScript);
    }
}

