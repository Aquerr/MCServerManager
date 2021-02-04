package pl.bartlomiejstepien.mcsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.bartlomiejstepien.mcsm.Routes;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final ApplicationContext applicationContext;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(final ApplicationContext applicationContext, @Qualifier("h2UserDetailsService") final UserDetailsService userDetailsService)
    {
        this.applicationContext = applicationContext;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .csrf().ignoringAntMatchers("/api/**")
            .and()
            .authorizeRequests()
            .antMatchers("/css/**", "/js/**").permitAll()
            .antMatchers(Routes.LOGIN, Routes.HOME, Routes.ROOT).authenticated()
            .antMatchers(HttpMethod.GET, "/api/modpacks/**").authenticated()
            .antMatchers(HttpMethod.POST, "/api/modpacks/**").authenticated()
            .antMatchers("/api/servers/**").authenticated()
            .antMatchers("/servers/**").authenticated()
            .and()
                .formLogin()
                .loginPage(Routes.LOGIN)
                .defaultSuccessUrl("/")
                .permitAll()
            .and()
                .logout()
                .logoutUrl(Routes.LOGOUT)
                .logoutSuccessUrl(Routes.HOME)
    //                    .invalidateHttpSession(true)
    //                    .deleteCookies("JSESSIONID")
    //                        .logoutUrl(Routes.LOGOUT)
                    .permitAll();
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
