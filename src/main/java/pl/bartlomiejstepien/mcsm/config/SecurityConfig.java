package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import pl.bartlomiejstepien.mcsm.Routes;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
            .authorizeRequests((auths) -> auths
                    .anyRequest().authenticated()
                    .requestMatchers("/css/**", "icons/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                    .requestMatchers("/config/**", "/api/config/users/**", "/logs/**").hasAnyAuthority("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.POST, "/api/config/java").hasAnyAuthority("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.PUT, "/api/config/java").hasAnyAuthority("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.DELETE, "/api/config/java").hasAnyAuthority("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.GET, "/api/config/java").permitAll()
                    .requestMatchers("/users/**").hasAnyAuthority("ADMIN", "OWNER")
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()

            )
                .csrf(customizer -> customizer.ignoringRequestMatchers("/api/**", "/h2-console/**"))
                .formLogin(customizer -> customizer
                        .loginPage(Routes.LOGIN)
                        .defaultSuccessUrl(Routes.HOME)
                        .permitAll()
                )
                .logout(customizer -> customizer
                        .logoutUrl(Routes.LOGOUT)
                        .logoutSuccessUrl(Routes.LOGIN)
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());


        // Only for testing. This encoder should be changed in production environment.
//        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();

//        auth.inMemoryAuthentication()
//            .withUser(userBuilder.username("Nerdi").password("test123").roles("ADMIN"))
//            .withUser(userBuilder.username("Test").password("test").roles("ADMIN"));
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler()
    {
        return new McsmAccessDeniedHandler();
    }
}
