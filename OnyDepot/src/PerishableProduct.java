import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class PerishableProduct extends Product {
    private LocalDate expirationDate;

    public PerishableProduct() {
        super();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the year of expiration date: ");
        int year = scanner.nextInt();
        System.out.print("Enter the month of expiration date: ");
        int month = scanner.nextInt();
        System.out.print("Enter the day of expiration date: ");
        int day = scanner.nextInt();
        this.expirationDate = LocalDate.of(year, month, day);
    }

    public PerishableProduct(String productName, String productCategory, String productDescription, double productPrice, int productStock, LocalDate expirationDate) {
        super(productName, productCategory, productDescription, productPrice, productStock);
        this.expirationDate = expirationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }
}
