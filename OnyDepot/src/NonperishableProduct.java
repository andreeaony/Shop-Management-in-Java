import java.util.Date;
import java.util.Scanner;

public class NonperishableProduct extends Product {
    private String storageInstructions;

    public NonperishableProduct() {
        super();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the storage instructions here: ");
        this.storageInstructions = scanner.nextLine();
    }
    public NonperishableProduct(String productName, String productCategory, String productDescription, double productPrice, int productStock, String storageInstructions) {
        super(productName, productCategory, productDescription, productPrice, productStock);
        this.storageInstructions = storageInstructions;
    }

    public String getStorageInstructions() {
        return storageInstructions;
    }

    public void setStorageInstructions(String storageInstructions) {
        this.storageInstructions = storageInstructions;
    }
}
