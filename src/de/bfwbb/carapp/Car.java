package de.bfwbb.carapp;

public class Car {
    private int id;
    private String brand;
    private String model;
    private int horsepower;
    private int year;

    private static int incrementID;

    public Car(String brand, String model, int horsepower, int year) {
        // "auto increment" für die ID
        incrementID++;
        id = incrementID;
        this.brand = brand;
        this.model = model;
        this.horsepower = horsepower;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(int horsepower) {
        this.horsepower = horsepower;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("""
                                     Car
                                      [ID]: %d
                                      [Brand]: %s
                                      [Model]: %s
                                      [HRSPRS]: %d
                                      [Year]: %d""", getId(), getBrand(), getModel(), getHorsepower(), getYear());
    }
}
