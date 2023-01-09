package edu.cis.Controller;

public class MenuItem {
    private String name;
    private String description;
    private double price;
    private String id;
    private int amountAvailable;
    private String type;

    public MenuItem(String name, String description, double price, String id, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.id = id;
        amountAvailable = 10;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(int amountAvailable) {
        this.amountAvailable = amountAvailable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //        String itemString = "MenuItem{name='Burger', "
    //                "description='Cheesey and delicious. "
    //                "Beyond meat option available.', price=22.0, id='33a', "
    //                "amountAvailable=9, type='breakfast'}";

    public String toString() {
        return "MenuItem{name='" + name + "', description='" + description +
                "', price=" + price + ", id='" + id + "', amountAvailable=" +
                amountAvailable + ", type='" + type + "'}";
    }
}
