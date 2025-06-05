import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static final String FILE_NAME = "audit.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static AuditService instance;

    private AuditService() {}

    public static AuditService getInstance() {
        if (instance == null)
            instance = new AuditService();
        return instance;
    }

    public void log(String actionName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.printf("%s,%s%n", actionName, timestamp);
        }
        catch (IOException e) {
            System.out.println("Error at writing in audit file: " + e.getMessage());
        }
    }
}
