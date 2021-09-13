package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import pl.bartlomiejstepien.mcsm.Routes;
import pl.bartlomiejstepien.mcsm.auth.McsmAccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .authorizeRequests()
                .antMatchers("/css/**", "icons/**", "/js/**", "/webjars/**", "/favicon.ico").permitAll()
                .antMatchers("/config/**", "/api/config/**").hasAuthority("ADMIN")
                .antMatchers("/h2-console/**").permitAll()
                .and().headers().frameOptions().sameOrigin()
            .and()
                .csrf().ignoringAntMatchers("/api/**", "/h2-console/**")
            .and()
            .formLogin()
                .loginPage(Routes.LOGIN)
                .defaultSuccessUrl(Routes.HOME)
                .permitAll()
            .and()
            .logout()
                .logoutUrl(Routes.LOGOUT)
                .logoutSuccessUrl(Routes.LOGIN)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().accessDeniedHandler(accessDeniedHandler());
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

    @Bean
    public AccessDeniedHandler accessDeniedHandler()
    {
        return new McsmAccessDeniedHandler();
    }
}
