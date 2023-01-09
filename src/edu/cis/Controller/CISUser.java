package edu.cis.Controller;

import java.util.ArrayList;

public class CISUser {
    public CISUser(String userID, String name, String yearLevel) {
        this.userID = userID;
        this.name = name;
        this.yearLevel = yearLevel;
        money = 50.0;
        orders = new ArrayList<>();
    }

    private String userID;
    private String name;
    private String yearLevel;
    private ArrayList<Order> orders;
    private double money;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void addOrders(Order order) {
        orders.add(order);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String toString() {
        String orderString = "";
        for (int i = 0; i < orders.size(); i++) {
            orderString = orderString + orders.get(i).toString();
        }
        return "CISUser{userID='" + userID + "', name='" + name +
                "', yearLevel='" + yearLevel + "', orders= " + orderString
                + ", money=" + money + "}";
    }
    //"CISUser{userID='qwert5', name='latisha', " +
    //  "yearLevel='y13', orders= Order{itemID='33a', " +
    //  "type='breakfast', orderID='9078ppp'}, money=28.0}";

}
