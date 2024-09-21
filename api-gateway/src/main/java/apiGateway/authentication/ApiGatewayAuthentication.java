package apiGateway.authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;

import apiGateway.authentication.dto.UserDto;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {

	@Bean
	public MapReactiveUserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {
		List<UserDetails> users = new ArrayList<>();
		List<UserDto> usersFromDatabase;
		
		ResponseEntity<UserDto[]> response = 
				new RestTemplate().getForEntity("http://localhost:8770/user-service/users", UserDto[].class);
		
		usersFromDatabase = Arrays.asList(response.getBody());
		
		for(UserDto ud: usersFromDatabase) {
			users.add(User.withUsername(ud
					.getEmail())
					.password(encoder.encode(ud.getPassword()))
					.roles(ud.getRole())
					.build());
		}
		
	    SecurityContextHolder.clearContext();
	    SecurityContextHolder.getContext();
		
		return new MapReactiveUserDetailsService(users);
		
	}
	
	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception{
		http.csrf().disable().authorizeExchange()
		.pathMatchers("/currency-exchange/**").permitAll()
		.pathMatchers("/crypto-exchange/**").permitAll()
		.pathMatchers("/currency-conversion/**").hasRole("USER")
		.pathMatchers("/crypto-conversion").hasRole("USER")
		.pathMatchers("/user-service/users").hasRole("ADMIN")
		.pathMatchers("/user-service/users/removeUser/{email}").hasRole("OWNER")
		.pathMatchers("/user-service/users/addUser").hasRole("ADMIN")
		.pathMatchers("/user-service/users/addAdminUser").hasRole("OWNER")
		.pathMatchers("/user-service/users/updateUser/{email}").hasRole("ADMIN")
		.pathMatchers("/user-service/users/update/{email}").hasRole("OWNER")
		.pathMatchers("/bank-account").hasRole("USER")
		.pathMatchers("/bank-account/editAccount/{email}").hasRole("ADMIN")
		.pathMatchers("/bank-account/addAccount").hasRole("ADMIN")
		.pathMatchers("/crypto-wallet").hasRole("USER")
		.pathMatchers("/crypto-wallet/editWallet/{email}").hasRole("ADMIN")
		.pathMatchers("/trade-service/from/{from}/to/{to}/user/{email}/quantity/{quantity}").hasRole("USER")
		.and().httpBasic();
		
		return http.build();
	}
}
