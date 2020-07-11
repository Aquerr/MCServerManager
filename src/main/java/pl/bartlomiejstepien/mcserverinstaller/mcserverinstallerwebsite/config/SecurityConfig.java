package pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import pl.bartlomiejstepien.mcserverinstaller.mcserverinstallerwebsite.Routes;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests()
                .antMatchers("/css/**", "/js/**").permitAll()
                .antMatchers(Routes.LOGIN, Routes.HOME, Routes.ROOT).authenticated()
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
        // Only for testing. This encoder should be changed in production environment.
        User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();

        auth.inMemoryAuthentication()
            .withUser(userBuilder.username("Nerdi").password("test123").roles("ADMIN"))
            .withUser(userBuilder.username("Test").password("test").roles("ADMIN"));
    }
}
