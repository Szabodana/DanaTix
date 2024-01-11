package com.project.danatix;

import com.project.danatix.models.*;
import com.project.danatix.repositories.ProductRepository;
import com.project.danatix.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@SpringBootApplication
public class danatixBackendApplication implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public danatixBackendApplication(UserRepository userRepository, PasswordEncoder passwordEncoder, ProductRepository productRepository) {

        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(danatixBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            User user = new User("John", "john@test.com", passwordEncoder.encode("password"));
            user.setRole(Role.ROLE_USER);
            user.setEmailVerified(true);

            SecureRandom secureRandom = new SecureRandom();
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String randomToken = new String(Base64.getEncoder().encode(bytes));

            user.setEmailVerificationToken("random-token");
            user.setEmailTokenExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24));
            user.setEmailVerified(true);
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            System.out.println("User already exists");
        }

        if (productRepository.findProductById(1).isEmpty()) {
            Product product = new Product("One day test", 150, 24, "This is test", ProductType.ticket);
            productRepository.save(product);
        }
    }
}