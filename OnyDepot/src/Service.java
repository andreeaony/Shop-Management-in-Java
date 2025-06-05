import db.DatabaseConnection;
import db.DatabaseSeeder;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class Service {
    ///PORNIRE BAZA DE DATE
    public void databaseInit() {
        //conexiunea cu baza de date
        try {
            if(DatabaseConnection.getConnection() != null)
                System.out.println("Successfully connected!");
            else
                System.out.println("Connection error!");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        //seeder pentru baza de date
        DatabaseSeeder.seedDatabase();
        System.out.println("Database seeded successfully!");
        clearConsole();
        //seeder pentru inventar
        Inventory inventory = new Inventory();
        String url = "jdbc:mysql://localhost:3306/onydepot";
        String user = "root";
        String password = "parola";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            inventory.loadFromDatabase(conn);
        }
        catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /// METHODS
    /// CUSTOMERS
    public void addCustomer() throws SQLException {
        Customer customerx = new Customer();
        String sql = "INSERT INTO customer (customerName, customerEmail, customerAddress, customerPhone) VALUES ("
                + "'" + customerx.getCustomerName() + "', "
                + "'" + customerx.getCustomerEmail() + "', "
                + "'" + customerx.getCustomerAddress() + "', "
                + "'" + customerx.getCustomerPhone() + "')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            if(rows>0) {
                System.out.println("\n\u001B[36mCustomer added successfully!\u001B[0m");
                AuditService.getInstance().log("Create Customer");
            }
            else
                System.out.println("\u001B[31mFailed to add customer.\u001B[0m");
        }
    }
    private Map<Integer, Integer> indexToCustomerId = new HashMap<>(); //index -> customerId
    public void listCustomers() throws SQLException {
        String sql = "SELECT customerId, customerName FROM customer";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int index=1;
            indexToCustomerId.clear(); //resetez maparea
            while (rs.next()) {
                int id = rs.getInt("customerId");
                String name = rs.getString("customerName");
                System.out.println(index + ". " + name);
                indexToCustomerId.put(index, id);
                index++;
            }
            AuditService.getInstance().log("Read Customers");
        }
    }
    public void showCustomer(int customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customerId = " + customerId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("\u001B[45mCUSTOMER PROFILE for " + rs.getString("customerName") + "\u001B[0m");
                System.out.println("-Customer ID: " + rs.getInt("customerId"));
                System.out.println("-Customer name: " + rs.getString("customerName"));
                System.out.println("-Customer Address: " + rs.getString("customerAddress"));
                System.out.println("-Customer Phone Number: " + rs.getString("customerPhone"));
                System.out.println("-Customer Email: " + rs.getString("customerEmail"));
            } else {
                System.out.println("\u001B[31mCustomer not found.\u001B[0m");
            }
            AuditService.getInstance().log("Read Customer");
        }
    }
    public void showCustomer() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        listCustomers();
        System.out.print("Enter the number of the customer you want to see: ");
        int choice = scanner.nextInt();

        Integer customerId = indexToCustomerId.get(choice);
        if (customerId != null)
            showCustomer(customerId);
        else
            System.out.println("\u001B[31mInvalid choice.\u001B[0m");
    }
    public void showAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customer";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("\u001B[45mCUSTOMER PROFILE for " + rs.getString("customerName") + "\u001B[0m");
                System.out.println("-Customer ID: " + rs.getInt("customerId"));
                System.out.println("-Customer name: " + rs.getString("customerName"));
                System.out.println("-Customer Address: " + rs.getString("customerAddress"));
                System.out.println("-Customer Phone Number: " + rs.getString("customerPhone"));
                System.out.println("-Customer Email: " + rs.getString("customerEmail"));
                System.out.println();
            }
            AuditService.getInstance().log("Read Customers");
        }
    }
    public void editCustomer() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number for what you want to edit: ");
        System.out.println("1.Customer's name\n2.Customer's address\n3.Customer's phone number\n4.Customer's email");
        int whatToEdit = scanner.nextInt();
        scanner.nextLine();
        clearConsole();

        System.out.println("Choose the customer number to edit:");
        listCustomers();
        int selectedIndex = scanner.nextInt();
        scanner.nextLine();
        Integer customerId = indexToCustomerId.get(selectedIndex);
        if(customerId == null) {
            System.out.println("\u001B[31mInvalid customer number!\u001B[0m");
            return;
        }
        clearConsole();

        System.out.print("Enter the new value: ");
        String newValue = scanner.nextLine();
        while (newValue.trim().isEmpty()) {
            System.out.print("Value cannot be empty. Please enter it again: ");
            newValue = scanner.nextLine();
        }

        String field = null;
        switch (whatToEdit) {
            case 1:
                field = "customerName";
                break;
            case 2:
                field = "customerAddress";
                break;
            case 3:
                field = "customerPhone";
                break;
            case 4:
                field = "customerEmail";
                break;
            default:
                System.out.println("\u001B[31mInvalid option for editing customer!\u001B[0m");
                return;
        }

        String sql = "UPDATE customer SET " + field + " = '" + newValue.replace("'", "''") + "' WHERE customerId = " + customerId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            if (rows > 0) {
                System.out.println("\u001B[36mCustomer updated successfully!\u001B[0m");
                AuditService.getInstance().log("Update Customer");
            }
            else
                System.out.println("\u001B[31mUpdate failed, no such customer.\u001B[0m");
        }
    }
    public void deleteCustomer() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        listCustomers();
        System.out.print("Enter the number of the customer you want to delete: ");
        int choice = scanner.nextInt();
        Integer customerId = indexToCustomerId.get(choice);
        String sqlDelete = "DELETE FROM customer WHERE customerId = " + customerId;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(sqlDelete);
            if (rowsAffected > 0) {
                System.out.println("\u001B[36mCustomer removed successfully.\u001B[0m");
                AuditService.getInstance().log("Delete Customer");
            } else {
                System.out.println("\u001B[31mNo customer was deleted. Something went wrong.\u001B[0m");
            }
        }
    }
    public void showOrdersForCustomer() {
        try (
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/onydepot", "root", "parola");
                Statement stmt = conn.createStatement();
        ) {
            Scanner scanner = new Scanner(System.in);

            ResultSet rsCustomers = stmt.executeQuery("SELECT customerId, customerName FROM customer");
            List<Integer> customerIds = new ArrayList<>();
            List<String> customerNames = new ArrayList<>();

            System.out.println("Enter the number for the customer you want to show orders for from the list below:");
            int index = 1;
            while (rsCustomers.next()) {
                int id = rsCustomers.getInt("customerId");
                String name = rsCustomers.getString("customerName");
                customerIds.add(id);
                customerNames.add(name);
                System.out.println(index + ". " + name);
                index++;
            }
            if (customerIds.isEmpty()) {
                System.out.println("No customers found.");
                return;
            }

            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice < 1 || choice > customerIds.size()) {
                System.out.println("Invalid choice.");
                return;
            }

            int selectedCustomerId = customerIds.get(choice - 1);
            String selectedCustomerName = customerNames.get(choice - 1);
            clearConsole();

            String sql = "SELECT o.orderId, o.dateOfOrder, s.shippingCity, s.statusOfOrder " +
                    "FROM `order` o JOIN shipping s ON o.shippingId = s.shippingId " +
                    "WHERE o.customerId = " + selectedCustomerId;
            ResultSet rsOrders = stmt.executeQuery(sql);
            boolean hasOrders = false;
            int orderNumber = 1;

            while (rsOrders.next()) {
                hasOrders = true;
                int orderId = rsOrders.getInt("orderId");
                Date dateOfOrder = rsOrders.getDate("dateOfOrder");
                String shippingCity = rsOrders.getString("shippingCity");
                String status = rsOrders.getString("statusOfOrder");

                System.out.println("\033[1mOrder number " + orderNumber + " for customer " + selectedCustomerName + ":\033[0m");
                System.out.println("Order ID: " + orderId);
                System.out.println("Date of Order: " + dateOfOrder);
                System.out.println("Shipping City: " + shippingCity);
                System.out.println("Status: " + status);

                String productQuery = "SELECT p.productName, p.productPrice, od.quantity " +
                        "FROM orderdetails od " +
                        "JOIN product p ON od.productId = p.productId " +
                        "WHERE od.orderId = " + orderId;
                try (Statement productStmt = conn.createStatement();
                     ResultSet rsProducts = productStmt.executeQuery(productQuery)) {

                    System.out.println("Products in this order:");
                    int productCount = 1;
                    while (rsProducts.next()) {
                        String productName = rsProducts.getString("productName");
                        double price = rsProducts.getDouble("productPrice");
                        int quantity = rsProducts.getInt("quantity");

                        System.out.println("  " + productCount + ". " + productName + " - Price: " + price + " RON, Quantity: " + quantity);
                        productCount++;
                    }
                }
                System.out.println();
                orderNumber++;
            }


            if (!hasOrders) {
                System.out.println("\033[36mThe customer does not have any orders yet.\033[0m");
            }

            AuditService.getInstance().log("Read Customer");

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }


    /// PRODUCTS
    public void addProduct() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Who is the provider for this product?");
        listProviders();
        System.out.print("Your choice: ");
        Integer prov=scanner.nextInt();
        clearConsole();
        String provider="SELECT * FROM provider WHERE providerId = " + prov;
        try(Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(provider);
            if(rs.next()) {
                addProductForProvider(rs.getInt("providerId"));
            }
        }
    }
    private Map<Integer, Integer> indexToProductId = new HashMap<>();
    public void listProducts() throws SQLException {
        String sql = "SELECT productId, productName FROM product";
        indexToCustomerId.clear(); //resetez maparea
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int index=1;
            while (rs.next()) {
                int id = rs.getInt("productId");
                String name = rs.getString("productName");
                System.out.println(index + ". " + name);
                indexToProductId.put(index, id);
                index++;
            }
            AuditService.getInstance().log("Read Products");
        }
    }
    public void showAProduct() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        listProducts();
        System.out.println("Enter the number for the product you want to see: ");
        System.out.print("Your choice: ");
        Integer choice = scanner.nextInt();

        Integer productId = indexToProductId.get(choice);
        if (productId == null) {
            System.out.println("\u001B[31mInvalid product number.\u001B[0m");
            return;
        }

        clearConsole();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM product WHERE productId = " + productId);
            if (rs.next()) {
                System.out.println("-ID of product: " + rs.getInt("productId"));
                System.out.println("-Type of product: " + rs.getString("productType"));
                System.out.println("-Name of product: " + rs.getString("productName"));
                System.out.println("-Category of product: " + rs.getString("productCategory"));
                System.out.println("-Description of product: " + rs.getString("productDescription"));
                System.out.println("-Price of product: " + rs.getDouble("productPrice"));
                System.out.println("-Stock of product: " + rs.getInt("productStock"));
                System.out.println("-Rating of product: " + rs.getInt("productRating"));
                System.out.println("-Expiration date of product: " + rs.getDate("expirationDate"));
                System.out.println("-Storage instructions for the product: " + rs.getString("storageInstructions"));
                System.out.println();
                AuditService.getInstance().log("Read Product");
            } else {
                System.out.println("\u001B[31mProduct not found.\u001B[0m");
            }
        }
    }
    public void removeProduct() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        listProducts();
        System.out.print("Enter the number of the product you want to delete: ");
        int choice = scanner.nextInt();
        Integer productId = indexToProductId.get(choice);
        String sqlDelete = "DELETE FROM product WHERE productId = " + productId;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int rowsAffected = stmt.executeUpdate(sqlDelete);
            if (rowsAffected > 0) {
                System.out.println("\u001B[36mProduct removed successfully. These are the products now:\u001B[0m");
                listProducts();
                AuditService.getInstance().log("Delete Product");
            } else {
                System.out.println("\u001B[31mNo product was deleted. Something went wrong.\u001B[0m");
            }
        }
    }
    public void showInventoryForPerishableProducts() {
        Inventory.perishableProducts.sort(Comparator.comparing(PerishableProduct::getProductName));
        System.out.println("\u001B[46m~~~~~PERISHABLE PRODUCTS INVENTORY~~~~~\u001B[0m");
        int productNumber = 1;
        for(PerishableProduct p : Inventory.perishableProducts) {
            System.out.println("\u001B[45mINVENTORY FOR PRODUCT NUMBER " + productNumber + "\u001B[0m");
            System.out.println("-ID of product: " + p.getProductId());
            System.out.println("-Name of product: " + p.getProductName());
            System.out.println("-Category of product: " + p.getProductCategory());
            System.out.println("-Description of product: " + p.getProductDescription());
            System.out.println("-Price of product: " + p.getProductPrice());
            System.out.println("-Stock of product: " + p.getProductStock());
            System.out.println("-Rating of product: " + p.getProductRating());
            System.out.println("-Expiration date of product: " + p.getExpirationDate());
            productNumber++;
            System.out.println();
            AuditService.getInstance().log("Read Product");
        }
    } //inventar
    public void showInventoryForNonperishableProducts() {
        Inventory.nonPerishableProducts.sort(Comparator.comparing(NonperishableProduct::getProductName));
        System.out.println("\u001B[46m~~~~~NONPERISHABLE PRODUCTS INVENTORY~~~~~\u001B[0m");
        int productNumber = 1;
        for(NonperishableProduct p : Inventory.nonPerishableProducts) {
            System.out.println("\u001B[45mINVENTORY FOR PRODUCT NUMBER " + productNumber + "\u001B[0m");
            System.out.println("-ID of product: " + p.getProductId());
            System.out.println("-Name of product: " + p.getProductName());
            System.out.println("-Category of product: " + p.getProductCategory());
            System.out.println("-Description of product: " + p.getProductDescription());
            System.out.println("-Price of product: " + p.getProductPrice());
            System.out.println("-Stock of product: " + p.getProductStock());
            System.out.println("-Rating of product: " + p.getProductRating());
            System.out.println("-Storage instructions for product: " + p.getStorageInstructions());
            productNumber++;
            System.out.println();
            AuditService.getInstance().log("Read Product");
        }
    } //inventar
    public void showExpiringProducts() {
        Inventory.soonToExpireProducts.clear();

        LocalDate currentDate = LocalDate.now();
        LocalDate next7Date = currentDate.plusDays(7);

        for (PerishableProduct product : Inventory.perishableProducts) {
            LocalDate expirationDate = product.getExpirationDate();
            if (!expirationDate.isAfter(next7Date)) {
                Inventory.soonToExpireProducts.add(product);
            }
        }

        System.out.println("\u001B[45m~~~~~ SOON TO EXPIRE PRODUCTS ~~~~~\u001B[0m");

        if (Inventory.soonToExpireProducts.isEmpty()) {
            System.out.println("\n" + "\u001B[36mNo products are expiring within the next 7 days.\u001B[0m");
        } else {
            for (PerishableProduct product : Inventory.soonToExpireProducts) {
                LocalDate expirationDate = product.getExpirationDate();
                System.out.println("- \033[1m" + product.getProductName() + "\033[0m expires on: " + expirationDate);
            }
        }
        AuditService.getInstance().log("Read Product");
        System.out.println();
    } //inventar
    public void showBestsellerProducts() {
        List<Map.Entry<Product, Integer>> top = new ArrayList<>(Inventory.bestseller.entrySet());
        top.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        System.out.println("\033[45m\033[1m        ~~~~~ TOP PRODUCTS ~~~~~        \033[0m" + "\n");
        for (Map.Entry<Product, Integer> entry : top)
            if(entry.getValue()==1)
                System.out.println("- "+entry.getKey().getProductName() + " ----- SOLD: " + entry.getValue() + " PRODUCT");
            else
                System.out.println("- "+entry.getKey().getProductName() + " ----- SOLD: " + entry.getValue() + " PRODUCTS");
        AuditService.getInstance().log("Read Product");
    } //inventar
    public void showLowStockProducts() {
        System.out.println("\u001B[36mLOW STOCK PRODUCTS\u001B[0m");

        String sql = "SELECT productName, productStock FROM product WHERE productStock <= 25";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int productNumber = 1;
            boolean hasLowStock = false;

            while (rs.next()) {
                String name = rs.getString("productName");
                int stock = rs.getInt("productStock");

                System.out.println(productNumber + ". " + name + " | stock = " + stock);
                productNumber++;
                hasLowStock = true;
            }
            if (!hasLowStock) {
                System.out.println("All products have sufficient stock.");
            }
            AuditService.getInstance().log("Read Product");
        }
        catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    public void editProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("For editing a perishable product, press 1. For editing a nonperishable product, press 2.\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        clearConsole();
        boolean isPerishable;
        if(choice==1)
            isPerishable=true;
        else isPerishable=false;
        String perishable=(isPerishable ? "perishable" : "nonperishable");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlSelect = "SELECT productId, productName FROM product WHERE productType = '" + perishable + "'";
            ResultSet rs = stmt.executeQuery(sqlSelect);

            List<Integer> typeOfProductsIds = new ArrayList<>();
            int i = 1;
            System.out.println("\nAvailable products for editing:");
            while (rs.next()) {
                int id = rs.getInt("productId");
                String name = rs.getString("productName");
                System.out.println(i + ". " + name);
                typeOfProductsIds.add(id);
                i++;
            }
            if (typeOfProductsIds.isEmpty()) {
                System.out.println("No products found.");
                return;
            }

            System.out.print("Enter the number of the product you want to edit: ");
            int selected = scanner.nextInt();
            scanner.nextLine();
            clearConsole();
            int productId = typeOfProductsIds.get(selected-1);
            System.out.println("""
                What do you want to edit for the selected product?
                1. Name
                2. Category
                3. Description
                4. Price
                5. Stock
                6. Rating
                """ + (isPerishable ? "7. Expiration Date" : "7. Storage Instructions"));
            System.out.print("Your choice: ");
            int choice2 = scanner.nextInt();
            scanner.nextLine();
            clearConsole();
            String updateSql;
            switch (choice2) {
                case 1: {
                    System.out.print("Enter the new name: ");
                    String newName = scanner.nextLine();
                    updateSql = "UPDATE product SET productName = '" + newName + "' WHERE productId = " + productId;
                    break;
                }
                case 2: {
                    System.out.print("Enter the new category: ");
                    String newCategory = scanner.nextLine();
                    updateSql = "UPDATE product SET productCategory = '" + newCategory + "' WHERE productId = " + productId;
                    break;
                }
                case 3: {
                    System.out.print("Enter the new description: ");
                    String newDescription = scanner.nextLine();
                    updateSql = "UPDATE product SET productDescription = '" + newDescription + "' WHERE productId = " + productId;
                    break;
                }
                case 4: {
                    System.out.print("Enter the new price: ");
                    double newPrice = scanner.nextDouble();
                    updateSql = "UPDATE product SET productPrice = " + newPrice + " WHERE productId = " + productId;
                    break;
                }
                case 5: {
                    System.out.print("Enter the new stock: ");
                    int newStock = scanner.nextInt();
                    updateSql = "UPDATE product SET productStock = " + newStock + " WHERE productId = " + productId;
                    break;
                }
                case 6: {
                    System.out.print("Enter the new rating: ");
                    double newRating = scanner.nextDouble();
                    updateSql = "UPDATE product SET productRating = " + newRating + " WHERE productId = " + productId;
                    break;
                }
                case 7: {
                    if (isPerishable) {
                        System.out.print("Enter the expiration year: ");
                        int year = scanner.nextInt();
                        System.out.print("Enter the month: ");
                        int month = scanner.nextInt();
                        System.out.print("Enter the day: ");
                        int day = scanner.nextInt();
                        String dateStr = String.format("%d-%02d-%02d", year, month, day);
                        updateSql = "UPDATE product SET expirationDate = '" + dateStr + "' WHERE productId = " + productId;
                    }
                    else {
                        System.out.print("Enter the new storage instructions: ");
                        String storage = scanner.nextLine();
                        updateSql = "UPDATE product SET storageInstructions = '" + storage + "' WHERE productId = " + productId;
                    }
                    break;
                }
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            if (updateSql != null) {
                int rowsAffected = stmt.executeUpdate(updateSql);
                if (rowsAffected > 0) {
                    System.out.println("\u001B[36mProduct updated successfully!\u001B[0m");
                    AuditService.getInstance().log("Update Product");
                }
                else
                    System.out.println("\u001B[31mFailed to update product.\u001B[0m");
            }
        }
        catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
    public void applyDiscount() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the product you want to apply a discount on, from the list above: ");
        listProducts();
        System.out.print("Your choice: ");
        int selectedIndex = scanner.nextInt();
        Integer selectedProductId = indexToProductId.get(selectedIndex);
        if (selectedProductId == null) {
            System.out.println("\u001B[31mInvalid choice!\u001B[0m");
            return;
        }
        clearConsole();

        System.out.println("Enter the discount (%) you want to apply for the selected product: ");
        double discount = scanner.nextDouble();
        discount /= 100;

        String selectSql = "SELECT * FROM product WHERE productId = " + selectedProductId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {

            if (rs.next()) {
                double oldPrice = rs.getDouble("productPrice");
                double newPrice = oldPrice - (oldPrice * discount);

                String updateSql = "UPDATE product SET productPrice = " + newPrice + " WHERE productId = " + selectedProductId;
                int rowsAffected = stmt.executeUpdate(updateSql);

                clearConsole();
                if (rowsAffected > 0) {
                    discount=discount*100;
                    System.out.println("\u001B[36mDiscount of " +discount+ "% applied! \u001B[0m\n");
                    AuditService.getInstance().log("Update Product");
                }
                else
                    System.out.println("\u001B[31mFailed to update the product price.\u001B[0m");
            } else
                System.out.println("\u001B[31mProduct with ID " + selectedProductId + " not found.\u001B[0m");
        }
        catch (SQLException e) {
            System.err.println("Error applying discount: " + e.getMessage());
        }
    }
    
    /// ORDERS + SHIPPING
    public void registerOrder() {
        Order order = null;
        try {
            order = new Order();
            if (!order.isValid()) {
                System.out.println("\033[31mOrder was not created. Nothing to register.\033[0m");
            } else {
                Connection conn = order.getConnection();
                conn.setAutoCommit(false);

                Shipping shipping = order.getShipping();

                Statement stmt = conn.createStatement();

                String insertShipping = String.format("""
                        INSERT INTO shipping (shippingCity, shippingCountry, shippingPostalCode, shippingMethod, trackingNumber, statusOfOrder, estimatedDelivery)
                        VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')
                        """, shipping.getShippingCity(), shipping.getShippingCountry(), shipping.getShippingPostalCode(), shipping.getShippingMethod(), shipping.getTrackingNumber(), shipping.getStatusOfOrder(), new java.sql.Date(shipping.getEstimatedDelivery().getTime()).toString());

                stmt.executeUpdate(insertShipping, Statement.RETURN_GENERATED_KEYS);
                AuditService.getInstance().log("Create Shipping");
                ResultSet rs = stmt.getGeneratedKeys();
                rs.next();
                int shippingId = rs.getInt(1);
                rs.close();

                String insertOrder = String.format("""
                        INSERT INTO `order` (customerId, shippingId, dateOfOrder)
                        VALUES (%d, %d, '%s')
                        """, order.getCustomerID(), shippingId, new java.sql.Date(order.getDateOfOrder().getTime()));

                stmt.executeUpdate(insertOrder, Statement.RETURN_GENERATED_KEYS);
                AuditService.getInstance().log("Create Order");
                rs = stmt.getGeneratedKeys();
                rs.next();
                int orderId = rs.getInt(1);
                rs.close();

                for (Map.Entry<Provider, ArrayList<Product>> entry : order.getOrderDistributorsAndProducts().entrySet()) {
                    int providerId = entry.getKey().getProviderId();

                    Map<Integer, Integer> productIdToQuantity = new HashMap<>();
                    Map<Integer, Product> productIdToProduct = new HashMap<>();

                    for (Product product : entry.getValue()) {
                        int productId = product.getProductId();
                        productIdToQuantity.put(productId, productIdToQuantity.getOrDefault(productId, 0) + 1);
                        productIdToProduct.putIfAbsent(productId, product);
                    }

                    for (Map.Entry<Integer, Integer> productEntry : productIdToQuantity.entrySet()) {
                        int productId = productEntry.getKey();
                        int quantity = productEntry.getValue();

                        String insertDetail = String.format("""
                                INSERT INTO orderdetails (orderId, productId, providerId, quantity)
                                VALUES (%d, %d, %d, %d)
                                """, orderId, productId, providerId, quantity);
                        stmt.executeUpdate(insertDetail);
                        AuditService.getInstance().log("Create Order Details");

                        String updateStock = String.format("""
                                UPDATE product
                                SET productStock = productStock - %d
                                WHERE productId = %d AND productStock >= %d
                                """, quantity, productId, quantity);
                        int rowsAffected = stmt.executeUpdate(updateStock);

                        if (rowsAffected == 0) {
                            System.out.println("\033[31mProduct '" + productIdToProduct.get(productId).getProductName() + "' has insufficient stock.\033[0m");
                        } else {
                            Product product = productIdToProduct.get(productId);
                            Inventory.bestseller.put(product, Inventory.bestseller.getOrDefault(product, 0) + quantity);
                        }
                    }
                }
                conn.commit();
                System.out.println("\n\033[36mOrder successfully placed!\033[0m");

                stmt.close();
            }} catch(SQLException e){
                System.out.println("\033[31mFailed to register the order: " + e.getMessage() + "\033[0m");
            } finally{
                if (order != null) order.close();
            }
    }
    public void listOrders() {
        try (Connection conn=DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String query = """
            SELECT o.orderId, o.customerId, o.dateOfOrder,
                   s.shippingCountry, s.shippingCity, s.shippingPostalCode,
                   s.trackingNumber, s.statusOfOrder, s.shippingMethod, s.estimatedDelivery
            FROM `order` o
            JOIN shipping s ON o.shippingId = s.shippingId
            ORDER BY o.orderId
        """;

            ResultSet rs = stmt.executeQuery(query);
            AuditService.getInstance().log("Read Orders");
            AuditService.getInstance().log("Read Shipping");
            System.out.println("\033[45m~~~~~ LIST OF ALL ORDERS ~~~~~\033[0m\n");

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                int customerId = rs.getInt("customerId");
                String date = rs.getString("dateOfOrder");
                String country = rs.getString("shippingCountry");
                String city = rs.getString("shippingCity");
                String postalCode = rs.getString("shippingPostalCode");
                String tracking = rs.getString("trackingNumber");
                String status = rs.getString("statusOfOrder");
                String method = rs.getString("shippingMethod");
                String estimated = rs.getString("estimatedDelivery");

                System.out.println("Order ID: " + orderId);
                System.out.println("- Customer ID: " + customerId);
                System.out.println("- Date of Order: " + date);
                System.out.println("- Shipping Address: " + country + ", " + city + ", " + postalCode);
                System.out.println("- Tracking Number: " + tracking);
                System.out.println("- Status: " + status);
                System.out.println("- Shipping Method: " + method);
                System.out.println("- Estimated Delivery: " + estimated);
                System.out.println("--------------------------------------------------\n");
            }

        } catch (SQLException e) {
            System.out.println("\033[31mError listing orders: " + e.getMessage() + "\033[0m");
        }
    }
    public void cancelOrder() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the order you want to cancel:");
        listOrders();
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();

        try(Connection conn = DatabaseConnection.getConnection();
        Statement stmt= conn.createStatement()){
            ResultSet rs = stmt.executeQuery("SELECT * FROM `order` WHERE orderId = " + choice);

            if (rs.next()) {
                int orderId = rs.getInt("orderId");

                String updateStatus = String.format("UPDATE shipping JOIN `order` ON order.shippingId=shipping.shippingId SET statusOfOrder = 'Cancelled' WHERE order.orderId = %d", orderId);
                stmt.executeUpdate(updateStatus);
                AuditService.getInstance().log("Update Shipping");

                String deleteDetails = String.format("DELETE FROM orderdetails WHERE orderId = %d", orderId);
                stmt.executeUpdate(deleteDetails);
                AuditService.getInstance().log("Delete Order Details");

                clearConsole();
                System.out.println("\033[45mOrder with ID " + choice + " has been cancelled.\033[0m");
            } else {
                System.out.println("\033[31mOrder with ID " + choice + " not found.\033[0m");
            }
        }
    }
    public void showOrderDetails() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose the order you want to see:");
        listOrders();
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();

        try(Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement()){
            String query = """
            SELECT o.orderId, o.customerId, o.dateOfOrder,
                   s.shippingCountry, s.shippingCity, s.shippingPostalCode,
                   s.trackingNumber, s.statusOfOrder, s.shippingMethod, s.estimatedDelivery
            FROM `order` o
            JOIN `shipping` s ON o.shippingId = s.shippingId
            WHERE o.orderId = """ + choice;


            ResultSet rs = stmt.executeQuery(query);
            clearConsole();

            if (rs.next()) {
                System.out.println("\033[45m~~~~~ ORDER DETAILS FOR ORDER " + rs.getInt("orderId") + " ~~~~~\033[0m");
                System.out.println("Customer ID: " + rs.getInt("customerId"));
                System.out.println("-Date of Order: " + rs.getDate("dateOfOrder"));
                System.out.println("-Address of Order: " + rs.getString("shippingCountry") + ", " +
                        rs.getString("shippingCity") + ", " + rs.getString("shippingPostalCode"));
                System.out.println("-Tracking Number: " + rs.getString("trackingNumber"));
                System.out.println("-Status of Order: " + rs.getString("statusOfOrder"));
                System.out.println("-Shipping Method: " + rs.getString("shippingMethod"));
                System.out.println("-Estimated Delivery Date: " + rs.getString("estimatedDelivery"));
            } else {
                System.out.println("No order found with ID " + choice);
            }


            String productSql = """
            SELECT p.productId, p.productName, p.productPrice,
                   prov.providerName
            FROM orderdetails od
            JOIN product p ON od.productId = p.productId
            JOIN provider prov ON od.providerId = prov.providerId
            WHERE od.orderId = """ + choice;

            ResultSet rs2 = stmt.executeQuery(productSql);

            System.out.println("-Order content:");
            while (rs2.next()) {
                String providerName = rs2.getString("providerName");
                String productName = rs2.getString("productName");
                double productPrice = rs2.getDouble("productPrice");
                System.out.printf("Provider: %s | Product: %s | Price: %.2f\n", providerName, productName, productPrice);
            }
            System.out.println();
            AuditService.getInstance().log("Read Order Details");
        }

    }
    public void addProductsToOrder(int orderId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        String queryProviders = "SELECT DISTINCT p.providerId, p.providerName FROM orderdetails od " +
                "JOIN provider p ON od.providerId = p.providerId WHERE od.orderId = " + orderId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rsProviders = stmt.executeQuery(queryProviders)) {

            List<Integer> providerIds = new ArrayList<>();
            System.out.println("\033[36mAvailable Providers in your order:\033[0m");
            int index = 1;
            while (rsProviders.next()) {
                int id = rsProviders.getInt("providerId");
                String name = rsProviders.getString("providerName");
                providerIds.add(id);
                System.out.println(index++ + ". " + name);
            }
            System.out.print("Choose a provider to add products from: ");
            int providerChoice = scanner.nextInt();
            int providerId = providerIds.get(providerChoice - 1);

            String queryProducts = "SELECT productId, productName, productPrice FROM product WHERE providerId = " + providerId;
            ResultSet rsProducts = stmt.executeQuery(queryProducts);
            AuditService.getInstance().log("Read Products");
            List<Integer> productIds = new ArrayList<>();
            List<String> productNames = new ArrayList<>();
            List<Double> productPrices = new ArrayList<>();
            index = 1;
            System.out.println("\033[36mAvailable Products:\033[0m");
            while (rsProducts.next()) {
                int pid = rsProducts.getInt("productId");
                String name = rsProducts.getString("productName");
                double price = rsProducts.getDouble("productPrice");
                productIds.add(pid);
                productNames.add(name);
                productPrices.add(price);
                System.out.println(index++ + ". " + name + " | Price: " + price);
            }

            System.out.print("Choose the product to add: ");
            int productChoice = scanner.nextInt();
            int selectedProductId = productIds.get(productChoice - 1);

            String insert = String.format("INSERT INTO orderdetails (orderId, productId, providerId, quantity) VALUES (%d, %d, %d, 1)", orderId, selectedProductId, providerId);
            try (Statement stmtInsert = conn.createStatement()) {
                stmtInsert.executeUpdate(insert);
                System.out.println("\033[32mProduct added to order successfully.\033[0m\n");
                AuditService.getInstance().log("Create Order Details");
            }
        }
    }
    public void removeProductFromOrder(int orderId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String query = """
        SELECT od.productId, p.productName, prov.providerName
        FROM orderdetails od
        JOIN product p ON od.productId = p.productId
        JOIN provider prov ON od.providerId = prov.providerId
        WHERE od.orderId = """ + orderId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Integer> productIds = new ArrayList<>();
            List<String> productLabels = new ArrayList<>();
            int index = 1;

            System.out.println("\033[36mProducts in this order:\033[0m");
            while (rs.next()) {
                int pid = rs.getInt("productId");
                String name = rs.getString("productName");
                String provider = rs.getString("providerName");
                productIds.add(pid);
                productLabels.add(name + " (Provider: " + provider + ")");
                System.out.println(index++ + ". " + productLabels.get(productLabels.size() - 1));
            }
            AuditService.getInstance().log("Read Products");
            System.out.print("Choose a product to remove: ");
            int choice = scanner.nextInt();
            int productIdToRemove = productIds.get(choice - 1);

            String deleteSql = String.format("DELETE FROM orderdetails WHERE orderId = %d AND productId = %d", orderId, productIdToRemove);
            try (Statement deleteStmt = conn.createStatement()) {
                deleteStmt.executeUpdate(deleteSql);
                System.out.println("\033[32mProduct removed from order.\033[0m\n");
                AuditService.getInstance().log("Delete Order Details");
            }
        }
    }
    private static void addProviderToOrder(int orderId, Scanner scanner) {
        final String URL = "jdbc:mysql://localhost:3306/onydepot";
        final String USER = "root";
        final String PASSWORD = "parola";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();) {

            String sqlProviders = "SELECT providerId, providerName FROM provider " +
                    "WHERE providerId NOT IN (SELECT DISTINCT providerId FROM orderdetails WHERE orderId = " + orderId + ")";
            ResultSet rs = stmt.executeQuery(sqlProviders);

            List<Integer> providerIds = new ArrayList<>();
            List<String> providerNames = new ArrayList<>();
            int index = 1;
            System.out.println("\033[36mAvailable Providers (not in current order):\033[0m");
            while (rs.next()) {
                providerIds.add(rs.getInt("providerId"));
                providerNames.add(rs.getString("providerName"));
                System.out.println(index++ + ". " + rs.getString("providerName"));
            }
            if (providerIds.isEmpty()) {
                System.out.println("\033[31mThere are no available providers to add in your order!\033[0m\n");
                return;
            }
            else AuditService.getInstance().log("Read Providers");

            System.out.print("\033[36mEnter the number for the new provider: \033[0m");
            int choice = scanner.nextInt();
            scanner.nextLine();
            int selectedProviderId = providerIds.get(choice - 1);
            String selectedProviderName = providerNames.get(choice - 1);

            String sqlProducts = "SELECT productId, productName, productPrice FROM product WHERE providerId = " + selectedProviderId;
            rs = stmt.executeQuery(sqlProducts);
            AuditService.getInstance().log("Read Products");
            List<Integer> productIds = new ArrayList<>();
            index = 1;
            System.out.println("\033[36mAvailable Products from provider " + selectedProviderName + ":\033[0m");
            while (rs.next()) {
                productIds.add(rs.getInt("productId"));
                System.out.println(index++ + ". " + rs.getString("productName") + " | Price: " + rs.getDouble("productPrice"));
            }

            System.out.print("\033[36mChoose product number: \033[0m");
            int productChoice = scanner.nextInt();
            scanner.nextLine();
            int selectedProductId = productIds.get(productChoice - 1);
            System.out.print("\033[36mEnter quantity: \033[0m");
            int quantity = scanner.nextInt();

            String insertSql = "INSERT INTO orderdetails (orderId, productId, providerId, quantity) " +
                    "VALUES (" + orderId + ", " + selectedProductId + ", " + selectedProviderId + ", " + quantity + ")";
            stmt.executeUpdate(insertSql);
            System.out.println("\033[32mProvider " + selectedProviderName + " and product added to order.\033[0m");
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    private static void removeProviderWithProducts(int orderId, Scanner scanner) {
        final String URL = "jdbc:mysql://localhost:3306/onydepot";
        final String USER = "root";
        final String PASSWORD = "parola";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();) {

            //existent providers in order
            String sql = "SELECT DISTINCT p.providerId, p.providerName FROM provider p " +
                    "JOIN orderdetails od ON p.providerId = od.providerId " +
                    "WHERE od.orderId = " + orderId;
            ResultSet rs = stmt.executeQuery(sql);
            AuditService.getInstance().log("Read Providers");
            List<Integer> providerIds = new ArrayList<>();
            List<String> providerNames = new ArrayList<>();
            int index = 1;
            System.out.println("\033[36mAvailable Providers to remove:\033[0m");
            while (rs.next()) {
                providerIds.add(rs.getInt("providerId"));
                providerNames.add(rs.getString("providerName"));
                System.out.println(index++ + ". " + rs.getString("providerName"));
            }
            if (providerIds.isEmpty()) {
                System.out.println("\033[31mNo providers found in this order to remove.\033[0m\n");
                return;
            }

            System.out.print("\033[36mChoose the provider you want to remove: \033[0m");
            int choice = scanner.nextInt();
            int selectedProviderId = providerIds.get(choice - 1);
            String selectedProviderName = providerNames.get(choice - 1);
            //delete order details for the choosen provider and order
            String deleteSql = "DELETE FROM orderdetails WHERE orderId = " + orderId + " AND providerId = " + selectedProviderId;
            stmt.executeUpdate(deleteSql);
            AuditService.getInstance().log("Delete Order Details");
            System.out.println("\033[32mProvider " + selectedProviderName + " and their products removed from the order.\033[0m\n");

        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    public void updateOrder(Scanner scanner) {
        final String URL = "jdbc:mysql://localhost:3306/onydepot";
        final String USER = "root";
        final String PASSWORD = "parola";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();) {

            String fetchOrders = "SELECT orderId FROM `order`";
            ResultSet rs = stmt.executeQuery(fetchOrders);
            List<Integer> orderIds = new ArrayList<>();
            System.out.println("Choose the order you want to edit:");
            int idx = 1;
            while (rs.next()) {
                int id = rs.getInt("orderId");
                orderIds.add(id);
                System.out.println(idx++ + ": Order ID " + id);
            }

            System.out.print("Your choice: ");
            int orderChoice = scanner.nextInt();
            int selectedOrderId = orderIds.get(orderChoice - 1);
            clearConsole();

            System.out.println("""
            Choose what you want to edit for this order:
            1. Order's shipping
            2. Order's content
            3. Status
            Your choice: """);
            int editChoice = scanner.nextInt();
            scanner.nextLine();
            clearConsole();

            switch (editChoice) {
                case 1: {
                    rs = stmt.executeQuery("SELECT shippingId FROM `order` WHERE orderId = " + selectedOrderId);
                    rs.next();
                    int shippingId = rs.getInt("shippingId");

                    System.out.println("""
                    What do you want to edit for order's shipping?
                    1. City
                    2. Country
                    3. Postal Code
                    4. Method of shipping
                    5. The tracking number
                    6. The estimated delivery date
                    Your choice:""");
                    int shipChoice = scanner.nextInt();
                    scanner.nextLine();
                    clearConsole();

                    String field = null, value = null;
                    switch (shipChoice) {
                        case 1:
                            System.out.print("Enter new city: ");
                            value = scanner.nextLine();
                            field = "shippingCity";
                            break;
                        case 2:
                            System.out.print("Enter new country: ");
                            value = scanner.nextLine();
                            field = "shippingCountry";
                            break;
                        case 3:
                            System.out.print("Enter new postal code: ");
                            value = scanner.nextLine();
                            field = "shippingPostalCode";
                            break;
                        case 4:
                            System.out.print("Enter new shipping method: ");
                            value = scanner.nextLine();
                            field = "shippingMethod";
                            break;
                        case 5:
                            System.out.print("Enter new tracking number: ");
                            value = scanner.nextLine();
                            field = "trackingNumber";
                            break;
                        case 6:
                            System.out.print("Year: ");
                            int y = scanner.nextInt();
                            System.out.print("Month: ");
                            int m = scanner.nextInt();
                            System.out.print("Day: ");
                            int d = scanner.nextInt();
                            value = "'" + y + "-" + m + "-" + d + "'";
                            field = "estimatedDelivery";
                            break;
                        default:
                            System.out.println("\033[31mInvalid choice.\033[0m");
                            break;
                    }
                    if (field != null) {
                        String update = "UPDATE shipping SET " + field + " = " + (value.startsWith("'") ? value : "'" + value + "'") + " WHERE shippingId = " + shippingId;
                        stmt.executeUpdate(update);
                        AuditService.getInstance().log("Update Shipping");
                        System.out.println("\033[32mShipping info updated successfully.\033[0m");
                    }
                    break;
                }
                case 2: {
                    boolean exit = false;
                    while (!exit) {
                        System.out.println("""
                        \033[36mEDITING ORDER CONTENT\033[0m
                        1. Add product from in-order provider
                        2. Remove product
                        3. Add product from new provider
                        4. Remove provider with products
                        0. Back to MAIN MENU""");
                        System.out.print("Your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1:
                                addProductsToOrder(selectedOrderId);
                                break;
                            case 2:
                                removeProductFromOrder(selectedOrderId);
                                break;
                            case 3:
                                addProviderToOrder(selectedOrderId, scanner);
                                break;
                            case 4:
                                removeProviderWithProducts(selectedOrderId, scanner);
                                break;
                            case 0:
                                exit = true;
                                break;
                            default:
                                System.out.println("Invalid option.");
                                break;
                        }
                        if (!exit) {
                            waitForEnter();
                            clearConsole();
                        }
                        AuditService.getInstance().log("Update Order Details");
                    }
                    break;
                }
                case 3:
                    rs = stmt.executeQuery("SELECT shippingId FROM `order` WHERE orderId = " + selectedOrderId);
                    rs.next();
                    int shippingId = rs.getInt("shippingId");

                    System.out.print("Enter the new status for order: ");
                    String newStatus = scanner.nextLine();
                    String update = "UPDATE shipping SET statusOfOrder = '" + newStatus + "' WHERE shippingId = " + shippingId;
                    stmt.executeUpdate(update);
                    AuditService.getInstance().log("Update Shipping");
                    System.out.println("\033[32mOrder status updated.\033[0m");
                    break;
                default:
                    System.out.println("\033[31mInvalid option.\033[0m");
                    break;
            }
        } catch (SQLException e) {
            System.out.println("DB ERROR: " + e.getMessage());
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    /// PROVIDERS
    public void addProvider() {
        Provider provider = new Provider(); // Colectezi date + produse
        String insertProviderSQL = "INSERT INTO provider (providerName, providerEmail, providerAddress, providerPhone) VALUES ('"
                + provider.getProviderName().replace("'", "''") + "', '"
                + provider.getProviderEmail().replace("'", "''") + "', '"
                + provider.getProviderAddress().replace("'", "''") + "', '"
                + provider.getProviderPhone().replace("'", "''") + "')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Inserează providerul și obține ID-ul generat
            stmt.executeUpdate(insertProviderSQL, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                int providerId = rs.getInt(1);

                // 2. Inserează produsele asociate
                for (Product p : provider.getAvailableProducts()) {
                    String type = (p instanceof PerishableProduct) ? "perishable" : "nonperishable";
                    String expirationDate = (p instanceof PerishableProduct) ?
                            "'" + ((PerishableProduct) p).getExpirationDate() + "'" : "NULL";
                    String storageInstructions = (p instanceof NonperishableProduct) ?
                            "'" + ((NonperishableProduct) p).getStorageInstructions().replace("'", "''") + "'" : "NULL";

                    String insertProductSQL = String.format("""
                        INSERT INTO product (
                            productType, productName, productCategory, productDescription,
                            productPrice, productStock, productRating,
                            expirationDate, storageInstructions, providerId
                        ) VALUES (
                            '%s', '%s', '%s', '%s', %.2f, %d, %.2f,
                            %s, %s, %d
                        )
                        """,
                            type,
                            p.getProductName().replace("'", "''"),
                            p.getProductCategory().replace("'", "''"),
                            p.getProductDescription().replace("'", "''"),
                            p.getProductPrice(),
                            p.getProductStock(),
                            p.getProductRating(),
                            expirationDate,
                            storageInstructions,
                            providerId
                    );

                    stmt.executeUpdate(insertProductSQL);
                }

                System.out.println("\u001B[36mProvider and associated products added successfully!\u001B[0m");
                AuditService.getInstance().log("Create Provider & Products");
            } else {
                System.out.println("\u001B[31mFailed to retrieve generated provider ID.\u001B[0m");
            }

        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
    public String formatProductsForProvider(int providerId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT productName, productPrice FROM product WHERE providerId = " + providerId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("productName");
                double price = rs.getDouble("productPrice");
                sb.append("     -Product: ").append(name).append(" | Price: ").append(price).append("\n");
            }
        }
        return sb.toString();
    }
    public void showAProvider(int providerId) throws SQLException {
        String sql = "SELECT * FROM provider WHERE providerId=" + providerId;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs2 = stmt.executeQuery(sql)) {

            if (rs2.next()) {
                String name = rs2.getString("providerName");
                String address = rs2.getString("providerAddress");
                String phone = rs2.getString("providerPhone");
                String email = rs2.getString("providerEmail");

                System.out.println("\u001B[45mPROVIDER PROFILE for " + name + "\u001B[0m");
                System.out.println("-Provider name: " + name);
                System.out.println("-Provider ID: " + providerId);
                System.out.println("-Provider Address: " + address);
                System.out.println("-Provider Phone Number: " + phone);
                System.out.println("-Provider Email: " + email);
                System.out.print(formatProductsForProvider(providerId));
                AuditService.getInstance().log("Read Provider");
            } else {
                System.out.println("\u001B[31mProvider not found!\u001B[0m");
            }
        }
    }
    public void showProviders() throws SQLException {
        String sql = "SELECT providerId FROM provider";
        List<Integer> providerIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                providerIds.add(rs.getInt("providerId"));
            }
        }

        for (int id : providerIds)
        {
            showAProvider(id);
            System.out.println();
        }
    }

    private Map<Integer, Integer> indexToProviderId = new HashMap<>(); //index -> providerId
    public void listProviders() throws SQLException {
        String sql = "SELECT providerId, providerName FROM provider";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int index=1;
            indexToProviderId.clear(); //resetez maparea
            while (rs.next()) {
                int id = rs.getInt("providerId");
                String name = rs.getString("providerName");
                System.out.println(index + ". Id: " + id + " - Name: " + name);
                indexToProviderId.put(index, id);
                index++;
            }
            AuditService.getInstance().log("Read Providers");
        }
    }
    public void showAProvider() throws SQLException {
        System.out.println("Enter the INDEX for the provider you want to show from the list above: ");
        listProviders();
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        Integer providerId = indexToProviderId.get(id);
        clearConsole();
        if (providerId != null)
            showAProvider(providerId);
        else
            System.out.println("\u001B[31mInvalid choice.\u001B[0m");
       }
    public void updateProvider() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the ID for the provider you want to update: ");
        listProviders();
        int id = scanner.nextInt();
        scanner.nextLine();
        clearConsole();
        System.out.print("What do you want to edit?\n1. Provider's Name\n2. Provider's Address\n3. Provider's Phone\n4. Provider's Email\n5. Provider's Products\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        clearConsole();
        String field = null;
        switch (choice) {
            case 1:
                field = "providerName";
                break;
            case 2:
                field = "providerAddress";
                break;
            case 3:
                field = "providerPhone";
                break;
            case 4:
                field = "providerEmail";
                break;
            case 5:
                updateProviderProducts(id);
                break;
            default:
                System.out.println("\u001B[31mInvalid choice!\u001B[0m");
                return;
        }
        if(field!=null){
            System.out.print("Enter the new value that you want to edit: ");
            String newValue = scanner.nextLine();
            String sql = "UPDATE provider SET " + field + " = '" + newValue.replace("'", "''") + "' WHERE providerId = " + id;

            try(Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {
                int rows=stmt.executeUpdate(sql);
                if(rows>0){
                    System.out.println("\u001B[36mProvider updated successfully!\u001B[0m");
                    AuditService.getInstance().log("Update Provider");}
                else
                    System.out.println("\u001B[31mProvider not found!\u001B[0m");
            }
        }
    }
    public void addProductForProvider(int providerId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("For adding a perishable product, press 1. For adding a nonperishable product, press 2.\nYour choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        clearConsole();
        String type;
        String sql;
        if (choice == 1) {
            PerishableProduct product = new PerishableProduct();
            sql = "INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES (" +
                    "'perishable', '" + product.getProductName() + "', '" + product.getProductCategory() + "', '" + product.getProductDescription() + "', " +
                    product.getProductPrice() + ", " + product.getProductStock() + ", " + product.getProductRating() + ", '" +
                    product.getExpirationDate() + "', NULL, " + providerId + ")";
        }
        else {
            NonperishableProduct product = new NonperishableProduct();
            sql = "INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES (" +
                    "'nonperishable', '" + product.getProductName() + "', '" + product.getProductCategory() + "', '" + product.getProductDescription() + "', " +
                    product.getProductPrice() + ", " + product.getProductStock() + ", " + product.getProductRating() + ", NULL, '" +
                    product.getStorageInstructions() + "', " + providerId + ")";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("\u001B[36mProduct added successfully for provider ID " + providerId + "!\u001B[0m");
            AuditService.getInstance().log("Create Product");
        }
    }
    public void deleteProductForProvider(int providerId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        List<Integer> productIds = new ArrayList<>();

        String sql = "SELECT productId, productName FROM product WHERE providerId = " + providerId;
        System.out.println("The provider has the following products: ");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int index = 1;
            while (rs.next()) {
                productIds.add(rs.getInt("productId"));
                System.out.println(index + ". " + rs.getString("productName"));
                index++;
            }
        }
        if (productIds.isEmpty()) {
            System.out.println("\u001B[31mNo products found for this provider.\u001B[0m");
            return;
        }

        System.out.print("Choose a product to delete.\nYour choice: ");
        int choice = scanner.nextInt();
        int productIdToRemove = productIds.get(choice-1);

        String deleteSQL = "DELETE FROM product WHERE productId = " + productIdToRemove;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
            System.out.println("\u001B[36mProduct deleted successfully for provider ID " + providerId + "!\u001B[0m");
            AuditService.getInstance().log("Delete Product");
        }
    }
    public void updateProviderProducts(int providerId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At the moment, provider ID " + providerId + " has the following products: \n" + formatProductsForProvider(providerId));
        System.out.print("What do you want to operate on these products?\n1.Add a product for provider\n2.Delete a product from provider\nYour choice: ");
        int choice = scanner.nextInt();
        clearConsole();
        switch (choice) {
            case 1:
                addProductForProvider(providerId);
                break;
            case 2:
                deleteProductForProvider(providerId);
                break;
            default:
                System.out.println("\u001B[31mInvalid choice!\u001B[0m");
                break;
        }
    }
    public void deleteProvider() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        listProviders();
        System.out.print("Enter the ID for the provider you want to delete: ");
        int id = scanner.nextInt();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM product WHERE providerId = " + id);
                int rows = stmt.executeUpdate("DELETE FROM provider WHERE providerId = " + id);
                conn.commit();

                if(rows>0) {
                    System.out.println("\u001B[36mProvider removed successfully.\u001B[0m");
                    AuditService.getInstance().log("Delete Provider");
                }
                else
                    System.out.println("\u001B[31mProvider not found.\u001B[0m");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public void showTheBestProviders() throws SQLException {
        String sql = "SELECT p.providerId, pr.providerName, SUM(od.quantity) AS totalSales " +
                    "FROM `order` o " +
                    "JOIN orderdetails od ON o.orderId = od.orderId " +
                    "JOIN product p ON od.productId = p.productId " +
                    "JOIN provider pr ON p.providerId = pr.providerId " +
                    "GROUP BY p.providerId, pr.providerName " +
                    "ORDER BY totalSales DESC";

        List<Integer> topProviderIds = new ArrayList<>();
        int topSales = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\u001B[45m\u001B[1m        ~~~~~ THE BEST PROVIDERS ~~~~~        \u001B[0m");
            System.out.println("\u001B[3mNote that this information is based on the provided data at " + LocalDate.now() + ". The best providers may change in time based on future placed orders.\u001B[0m\n");

            while (rs.next()) {
                int providerId = rs.getInt("providerId");
                int sales = rs.getInt("totalSales");

                if (topSales == -1)
                    topSales = sales;
                if (sales < topSales)
                    break;

                topProviderIds.add(providerId);
            }
        }

        for (int providerId : topProviderIds) {
            showAProvider(providerId);
            System.out.println();
        }
    }

    ///CONSOLE
    public static void printMenu() {
        System.out.println("\n" + "\033[45m\033[1m        ~~~~~ ONY DEPOT ~~~~~        \033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m      ~~ CUSTOMER OPERATIONS ~~\033[0m");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[37m1. Add Customer\033[0m");
        System.out.println("\033[37m2. Show Customer\033[0m");
        System.out.println("\033[37m3. Show all Customers\033[0m");
        System.out.println("\033[37m4. Edit Customer\033[0m");
        System.out.println("\033[37m5. Delete Customer\033[0m");
        System.out.println("\033[37m6. Get orders for a certain Customer\033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m       ~~ PRODUCT OPERATIONS ~~\033[0m");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[37m7. Add a Product\033[0m");
        System.out.println("\033[37m8. Show a Product\033[0m");
        System.out.println("\033[37m9. Edit Product\033[0m");
        System.out.println("\033[37m10. Remove Product\033[0m");
        System.out.println("\033[37m11. Show Low Stock Products\033[0m");
        System.out.println("\033[37m12. Apply a discount for a Product\033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m      ~~ INVENTORY OPERATIONS ~~\033[0m");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[37m13. Show Perishable Products\033[0m");
        System.out.println("\033[37m14. Show Nonperishable Products\033[0m");
        System.out.println("\033[37m15. Show Expiring Products\033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m        ~~ ORDER OPERATIONS ~~\033[0m");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[37m16. Register Order\033[0m");
        System.out.println("\033[37m17. Show Order Details\033[0m");
        System.out.println("\033[37m18. Update Order\033[0m");
        System.out.println("\033[37m19. Cancel Order\033[0m");
        System.out.println("\033[37m20. Show Bestseller Products\033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m       ~~ PROVIDER OPERATIONS ~~\033[0m");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[37m21. Add a Provider\033[0m");
        System.out.println("\033[37m22. Show Providers\033[0m");
        System.out.println("\033[37m23. Show a Certain Provider\033[0m");
        System.out.println("\033[37m24. Update Provider\033[0m");
        System.out.println("\033[37m25. Delete Provider\033[0m");
        System.out.println("\033[37m26. Show the best Providers\033[0m" + "\n");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.println("\033[35m +++++++++Press 0 to EXIT+++++++++\033[0m ");
        System.out.println("\033[46m=====================================\033[0m");
        System.out.print("Your choice: ");
    }
    public static void clearConsole() {
        for (int i = 0; i < 50; i++)
            System.out.println();
    }
    public static void waitForEnter() {
        System.out.println("\n" + "\033[46m=====================================\033[0m");
        System.out.println("\033[35m Press ENTER to go back to the MENU\033[0m ");
        System.out.println("\033[46m=====================================\033[0m");

        Scanner input = new Scanner(System.in);
        input.nextLine();
    }
}

