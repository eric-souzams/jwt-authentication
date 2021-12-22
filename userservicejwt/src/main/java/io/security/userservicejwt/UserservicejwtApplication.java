package io.security.userservicejwt;

import io.security.userservicejwt.domain.Role;
import io.security.userservicejwt.domain.User;
import io.security.userservicejwt.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class UserservicejwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserservicejwtApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return  args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SEO"));

			userService.saveUser(new User(null, "Eric Magalhaes", "eric", "123", new ArrayList<>()));
			userService.saveUser(new User(null, "Will Smith", "will", "123", new ArrayList<>()));
			userService.saveUser(new User(null, "Toby Santana", "toby", "123", new ArrayList<>()));
			userService.saveUser(new User(null, "Matheus Soares", "teteu", "123", new ArrayList<>()));

			userService.addRoleToUser("eric", "ROLE_USER");
			userService.addRoleToUser("eric", "ROLE_MANAGER");
			userService.addRoleToUser("will", "ROLE_MANAGER");
			userService.addRoleToUser("toby", "ROLE_ADMIN");
			userService.addRoleToUser("teteu", "ROLE_SEO");
			userService.addRoleToUser("teteu", "ROLE_ADMIN");
			userService.addRoleToUser("teteu", "ROLE_USER");
		};
	}

}
