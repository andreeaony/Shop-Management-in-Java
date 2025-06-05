import db.DatabaseConnection;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Order {
    private Connection conn;
    private Statement stmt;
    private static int numberOfOrders;
    protected int orderID;
    protected int customerID;
    protected Customer customer;
    protected Date dateOfOrder;
    protected Shipping shipping;
    protected HashMap<Provider, ArrayList<Product>> orderDistributorsAndProducts;

    private boolean valid = true;
    public boolean isValid() {
        return valid;
    }

    static
    {
        numberOfOrders=0;
    }
    {
        this.orderID=++numberOfOrders;
    }

    public Order() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        conn = DatabaseConnection.getConnection();  // deschizi conexiunea
        stmt = conn.createStatement();

        System.out.print("Enter customer's ID: ");
        this.customerID = scanner.nextInt();
        String queryCustomer = "SELECT * FROM customer WHERE customerId = " + this.customerID;
        ResultSet rsCustomer = stmt.executeQuery(queryCustomer);
        if (!rsCustomer.next()) {
            System.out.println("\033[31mCustomer ID not found in the database!\033[0m");
            rsCustomer.close();
            return;
        }
        rsCustomer.close();

        System.out.print("Enter the year from the date of order: ");
        int year = scanner.nextInt();
        System.out.print("Enter the month from the date of order: ");
        int month = scanner.nextInt();
        System.out.print("Enter the day from the date of order: ");
        int day = scanner.nextInt();
        this.dateOfOrder = new Date(year - 1900, month - 1, day);

        this.shipping = new Shipping();

        this.orderDistributorsAndProducts = new HashMap<>();
        String queryProviders = "SELECT * FROM provider";
        ResultSet rsProviders = stmt.executeQuery(queryProviders);
        Map<Integer, Provider> providerIndexToObject = new HashMap<>();
        int index = 1;
        Service.clearConsole();
        while (rsProviders.next()) {
            int providerId = rsProviders.getInt("providerId");
            String providerName = rsProviders.getString("providerName");

            Provider p = new Provider(providerName, providerId);

            providerIndexToObject.put(index, p);
            System.out.println(index + ". " + providerName);
            index++;
        }
        rsProviders.close();

        System.out.print("\n\033[35mHow many providers do you want to add to the order?\nYour choice: \033[0m");
        int providerCount = scanner.nextInt();
        for (int i = 0; i < providerCount; i++) {
            System.out.print("\n\033[36mEnter the index of provider number " + (i + 1) + ": \033[0m");
            int providerChoice = scanner.nextInt();
            Provider provider = providerIndexToObject.get(providerChoice);
            int providerId = provider.getProviderId();

            String productQuery = "SELECT * FROM product WHERE providerId = " + providerId;
            Statement stmtProducts = conn.createStatement();
            ResultSet rsProducts = stmtProducts.executeQuery(productQuery);
            Map<Integer, Product> productIndexToProduct = new HashMap<>();
            int prodIndex = 1;
            System.out.println("\033[35mAvailable products from " + provider.getProviderName() + ":\033[0m");
            while (rsProducts.next()) {
                int productId = rsProducts.getInt("productId");
                String productName = rsProducts.getString("productName");
                double price = rsProducts.getDouble("productPrice");

                Product product = new Product(productId, productName, price);

                productIndexToProduct.put(prodIndex, product);
                System.out.println(prodIndex + ". " + productName + " - " + price);
                prodIndex++;
            }
            rsProducts.close();
            stmtProducts.close();

            if (productIndexToProduct.isEmpty()) {
                System.out.println("\033[33mNo products available from this provider.\033[0m");
                continue;
            }

            System.out.print("\033[35mHow many products to add from this provider?\nYour choice: \033[0m");
            int productCount = scanner.nextInt();
            ArrayList<Product> selectedProducts = new ArrayList<>();
            for (int j = 0; j < productCount; j++) {
                System.out.print("\033[36mEnter index of product number " + (j + 1) + ": \033[0m");
                int prodChoice = scanner.nextInt();
                selectedProducts.add(productIndexToProduct.get(prodChoice));
            }
            this.orderDistributorsAndProducts.put(provider, selectedProducts);
            }
            orderDistributorsAndProducts.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
            if (orderDistributorsAndProducts.isEmpty()) {
                System.out.println("\033[31mNo products were selected for the order. Cancelling order.\033[0m");
                valid=false;
                return;
            }
    }

    public Connection getConnection() {
        return conn;
    }

    public Order(int customerID, Customer customer, Date dateOfOrder, Shipping shipping, HashMap<Provider, ArrayList<Product>> orderDistributorsAndProducts){
        this.customerID=customerID;
        this.customer=customer;
        this.dateOfOrder=dateOfOrder;
        this.shipping=shipping;
        this.orderDistributorsAndProducts=orderDistributorsAndProducts;

        //bestseller&stock upgrade
        for (Map.Entry<Provider, ArrayList<Product>> entry : orderDistributorsAndProducts.entrySet()) {
            ArrayList<Product> products = entry.getValue();
            for (Product product : products) {
                if (product instanceof PerishableProduct) {
                    for (PerishableProduct p : Inventory.perishableProducts) {
                        if (p.getProductName().equals(product.getProductName())) {
                            if (p.getProductStock() > 0) {
                                p.setProductStock(p.getProductStock() - 1);
                                Inventory.bestseller.put(p, Inventory.bestseller.getOrDefault(p, 0) + 1);
                            } else {
                                System.out.println("\033[31mProduct '" + p.getProductName() + "' not in stock.\033[0m");
                            }
                            break;
                        }
                    }
                } else if (product instanceof NonperishableProduct) {
                    for (NonperishableProduct p : Inventory.nonPerishableProducts) {
                        if (p.getProductName().equals(product.getProductName())) {
                            if (p.getProductStock() > 0) {
                                p.setProductStock(p.getProductStock() - 1);
                                Inventory.bestseller.put(p, Inventory.bestseller.getOrDefault(p, 0) + 1);
                            } else {
                                System.out.println("\033[31mProduct '" + p.getProductName() + "' not in stock.\033[0m");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    public int getOrderID() {
        return orderID;
    }
    public int getCustomerID() {
        return customerID;
    }
    public Date getDateOfOrder() {
        return dateOfOrder;
    }
    public Shipping getShipping() {
        return shipping;
    }
    public HashMap<Provider, ArrayList<Product>> getOrderDistributorsAndProducts() {
        return orderDistributorsAndProducts;
    }

    public void setDateOfOrder(int year, int month, int day) {
        Date newDate = new Date(year-1900, month-1, day);
        this.dateOfOrder = newDate;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    public void setCustomer(Customer customer){
        this.customer=customer;
    }
    public void setShipping (Shipping shipping) {
        this.shipping = shipping;
    }
    public void setOrderDistributorsAndProducts(HashMap<Provider, ArrayList<Product>> orderDistributorsAndProducts) {
        this.orderDistributorsAndProducts = orderDistributorsAndProducts;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void close() {
        try {
            if (stmt != null && !stmt.isClosed()) stmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    //delete
//    public void cancelOrder() {
//        this.statusOfOrder = "cancelled";
//        this.orderDistributorsAndProducts.clear();
//        System.out.println("Order ID " + orderID + " has been cancelled.");
//    }
//    //read
//    public void showOrderDetails() {
//        System.out.println("~~~~~ORDER DETAILS FOR ORDER "+this.orderID+"~~~~~");
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println("Customer ID: "+this.customerID);
//        System.out.println("-Date of Order: " + this.dateOfOrder);
//        System.out.println("-Tracking Number: " + this.trackingNumber);
//        System.out.println("-Status of Order: " + this.statusOfOrder);
//        System.out.println("-Order content: " + this.orderDistributorsAndProducts);
//        System.out.println();
//    }
}
