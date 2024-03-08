package com.ibg.receipt.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.user.name:user}")
    private String securityUser;
    @Value("${security.user.password:CoreSystem@2018}")
    private String securityPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        String[] urls = new String[]{"/info", "/env", "/trace", "/dump", "/metrics",
            "/loggers", "/heapdump", "/refresh", "/auditevents", "/jolokia"};
        // ... and any other request needs to be authorized
        http.authorizeRequests().antMatchers(urls).authenticated();
        http.authorizeRequests().antMatchers("/**").permitAll();

        http.addFilterBefore(new ActuatorSecurityFilter(urls, securityUser, securityPassword),
            UsernamePasswordAuthenticationFilter.class);
        //http.httpBasic();
    }

}

class ActuatorSecurityFilter extends AbstractAuthenticationProcessingFilter {

    private String actuatorUser;
    private String actuatorPassword;

    public ActuatorSecurityFilter(String[] urls, String actuatorUser, String actuatorPassword) {
        super(urls[0]);
        AntPathRequestMatcher[] matchers = new AntPathRequestMatcher[urls.length * 2];
        int index = 0;
        for (int i = 0; i < urls.length; i++) {
            matchers[index++] = new AntPathRequestMatcher(urls[i]);
            matchers[index++] = new AntPathRequestMatcher(urls[i] + "/**");
        }
        super.setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(matchers));
        this.actuatorUser = actuatorUser;
        this.actuatorPassword = actuatorPassword;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        final String headerUser = request.getHeader("INSTANCE_ACTUATOR_USER");
        final String headerPass = request.getHeader("INSTANCE_ACTUATOR_PASS");

        if (actuatorUser.equals(headerUser) && actuatorPassword.equals(headerPass)) {
            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return new ArrayList<>();
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }

                @Override
                public void setAuthenticated(boolean b) throws IllegalArgumentException {
                }

                @Override
                public String getName() {
                    return null;
                }
            };
        } else {
            httpServletResponse.setStatus(401);
            httpServletResponse.getWriter().write("forbidden");
            httpServletResponse.getWriter().flush();

            return new Authentication() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return new ArrayList<>();
                }

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getDetails() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return null;
                }

                @Override
                public boolean isAuthenticated() {
                    return false;
                }

                @Override
                public void setAuthenticated(boolean b) throws IllegalArgumentException {
                }

                @Override
                public String getName() {
                    return null;
                }
            };
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(authResult);
        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        if (response.getStatus() == 401 && response.isCommitted()) {
            return;
        }
        chain.doFilter(request, response);
    }
}
