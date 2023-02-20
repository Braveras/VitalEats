package com.vitaleats.utilities;

public class Food {
    private String id;
    private String name;
    private int quantity;

    public Food(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Food(String id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Food(String name) {
        this.name = name;
    }

    // Método para obtener el id del objeto Food
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}