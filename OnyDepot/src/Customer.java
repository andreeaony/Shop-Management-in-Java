import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Customer {
    private static int numberOfCustomers;
    protected int customerID;
    protected String customerName;
    protected String customerAddress;
    protected String customerPhone;
    protected String customerEmail;

    static
    {
        numberOfCustomers = 0;
    }
    {
        this.customerID = ++numberOfCustomers;
    }

    public Customer()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Customer Name: ");
        this.customerName = scanner.nextLine();
        System.out.println("Enter Customer Address: ");
        this.customerAddress = scanner.nextLine();
        System.out.println("Enter Customer Phone: ");
        this.customerPhone = scanner.next();
        System.out.println("Enter Customer Email: ");
        this.customerEmail = scanner.next();
    }
    public Customer(String customerName, String customerAddress, String customerPhone, String customerEmail) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }
    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
