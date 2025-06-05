package db;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void seedDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            seedCustomers(conn);
            seedProviders(conn);
            seedProducts(conn);
            seedShippings(conn);
            seedOrders(conn);
            seedOrderDetails(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedCustomers(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet customers = stmt.executeQuery("SELECT COUNT(*) FROM customer");
        customers.next();
        int count = customers.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO customer (customerName, customerEmail, customerAddress, customerPhone) VALUES ('Popescu Ioana', 'popescu_ioana@yahoo.com', 'Str. Lalelelor nr. 29', '0755678987')");
            stmt.executeUpdate("INSERT INTO customer (customerName, customerEmail, customerAddress, customerPhone) VALUES ('Cirnu Eliana-Maria', 'cirnu_eliana@yahoo.com', 'Str. Livezi nr. 7', '0754322237')");
            stmt.executeUpdate("INSERT INTO customer (customerName, customerEmail, customerAddress, customerPhone) VALUES ('Serafim Ionut', 'serafim_ionut@yahoo.com', 'Str. Locotenent Marian nr. 9', '0789982216')");
            stmt.executeUpdate("INSERT INTO customer (customerName, customerEmail, customerAddress, customerPhone) VALUES ('Cruceru Mihai', 'cruceru_mihai@yahoo.com', 'Str. Zambilelor nr. 23', '0753320091')");

            System.out.println("Customers successfully inserted.");
        } else {
            System.out.println("Customers already exist in database.");
        }
    }

    private static void seedProviders(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet providers = stmt.executeQuery("SELECT COUNT(*) FROM provider");
        providers.next();
        int count = providers.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO provider (providerName, providerEmail, providerAddress, providerPhone) VALUES ('Protopopescu Ionel-Sergiu', 'prtp_ion@yahoo.com', 'Str. Lalelelor nr. 4', '0733678987')");
            stmt.executeUpdate("INSERT INTO provider (providerName, providerEmail, providerAddress, providerPhone) VALUES ('Colcaroiu Sorin', 'colcaroius@yahoo.com', 'Str. Livezilor nr. 37', '0754367899')");
            stmt.executeUpdate("INSERT INTO provider (providerName, providerEmail, providerAddress, providerPhone) VALUES ('Cotofana Dragos-Daniel', 'dragos.dany@yahoo.com', 'Str. Gorjului nr. 9', '0789211236')");

            System.out.println("Providers successfully inserted.");
        } else {
            System.out.println("Providers already exist in database.");
        }
    }

    private static void seedProducts(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet products = stmt.executeQuery("SELECT COUNT(*) FROM product");
        products.next();
        int count = products.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('perishable', 'Lava cake congelat', 'Alimente congelate', 'Acesta este un produs dulce, congelat, care contine multa ciocolata. A se decongela si prepara la cuptor inainte de consumare', 23.49, 57, 0, '2025-09-25', NULL, 1)");
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('perishable', 'Rulada cu cocos', 'Cofetarie', 'A se consuma inainte de data inscriptionata pe ambalaj. Este un aliment cu mult zahar.', 18.99, 96, 0, '2025-11-25', NULL, 1)");
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('perishable', 'Paine cu mix de seminte', 'Panificatie', 'A se consuma in maximum 5 zile de la deschidere!', 7.99, 267, 0, '2025-04-17', NULL, 2)");
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('perishable', 'Gogoasa cu ciocolata', 'Cofetarie', 'Acesta este un desert foarte apreciat de clientii nostri!', 6.50, 23, 0, '2025-04-10', NULL, 1)");

            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('nonperishable', 'Aspirator mic', 'Electrocasnice mici', 'Noul aspirator mic - cu zgomot si mai mic!', 579.99, 37, 0, NULL, 'A se feri de apa.', 3)");
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('nonperishable', 'Fier de calcat vertical', 'Electrocasnice mici', 'Cu o capacitate de 500ml de apa, acest fier de calcat pe baza de aburi calca orice ai nevoie in doar 5 minute!', 109.99, 101, 0, NULL, 'A se feri de foc si apa.', 2)");
            stmt.executeUpdate("INSERT INTO product (productType, productName, productCategory, productDescription, productPrice, productStock, productRating, expirationDate, storageInstructions, providerId) VALUES ('nonperishable', 'Masina de spalat', 'Electrocasnice mari', 'Destinata uzului zilnic, cu o capacitate de 10kg.', 1987.50, 79, 0, NULL, 'A se feri de apa, inghet, razele directe ale soarelui si foc.', 3)");

            System.out.println("Products successfully inserted.");
        } else {
            System.out.println("Products already exist in database.");
        }
    }

    private static void seedShippings(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet shippings = stmt.executeQuery("SELECT COUNT(*) FROM shipping");
        shippings.next();
        int count = shippings.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO shipping (shippingCity, shippingCountry, shippingPostalCode, shippingMethod, trackingNumber, statusOfOrder, estimatedDelivery) VALUES ('Bucuresti', 'Romania', '010101', 'Curier', 'TRK123456', 'In curs de livrare', '2025-05-27')");
            stmt.executeUpdate("INSERT INTO shipping (shippingCity, shippingCountry, shippingPostalCode, shippingMethod, trackingNumber, statusOfOrder, estimatedDelivery) VALUES ('Cluj-Napoca', 'Romania', '400001', 'Posta', 'TRK654321', 'Livrat', '2025-05-25')");

            System.out.println("Shippings successfully inserted.");
        } else {
            System.out.println("Shippings already exist in database.");
        }
    }

    private static void seedOrders(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet orders = stmt.executeQuery("SELECT COUNT(*) FROM `order`");
        orders.next();
        int count = orders.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO `order` (customerId, shippingId, dateOfOrder) VALUES (1, 1, '2025-05-20')");
            stmt.executeUpdate("INSERT INTO `order` (customerId, shippingId, dateOfOrder) VALUES (2, 2, '2025-05-21')");

            System.out.println("Orders successfully inserted.");
        } else {
            System.out.println("Orders already exist in database.");
        }
    }

    private static void seedOrderDetails(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        ResultSet orderDetails = stmt.executeQuery("SELECT COUNT(*) FROM orderdetails");
        orderDetails.next();
        int count = orderDetails.getInt(1);

        if (count == 0) {
            stmt.executeUpdate("INSERT INTO orderdetails (orderId, productId, providerId, quantity) VALUES (1, 1, 1, 10)");
            stmt.executeUpdate("INSERT INTO orderdetails (orderId, productId, providerId, quantity) VALUES (2, 3, 2, 5)");

            System.out.println("OrderDetails successfully inserted.");
        } else {
            System.out.println("OrderDetails already exist in database.");
        }
    }
}
