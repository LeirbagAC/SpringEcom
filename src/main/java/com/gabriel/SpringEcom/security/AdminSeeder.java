package com.gabriel.SpringEcom.security;

import com.gabriel.SpringEcom.model.User;
import com.gabriel.SpringEcom.model.enums.Role;
import com.gabriel.SpringEcom.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeeder {

    @Bean
    public CommandLineRunner seedAdmin(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            boolean adminExists = userRepo.findByEmail("admin@ecommerce.com").isPresent();

            if (!adminExists) {
                User admin = new User();
                admin.setUsername("Super Admin");
                admin.setEmail("admin@ecommerce.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);

                userRepo.save(admin);
                System.out.println("Administrador padrão criado com sucesso!");
            }
        };
    }

}
