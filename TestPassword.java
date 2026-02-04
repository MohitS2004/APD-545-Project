import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Testing Admin!234:");
        String adminHash = "$2a$10$Fw1cQWmvMRGsiE9z1FMvxu1MpiKFFitvolSF/GpNZgbf28pQ5e0si";
        System.out.println("Matches: " + encoder.matches("Admin!234", adminHash));
        
        System.out.println("\nTesting Manager!234:");
        String managerHash = "$2a$10$kGouVp21HbQJseb3CidRubc4QZAlWTMwVzKhI1+w4n1vCtbmZh9rq";
        System.out.println("Matches: " + encoder.matches("Manager!234", managerHash));
        
        System.out.println("\nGenerating new hashes:");
        System.out.println("Admin!234: {bcrypt}" + encoder.encode("Admin!234"));
        System.out.println("Manager!234: {bcrypt}" + encoder.encode("Manager!234"));
    }
}
