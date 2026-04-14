package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DemoApplication.class);

		Properties properties = new Properties();
		// Manually setting the database configuration
		properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true");
		properties.setProperty("spring.datasource.username", "root");
		properties.setProperty("spring.datasource.password", "root"); // <-- Ensure this matches your MySQL password
		properties.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
		properties.setProperty("spring.jpa.hibernate.ddl-auto", "update");
		properties.setProperty("spring.jpa.show-sql", "true");

		app.setDefaultProperties(properties);
		app.run(args);
	}
}