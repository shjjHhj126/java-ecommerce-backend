package com.sherry.ecom;

import com.sherry.ecom.auth.AuthenticationService;
import com.sherry.ecom.auth.RegisterRequest;
import com.sherry.ecom.category.Category;
import com.sherry.ecom.category.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sherry.ecom.user.Role.ADMIN;
import static com.sherry.ecom.user.Role.MANAGER;


@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(
//			AuthenticationService service,
//			CategoryService categoryService
//	) {
//		return args -> {
//			var admin = RegisterRequest.builder()
//					.firstName("Admin1")
//					.lastName("Admin1")
//					.email("admin1@gmail.com")
//					.password("password")
//					.role(ADMIN)
//					.build();
//			System.out.println("Admin1 token: " + service.register(admin).getAccessToken());
//
//			var admin2 = RegisterRequest.builder()
//					.firstName("Admin2")
//					.lastName("Admin2")
//					.email("admin2@gmail.com")
//					.password("password")
//					.role(ADMIN)
//					.build();
//			System.out.println("Admin2 token: " + service.register(admin2).getAccessToken());
//
//			var manager = RegisterRequest.builder()
//					.firstName("Admin3")
//					.lastName("Admin3")
//					.email("manager@gmail.com")
//					.password("password")
//					.role(MANAGER)
//					.build();
//			System.out.println("Manager token: " + service.register(manager).getAccessToken());
//
//			// create default categories
//			createDefaultCategories(categoryService);
//
//		};
//	}

	private void createDefaultCategories(CategoryService categoryService) {

		List<Category> topCategories =  new ArrayList<>(List.of(
				Category.builder()
						.name("women")
						.level(1)
						.build(),
				Category.builder()
						.name("men")
						.level(1)
						.build(),
				Category.builder()
						.name("kids")
						.level(1)
						.build()
		));

		topCategories = topCategories.stream()
				.map(categoryService::create)
				.toList();

		List<Category> middleCategories =  new ArrayList<>(List.of(
				Category.builder()
						.name("shirts")
						.parent(topCategories.get(0))//women
						.level(2)
						.build(),
				Category.builder()
						.name("dresses")
						.parent(topCategories.get(0))//women
						.level(2)
						.build(),
				Category.builder()
						.name("shirts")
						.parent(topCategories.get(1))//men
						.level(2)
						.build(),
				Category.builder()
						.name("pants")
						.parent(topCategories.get(1))//men
						.level(2)
						.build(),
				Category.builder()
						.name("shirts")
						.parent(topCategories.get(2))//kid
						.level(2)
						.build(),
				Category.builder()
						.name("pants")
						.parent(topCategories.get(2))//kid
						.level(2)
						.build()
		));

		middleCategories.forEach(categoryService::create);
	}
}
