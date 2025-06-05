package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String URL = "jdbc:mysql://localhost:3306/onydepot";
    private static final String USER = "root";
    private static final String PASSWORD = "parola";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
                String createCustomer = """
                        CREATE TABLE IF NOT EXISTS customer (
                            customerId INT PRIMARY KEY AUTO_INCREMENT,
                            customerName VARCHAR(100) NOT NULL,
                            customerEmail VARCHAR(100) NOT NULL,
                            customerAddress VARCHAR(100) NOT NULL,
                            customerPhone VARCHAR(20) NOT NULL
                        )
                        """;
                stmt.executeUpdate(createCustomer);

                String createProvider = """
                        CREATE TABLE IF NOT EXISTS provider (
                            providerId INT PRIMARY KEY AUTO_INCREMENT,
                            providerName VARCHAR(100) NOT NULL,
                            providerEmail VARCHAR(100) NOT NULL,
                            providerAddress VARCHAR(100) NOT NULL,
                            providerPhone VARCHAR(20) NOT NULL
                        )
                        """;
                stmt.executeUpdate(createProvider);

                //type is perishable => expiration date exists
                //type is nonperishable => storage instructions exist
                String createProduct = """
                        CREATE TABLE IF NOT EXISTS product (
                            productId INT PRIMARY KEY AUTO_INCREMENT,
                            productType ENUM('perishable', 'nonperishable') NOT NULL,
                            productName VARCHAR(100) NOT NULL,
                            productCategory VARCHAR(100) NOT NULL,
                            productDescription VARCHAR(200) NOT NULL,
                            productPrice DOUBLE PRECISION NOT NULL,
                            productStock INT NOT NULL,
                            productRating DOUBLE PRECISION NOT NULL,
                            expirationDate DATE, 
                            storageInstructions VARCHAR(150), 
                            providerId INT,
                            FOREIGN KEY (providerId) REFERENCES provider(providerId)
                        )
                        """;
                stmt.executeUpdate(createProduct);

                String createShipping = """
                        CREATE TABLE IF NOT EXISTS shipping(
                            shippingId INT PRIMARY KEY AUTO_INCREMENT,
                            shippingCity VARCHAR(100) NOT NULL,
                            shippingCountry VARCHAR(100) NOT NULL,
                            shippingPostalCode VARCHAR(100) NOT NULL,
                            shippingMethod VARCHAR(100) NOT NULL,
                            trackingNumber VARCHAR(100) NOT NULL,
                            statusOfOrder VARCHAR(100) NOT NULL,
                            estimatedDelivery DATE NOT NULL
                        )
                        """;
                stmt.executeUpdate(createShipping);

                String createOrder = """
                        CREATE TABLE IF NOT EXISTS `order`(
                            orderId INT PRIMARY KEY AUTO_INCREMENT,
                            customerId INT NOT NULL,
                            shippingId INT NOT NULL,
                            dateOfOrder DATE NOT NULL,
                            FOREIGN KEY (customerId) REFERENCES customer(customerId),
                            FOREIGN KEY (shippingId) REFERENCES shipping(shippingId)
                        )
                        """;
                stmt.executeUpdate(createOrder);
                String createOrderDetails = """
                        CREATE TABLE IF NOT EXISTS orderdetails (
                            orderDetailsId INT PRIMARY KEY AUTO_INCREMENT,
                            orderId INT NOT NULL,
                            productId INT NOT NULL,
                            providerId INT NOT NULL,
                            quantity INT NOT NULL,
                            FOREIGN KEY (orderId) REFERENCES `order`(orderId),
                            FOREIGN KEY (productId) REFERENCES product(productId),
                            FOREIGN KEY (providerId) REFERENCES provider(providerId)
                        )
                        """;
                stmt.executeUpdate(createOrderDetails);
        }
        catch (SQLException e) {
            System.out.println("ERROR: "+e.getMessage());
        }
    }
}
