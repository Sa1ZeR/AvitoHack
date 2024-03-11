package ru.avito.priceservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.avito.priceservice.PriceServiceApplication;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.util.Scanner;

@Configuration
public class DbInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            try (Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement()) {
                URI resource = URI.create(String.valueOf(PriceServiceApplication.class.getResource("/InitData.sql")));
                try (Scanner scanner = new Scanner(new File(resource), StandardCharsets.UTF_8)) {
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNext()) {
                        sb.append(scanner.nextLine());
                    }
                    statement.executeUpdate(sb.toString());
                }
            }
        };
    }
}
