import db.DatabaseConnection;
import db.DatabaseSeeder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException; 
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        Service service = new Service();
        service.databaseInit();

        boolean var=true;
        while(var)
        {
            service.printMenu();
            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
            service.clearConsole();
            switch (choice) {
                case 1:
                    service.addCustomer();
                    break;
                case 2:
                    service.showCustomer();
                    break;
                case 3:
                    service.showAllCustomers();
                    break;
                case 4:
                    service.editCustomer();
                    break;
                case 5:
                    service.deleteCustomer();
                    break;
                case 6:
                    service.showOrdersForCustomer();
                    break;
                case 7:
                    service.addProduct();
                    break;
                case 8:
                    service.showAProduct();
                    break;
                case 9:
                    service.editProduct();
                    break;
                case 10:
                    service.removeProduct();
                    break;
                case 11:
                    service.showLowStockProducts();
                    break;
                case 12:
                    service.applyDiscount();
                    break;
                case 13:
                    service.showInventoryForPerishableProducts();
                    break;
                case 14:
                    service.showInventoryForNonperishableProducts();
                    break;
                case 15:
                    service.showExpiringProducts();
                    break;
                case 16:
                    service.registerOrder();
                    break;
                case 17:
                    service.showOrderDetails();
                    break;
                case 18:
                    service.updateOrder(input);
                    break;
                case 19:
                    service.cancelOrder();
                    break;
                case 20:
                    service.showBestsellerProducts();
                    break;
                case 21:
                    service.addProvider();
                    break;
                case 22:
                    service.showProviders();
                    break;
                case 23:
                    service.showAProvider();
                    break;
                case 24:
                    service.updateProvider();
                    break;
                case 25:
                    service.deleteProvider();
                    break;
                case 26:
                    service.showTheBestProviders();
                    break;
                case 0:
                    var=false;
                    break;
                default:
                    System.out.println("\u001B[31mInvalid choice. Please try again!\u001B[31m");
                    break;
            }
            if(choice != 0){
                service.waitForEnter();
                service.clearConsole();
            }
        }
    }

}