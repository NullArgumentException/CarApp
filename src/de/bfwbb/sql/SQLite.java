package de.bfwbb.sql;

import de.bfwbb.carapp.Car;

import java.sql.*;
import java.util.ArrayList;

public class SQLite {
    private static final String url = "jdbc:sqlite:db/car.db";
    private Connection connection;
    private Statement statement;

    public void connect() {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite established.");
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void createTable() {
        try {
            String sql = """
                    CREATE TABLE IF NOT EXISTS car (
                    id INTEGER PRIMARY KEY,
                    brand TEXT,
                    model TEXT,
                    horsepower INTEGER,
                    modelyear INTEGER);""";
            statement.execute(sql);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void truncateTable() {
        try {
            String sql = "DELETE FROM car;";
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Car[] readCarsFromDB() {
        ArrayList<Car> cars = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM car");

            while (resultSet.next()) {
                Car c = new Car(resultSet.getString("brand"),
                                resultSet.getString("model"),
                                resultSet.getInt("horsepower"),
                                resultSet.getInt("modelyear"));
                cars.add(c);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

            return new Car[0];
        }
        return cars.toArray(new Car[0]);
    }

    public void writeCarsToDB(Car[] cars) {
        truncateTable();
        String sql = "INSERT INTO car(brand, model, horsepower, modelyear) VALUES (?, ?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Car car : cars) {
                preparedStatement.setString(1, car.getBrand());
                preparedStatement.setString(2, car.getModel());
                preparedStatement.setInt(3, car.getHorsepower());
                preparedStatement.setInt(4, car.getYear());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}