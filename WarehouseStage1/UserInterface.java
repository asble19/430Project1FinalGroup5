import java.io.*;
import java.util.*;

public class UserInterface {
  private static UserInterface userInterface;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private static final int EXIT = 0;
  private static final int ADD_CLIENT = 1;
  private static final int ADD_PRODUCTS = 2;
  private static final int MANAGE_WISHLIST = 3;
  private static final int LIST_CLIENTS = 4;
  private static final int LIST_PRODUCTS = 5;
  private static final int LIST_WISHLIST = 6;
  private static final int SAVE = 7;
  private static final int RETRIEVE = 8;
  private static final int HELP = 9;
  private static final int PROCESS_ORDER = 10;
  private static final int DISPLAY_WAITLIST = 11;
  private static final int RECEIVE_SHIPMENT = 12;
  private static final int RECEIVE_PAYMENT = 13;
  private static final int DISPLAY_INVOICES = 14;

  private UserInterface() {
    if (yesOrNo("Look for saved data and use it?")) {
      retrieve();
    } else {
      warehouse = Warehouse.instance();
    }
  }

  public static UserInterface instance() {
    if (userInterface == null) {
      return userInterface = new UserInterface();
    } else {
      return userInterface;
    }
  }

  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(line, "\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          return tokenizer.nextToken();
        }
      } catch (IOException ioe) {
        System.exit(0);
      }
    } while (true);
  }

  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
      return false;
    }
    return true;
  }

  public int getNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Integer num = Integer.valueOf(item);
        return num.intValue();
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }

  public double getDoubleNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Double num = Double.valueOf(item);
        return num.doubleValue();
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }

  public int getCommand() {
    do {
      try {
        int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
        if (value >= EXIT && value <= DISPLAY_INVOICES) {
          return value;
        }
      } catch (NumberFormatException nfe) {
        System.out.println("Enter a number");
      }
    } while (true);
  }

  public void help() {
    System.out.println("Enter a number between 0 and " + DISPLAY_INVOICES + " as explained below:");
    System.out.println(EXIT + " to Exit\n");
    System.out.println(ADD_CLIENT + " to add a client");
    System.out.println(ADD_PRODUCTS + " to add products");
    System.out.println(MANAGE_WISHLIST + " to add products to a client's wishlist");
    System.out.println(LIST_CLIENTS + " to show all clients");
    System.out.println(LIST_PRODUCTS + " to show all products");
    System.out.println(LIST_WISHLIST + " to show a client's wishlist");
    System.out.println(SAVE + " to save data");
    System.out.println(RETRIEVE + " to retrieve data");
    System.out.println(HELP + " for help");
    System.out.println(PROCESS_ORDER + " to process a client's order");
    System.out.println(DISPLAY_WAITLIST + " to display a product's waitlist");
    System.out.println(RECEIVE_SHIPMENT + " to receive a shipment");
    System.out.println(RECEIVE_PAYMENT + " to record a client payment");
    System.out.println(DISPLAY_INVOICES + " to display a client's invoices");
  }

  public void addClient() {
    String name = getToken("Enter client name");
    String address = getToken("Enter address");
    Client result = warehouse.addClient(name, address);
    if (result == null) {
      System.out.println("Could not add client");
    } else {
      System.out.println(result);
    }
  }

  public void addProducts() {
    Product result;
    do {
      String name = getToken("Enter product name");
      int quantity = getNumber("Enter quantity");
      double price = getDoubleNumber("Enter unit price");
      result = warehouse.addProduct(name, quantity, price);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("Product could not be added");
      }
      if (!yesOrNo("Add more products?")) {
        break;
      }
    } while (true);
  }

  public void manageWishlist() {
    String clientID = getToken("Enter client ID");
    do {
      String productID = getToken("Enter product ID");
      int quantity = getNumber("Enter quantity");
      warehouse.addProductToWishlist(clientID, productID, quantity);
      if (!yesOrNo("Add more products to this wishlist?")) {
        break;
      }
    } while (true);
  }

  public void listClients() {
    warehouse.displayAllClients();
  }

  public void listProducts() {
    warehouse.displayAllProducts();
  }

  public void listWishlist() {
    String clientID = getToken("Enter client ID");
    warehouse.displayWishlist(clientID);
  }

  private void save() {
    if (warehouse.save()) {
      System.out.println(" The warehouse has been successfully saved in the file WarehouseData \n");
    } else {
      System.out.println(" There has been an error in saving \n");
    }
  }

  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The warehouse has been successfully retrieved from the file WarehouseData \n");
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesn't exist; creating new warehouse");
        warehouse = Warehouse.instance();
      }
    } catch (Exception cnfe) {
      cnfe.printStackTrace();
    }
  }

  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case ADD_CLIENT:        addClient();
                                 break;
        case ADD_PRODUCTS:      addProducts();
                                 break;
        case MANAGE_WISHLIST:   manageWishlist();
                                 break;
        case LIST_CLIENTS:      listClients();
                                 break;
        case LIST_PRODUCTS:     listProducts();
                                 break;
        case LIST_WISHLIST:     listWishlist();
                                 break;
        case SAVE:               save();
                                 break;
        case RETRIEVE:           retrieve();
                                 break;
        case HELP:               help();
                                 break;
        case PROCESS_ORDER:
          String clientID = getToken("Enter client ID");
          warehouse.processOrder(clientID);
          break;

        
        case DISPLAY_WAITLIST: {
          String waitlistProductID = getToken("Enter product ID");
          warehouse.displayWaitlist(waitlistProductID);
          break;
        }

        case RECEIVE_SHIPMENT: {
          String shipmentProductID = getToken("Enter product ID");
          int shipmentQuantity = getNumber("Enter shipment quantity");
          warehouse.receiveShipment(shipmentProductID, shipmentQuantity);
          break;
        }

        case RECEIVE_PAYMENT: {
          String paymentClientID = getToken("Enter client ID");
          double paymentAmount = getDoubleNumber("Enter payment amount");
          warehouse.receivePayment(paymentClientID, paymentAmount);
          break;
        }

        case DISPLAY_INVOICES: {
          String invoiceClientID = getToken("Enter client ID");
          warehouse.displayInvoices(invoiceClientID);
          break;
        }

      }
    }
  }

  public static void main(String[] s) {
    UserInterface.instance().process();
  }
}