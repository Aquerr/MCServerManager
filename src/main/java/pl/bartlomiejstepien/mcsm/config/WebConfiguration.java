package pl.bartlomiejstepien.mcsm.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer
{
//    @Bean
//    public CommonsRequestLoggingFilter logFilter()
//    {
//        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludeHeaders(false);
//        filter.setIncludePayload(true);
//        filter.setAfterMessageSuffix("");
//
//        return filter;
//    }

//    @Bean
//    public FilterRegistrationBean<CommonsRequestLoggingFilter> loggingFilter(CommonsRequestLoggingFilter commonsRequestLoggingFilter)
//    {
//        FilterRegistrationBean<CommonsRequestLoggingFilter> registrationBean = new FilterRegistrationBean<>(commonsRequestLoggingFilter);
//        registrationBean.setEnabled(true);
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
