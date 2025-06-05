import java.util.Date;
import java.util.Scanner;

public class Shipping {
    protected String shippingCity;
    protected String shippingCountry;
    protected String shippingPostalCode;

    protected String shippingMethod;
    protected String trackingNumber;
    protected String statusOfOrder;
    protected Date estimatedDelivery;

    public Shipping() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the shipping city: ");
        shippingCity = scanner.nextLine();
        System.out.print("Enter the shipping country: ");
        shippingCountry = scanner.nextLine();
        System.out.print("Enter the shipping postalCode: ");
        shippingPostalCode = scanner.nextLine();
        System.out.print("Enter the shipping method: ");
        shippingMethod = scanner.nextLine();
        System.out.print("Enter the trackingNumber: ");
        trackingNumber = scanner.nextLine();
        System.out.print("Enter the statusOfOrder: ");
        statusOfOrder = scanner.nextLine();

        int year, month, day;
        System.out.print("Enter the estimated year of delivery: ");
        year= scanner.nextInt();
        System.out.print("Enter the estimated month of delivery: ");
        month= scanner.nextInt();
        System.out.print("Enter the estimated day of delivery: ");
        day= scanner.nextInt();
        estimatedDelivery = new Date(year-1900, month-1, day);

        this.statusOfOrder="in progress";
    }
    public Shipping(String shippingCity, String shippingCountry, String shippingPostalCode, String shippingMethod, String trackingNumber, int year, int month, int day) {
        this.shippingCity = shippingCity;
        this.shippingCountry = shippingCountry;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingMethod = shippingMethod;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = new Date(year, month, day);
        this.statusOfOrder="in progress";
    }

    public String getShippingCity() {
        return shippingCity;
    }
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    public String getShippingCountry() {
        return shippingCountry;
    }
    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }
    public String getShippingPostalCode() {
        return shippingPostalCode;
    }
    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }
    public String getShippingMethod() {
        return shippingMethod;
    }
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public String getStatusOfOrder() {
        return statusOfOrder;
    }
    public void setStatusOfOrder(String statusOfOrder) {
        this.statusOfOrder = statusOfOrder;
    }
    public Date getEstimatedDelivery() {
        return estimatedDelivery;
    }
    public void setEstimatedDelivery(int year, int month, int day) {
        estimatedDelivery = new Date(year, month, day);
    }
}
