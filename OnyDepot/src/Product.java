import java.util.Scanner;

public class Product {
    private static int numberOfProducts;
    protected int productId;
    protected String productName;
    protected String productCategory;
    protected String productDescription;
    protected double productPrice;
    protected int productStock;
    protected double productRating;

    static
    {
        numberOfProducts=0;
    }
    {
        this.productId=++numberOfProducts;
    }

    public Product() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Product name: ");
        this.productName = scanner.nextLine();
        System.out.print("Product category: ");
        this.productCategory = scanner.nextLine();
        System.out.print("Product description: ");
        this.productDescription = scanner.nextLine();
        System.out.print("Product price: ");
        this.productPrice = scanner.nextDouble();
        System.out.print("Product stock: ");
        this.productStock = scanner.nextInt();
        this.productRating = 0; //when adding a new product, first it has no rating
    }
    public Product(String productName, String productCategory, String productDescription, double productPrice, int productStock) {
        this.productName = productName;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productStock = productStock;
    }
    public Product(Product product) {
        this.productName = product.productName;
        this.productCategory = product.productCategory;
        this.productDescription = product.productDescription;
        this.productPrice = product.productPrice;
        this.productStock = product.productStock;
        this.productRating = product.productRating;
    }
    public Product(Integer productId, String productName, Double productPrice)
    {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productName;
    }

    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public String getProductCategory() {
        return productCategory;
    }
    public String getProductDescription() {
        return productDescription;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public int getProductStock() {
        return productStock;
    }
    public double getProductRating() {
        return productRating;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }
    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }
    public void setProductId(int productId) {
        this.productId=productId;
    }
}
