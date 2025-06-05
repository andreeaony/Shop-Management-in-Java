import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;

public class Inventory {
    static List<PerishableProduct> perishableProducts = new ArrayList<>();
    static List<NonperishableProduct> nonPerishableProducts = new ArrayList<>();

    static PriorityQueue<PerishableProduct> soonToExpireProducts = new PriorityQueue<>(Comparator.comparing(PerishableProduct::getExpirationDate));
    static Map<Product, Integer> bestseller = new HashMap<>();

    public void loadFromDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM product");

        while (rs.next()) {
            int productId = rs.getInt("productId");
            String productType = rs.getString("productType");
            String productName = rs.getString("productName");
            String productCategory = rs.getString("productCategory");
            String productDescription = rs.getString("productDescription");
            double productPrice = rs.getDouble("productPrice");
            int productStock = rs.getInt("productStock");
            double productRating = rs.getDouble("productRating");
            java.sql.Date expirationDateSQL = rs.getDate("expirationDate");
            String storageInstructions = rs.getString("storageInstructions");

            if ("perishable".equalsIgnoreCase(productType)) {
                LocalDate expirationDate = null;
                if (expirationDateSQL != null) {
                    expirationDate = expirationDateSQL.toLocalDate();
                }

                PerishableProduct p = new PerishableProduct(productName, productCategory, productDescription, productPrice, productStock, expirationDate);
                perishableProducts.add(p);

                if (expirationDate != null && expirationDate.isBefore(LocalDate.now().plusDays(7))) {
                    soonToExpireProducts.add(p);
                }
            }
            else if ("nonperishable".equalsIgnoreCase(productType)) {
                NonperishableProduct np = new NonperishableProduct(productName, productCategory, productDescription,
                        productPrice, productStock, storageInstructions);
                nonPerishableProducts.add(np);
            }
        }

        for (PerishableProduct p : perishableProducts) {
            bestseller.put(p, p.getProductStock());
        }
        for (NonperishableProduct np : nonPerishableProducts) {
            bestseller.put(np, np.getProductStock());
        }
    }
}
