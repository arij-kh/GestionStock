package com.sip.GS.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


import com.sip.GS.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserService userService;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Autowired
	 private DataSource dataSource;
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		 http.
	 		authorizeRequests()
	 		 
	 		.antMatchers("/login").permitAll() // accès pour tous users
	 		.antMatchers("/registration").permitAll()
	 		
	 		.antMatchers("/role/**").permitAll()
	 		//.antMatchers("/article/**").hasAuthority("USER").anyRequest()
	 		.antMatchers("/").hasAnyAuthority("SUPERADMIN")
	 		.antMatchers("/provider/**").hasAnyAuthority("ADMIN", "SUPERADMIN")
	 		.antMatchers("/article/**").hasAnyAuthority("USER", "SUPERADMIN").anyRequest()
	 		
	 		.authenticated().and().csrf().disable().formLogin()  // l'accès de fait via un formulaire
	 		.loginPage("/login").failureUrl("/login?error=true") // fixer la page login
	 		.defaultSuccessUrl("/") // page d'accueil après login avec succès
	 		.usernameParameter("email") // paramètres d'authentifications login et password
	 		.passwordParameter("password")
	 		 .and().logout()
	 		 .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // route de deconnexion ici /logut
	 		.logoutSuccessUrl("/login").and().exceptionHandling() // une fois deconnecté redirection vers login
	 		.accessDeniedPage("/403"); 
		
	}
	
	@Override
	 public void configure(WebSecurity web) throws Exception {
	        web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**","/vendor/**","/fonts/**").anyRequest(); 
	 }

}