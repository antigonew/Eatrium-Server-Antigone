/*
 * File: CIServer.java
 * ------------------------------
 * When it is finished, this program will implement a basic
 * ecommerce network management server.  Remember to update this comment!
 */

package edu.cis.Controller;

import acm.program.*;
import edu.cis.Model.CISConstants;
import edu.cis.Model.Request;
import edu.cis.Model.SimpleServerListener;
import edu.cis.Utils.SimpleServer;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);

    private ArrayList<CISUser> users = new ArrayList<CISUser>();
    private ArrayList<MenuItem> eatriumItems = new ArrayList<MenuItem>();
    String adminID;
    private Menu menu = new Menu(eatriumItems, adminID);

    /*
     * Starts the server running so that when a program sends
     * a request to this server, the method requestMade is
     * called.
     */
    public void run()
    {
        println("Starting server on port " + PORT);
        server.start();
    }

    /*
     * When a request is sent to this server, this method is
     * called. It must return a String.
     *
     * @param request a Request object built by SimpleServer from an
     * incoming network request by the client
     */
    public String requestMade(Request request)
    {
        String cmd = request.getCommand();
        println(request.toString());

        switch (request.getCommand()) {
            case CISConstants.CREATE_USER -> {
                return createUser(request);
            }
            case CISConstants.ADD_MENU_ITEM -> {
                return addMenuItem(request);
            }
            case CISConstants.PLACE_ORDER -> {
                return placeOrder(request);
            }
            case CISConstants.GET_ORDER -> {
                return getOrder(request);
            }
            case CISConstants.GET_USER -> {
                return getUser(request);
            }
            case CISConstants.GET_ITEM -> {
                return getItem(request);
            }
            case CISConstants.DELETE_ORDER -> {
                return deleteOrder(request);
            }
            case CISConstants.GET_CART -> {
                return getCart(request);
            }
            case CISConstants.TOP_UP -> {
                return topUp(request);
            }
            case CISConstants.GET_MENU -> {
                return getMenu(request);
            }
            case CISConstants.PING -> {
                final String PING_MSG = "Hello, internet";
                //println is used instead of System.out.println to print to the server GUI
                println("   => " + PING_MSG);
                return PING_MSG;
            }
        }
        return "Error: Unknown command " + cmd + ".";
    }

    public String createUser(Request req) {
        String id = req.getParam(CISConstants.USER_ID_PARAM);
        String name = req.getParam(CISConstants.USER_NAME_PARAM);
        String year = req.getParam(CISConstants.YEAR_LEVEL_PARAM);
        CISUser cisUser = new CISUser(id, name, year);

        if (id == null || name == null || year == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        for (int i = 0; i < users.size(); i++) {
            if (id.equals(users.get(i).getName()) || name.equals(users.get(i).getUserID())) {
                return CISConstants.DUP_USER_ERR;
            }
        }

        users.add(cisUser);
        return CISConstants.SUCCESS;
    }

    public String addMenuItem(Request req) {
        String name = req.getParam(CISConstants.ITEM_NAME_PARAM);
        String description = req.getParam(CISConstants.DESC_PARAM);
        double price = Double.parseDouble(req.getParam(CISConstants.PRICE_PARAM));
        String id = req.getParam(CISConstants.ITEM_ID_PARAM);
        String type = req.getParam(CISConstants.ITEM_TYPE_PARAM);

        if (name == null || description == null || id == null || type == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        MenuItem menuItem = new MenuItem(name, description, price, id, type);
        for (int i = 0; i < menu.getEatriumItems().size(); i++) {
            if (name.equals(menu.getEatriumItems().get(i).getName()) || id.equals(menu.getEatriumItems().get(i).getId())) {
                return CISConstants.DUP_ITEM_ERR;
            }
        }

        menu.getEatriumItems().add(menuItem);
        return CISConstants.SUCCESS;
    }

    public String placeOrder(Request req) {
        String orderID = req.getParam(CISConstants.ORDER_ID_PARAM);
        String menuItemID = req.getParam(CISConstants.ITEM_ID_PARAM);
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        String orderType = req.getParam(CISConstants.ORDER_TYPE_PARAM);
        Order order = new Order(menuItemID, orderType, orderID);

        //order exists?
        if (orderID == null) {
            return CISConstants.ORDER_INVALID_ERR;
        }

        // user exists?
        boolean userExists = false;
        int userIndex = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                userExists = true;
                userIndex = i;
            }
        }
        if (!userExists) {
            return CISConstants.USER_INVALID_ERR;
        } else {
            // duplicate order? (pre-existing order)
            for (int i = 0; i < users.get(userIndex).getOrders().size(); i++) {
                if (users.get(userIndex).getOrders().get(i).getOrderID().equals(orderID)) {
                    return CISConstants.DUP_ORDER_ERR;
                }
            }
        }
        //price of item given menuItemID;
        int eatriumIndex = -1;
        double price = 0.0;
        for (int i = 0; i < eatriumItems.size(); i++) {
            if (eatriumItems.get(i).getId().equals(menuItemID)) {
                eatriumIndex = i;
                price = eatriumItems.get(i).getPrice();
            }
        }
        // menuItem exists?
        if (eatriumIndex == -1) {
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
        //sufficient money?
        if (users.get(userIndex).getMoney() < price) {
            return CISConstants.USER_BROKE_ERR;
        }
        //sold out?
        if (eatriumItems.get(eatriumIndex).getAmountAvailable() < 1) {
            return CISConstants.SOLD_OUT_ERR;
        } else {
            //decrease amount
            int amount = eatriumItems.get(eatriumIndex).getAmountAvailable();
            amount--;
            eatriumItems.get(eatriumIndex).setAmountAvailable(amount);
        }

        //add user
        users.get(userIndex).addOrders(order);
        double wallet = users.get(userIndex).getMoney();
        users.get(userIndex).setMoney(wallet - price);
        return CISConstants.SUCCESS;
    }

    public String deleteOrder(Request req) {
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        String orderID = req.getParam(CISConstants.ORDER_ID_PARAM);
        String itemID = req.getParam(CISConstants.ITEM_ID_PARAM);
        int userIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                userIndex = i;
            }
        }
        if (userIndex == -1) {
            return CISConstants.USER_INVALID_ERR;
        } else {
            double itemPrice = 0;
            for (int i = 0; i < eatriumItems.size(); i++) {
                if (itemID.equals(eatriumItems.get(i).getId())) {
                    itemPrice = eatriumItems.get(i).getPrice();
                }
            }
            for (int i = 0; i < users.get(userIndex).getOrders().size(); i++) {
                if (users.get(userIndex).getOrders().get(i).getOrderID().equals(orderID)) {
                    users.get(userIndex).getOrders().remove(i);
                    // make refund
                    double currentWallet = users.get(userIndex).getMoney();
                    users.get(userIndex).setMoney(currentWallet + itemPrice);
                    return CISConstants.SUCCESS;
                }
            }
        }
        return CISConstants.PARAM_MISSING_ERR;
    }


    public String getOrder(Request req) {
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        String orderID = req.getParam(CISConstants.ORDER_ID_PARAM);
        int userIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                userIndex = i;
            }
        }
        int orderIndex = -1;
        if (userIndex != -1) {
            for (int i = 0; i < users.get(userIndex).getOrders().size(); i++) {
                if (users.get(userIndex).getOrders().get(i).getOrderID().equals(orderID)) {
                orderIndex = i;
                }
            }
        }
        return users.get(userIndex).getOrders().get(orderIndex).toString();
    }


    public String getUser(Request req) {
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                return users.get(i).toString();
            }
        }
        return CISConstants.USER_INVALID_ERR;
    }


    public String getItem(Request req) {
        String itemID = req.getParam(CISConstants.ITEM_ID_PARAM);
        for (int i = 0; i < eatriumItems.size(); i++) {
            if (itemID.equals(eatriumItems.get(i).getId())) {
                return eatriumItems.get(i).toString();
            }
        }
        return CISConstants.EMPTY_MENU_ERR;
    }

    public String getCart(Request req) {
        String allOrders = "";
        boolean userExists = false;
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                userExists = true;
                for (int j = 0; j < users.get(i).getOrders().size(); j++) {
                    allOrders = allOrders + "|" + users.get(i).getOrders().get(j).toString();
                }
            }
        }
        if (userExists) {
            return allOrders;
        } else {
            return null;
        }
    }

    public String getMenu(Request req) {
        String allMenuItems = "";
        for (int i = 0; i < eatriumItems.size(); i++) {
            allMenuItems = allMenuItems + "|" + eatriumItems.get(i);
        }
        return allMenuItems;
    }

    public String topUp(Request req) {
        String userID = req.getParam(CISConstants.USER_ID_PARAM);
        String topUpAmount = req.getParam(CISConstants.TOP_UP_AMOUNT);
        double amount = Double.parseDouble(topUpAmount);
        if (amount > 500) {
            return CISConstants.TOP_UP_ERR;
        }
        int userIndex = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(userID)) {
                userIndex = i;
            }
        }
        if (userIndex == -1) {
            return CISConstants.USER_INVALID_ERR;
        } else {
            double current = users.get(userIndex).getMoney();
            users.get(userIndex).setMoney(current + amount);
            println("New user amount: " + users.get(userIndex).getMoney());
            return CISConstants.SUCCESS;
        }
    }

    public static void main(String[] args)
    {
        CIServer f = new CIServer();
        f.start(args);
    }
}
