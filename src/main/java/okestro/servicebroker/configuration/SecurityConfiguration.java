/*
 * Developed by bhhan@okestro.com on 2021-01-25
 * Last modified 2021-01-21 11:15:53
 */

package okestro.servicebroker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	final String user = "okestro";
	final String password = "okestro2018!";

	/**
	 * security to application endpoints
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/v2/**").hasRole("ADMIN")
				.and()
				.httpBasic();
	}

	/**
	 *
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		return new InMemoryUserDetailsManager(adminUser());
	}

	private UserDetails adminUser() {
		return User.withUsername(user)
				.password("{noop}" + password)
				.roles("ADMIN")
				.build();
	}
}
