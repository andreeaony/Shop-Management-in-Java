import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Provider {
    String providerName;
    String providerAddress;
    String providerPhone;
    String providerEmail;
    int providerId;
    ArrayList<Product> availableProducts;
    static int numberOfProviders;
    static {
        numberOfProviders = 0;
    }
    {
        this.providerId = ++numberOfProviders;
    }

    public Provider() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Provider Name: ");
        this.providerName = scanner.nextLine();
        System.out.print("Enter Provider Address: ");
        this.providerAddress = scanner.nextLine();
        System.out.print("Enter Provider Phone: ");
        this.providerPhone = scanner.nextLine();
        System.out.print("Enter Provider Email: ");
        this.providerEmail = scanner.nextLine();
        System.out.print("How many products does the provider have? ");
        int numberOfProducts = scanner.nextInt();
        this.availableProducts = new ArrayList<Product>();
        Service.clearConsole();
        for (int i = 0; i < numberOfProducts; i++) {
            System.out.println("\n\u001B[36mProduct number " + (i + 1) + "\u001B[0m");
            System.out.print("Is the product perishable? (1 for yes/2 for no): ");
            int isPerishable = scanner.nextInt();
            if (isPerishable == 1) {
                PerishableProduct product = new PerishableProduct();
                availableProducts.add(product);
                Inventory.perishableProducts.add(product);
            } else {
                NonperishableProduct product = new NonperishableProduct();
                availableProducts.add(product);
                Inventory.nonPerishableProducts.add(product);
            }
        }
    }
    public Provider(String providerName, Integer providerId)
    {
        this.providerName=providerName;
        this.providerId=providerId;
    }
    public Provider(String providerName, String providerAddress, String providerPhone, String providerEmail, ArrayList<Product> availableProducts) {
        this.providerName = providerName;
        this.providerAddress = providerAddress;
        this.providerPhone = providerPhone;
        this.providerEmail = providerEmail;
        this.availableProducts = availableProducts;
    }

    public String getProviderName() {
        return providerName;
    }
    public String getProviderAddress() {
        return providerAddress;
    }
    public String getProviderPhone() {
        return providerPhone;
    }
    public String getProviderEmail() {
        return providerEmail;
    }
    public int getProviderId() {
        return providerId;
    }
    public ArrayList<Product> getAvailableProducts(){
        return availableProducts;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
    public void setProviderAddress(String providerAddress) {
        this.providerAddress = providerAddress;
    }
    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }
    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }
    public void setProviderId(int providerId) {
        this.providerId=providerId;
    }
}
