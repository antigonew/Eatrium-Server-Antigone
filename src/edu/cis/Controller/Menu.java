package edu.cis.Controller;

import java.util.ArrayList;

public class Menu {
    private ArrayList<MenuItem> eatriumItems;
    private String adminID;

    public Menu(ArrayList<MenuItem> eatriumItems, String adminID) {
        this.eatriumItems = eatriumItems;
        this.adminID = adminID;
    }

    public ArrayList<MenuItem> getEatriumItems() {
        return eatriumItems;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminID() {
        return adminID;
    }


}
