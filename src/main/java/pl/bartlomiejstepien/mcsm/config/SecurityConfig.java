package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.jwt.AuthEntryPointJwt;
import pl.bartlomiejstepien.mcsm.auth.jwt.AuthTokenFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer
{
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "DELETE", "POST", "PUT");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.cors()
            .and()
                .csrf().ignoringAntMatchers("/api/**")
            .and()
                .authorizeRequests()
                .antMatchers("/css/**", "icons/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
            .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
//            .formLogin()
//                .loginPage(Routes.LOGIN)
//                .defaultSuccessUrl(Routes.HOME)
//                .permitAll()
//            .and()
            .logout()
                .logoutUrl(Routes.LOGOUT)
                .logoutSuccessUrl(Routes.LOGIN)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
//                .antMatchers("/api/platforms").permitAll()
//                .antMatchers("/api/modpacks/categories").permitAll()
//                .antMatchers("/api/modpacks/versions").permitAll()
            .anyRequest().authenticated()
            .and();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
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
}
