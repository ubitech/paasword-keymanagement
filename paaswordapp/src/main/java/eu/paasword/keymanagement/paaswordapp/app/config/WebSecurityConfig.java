/*
 *  Copyright 2016-2017 PaaSword Framework, http://www.paasword.eu/
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.paasword.keymanagement.paaswordapp.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 *
 * @author Panagiotis Gouvas (pgouvas@ubitech.eu)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    public WebSecurityConfig() {
        //Disable default settings
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //Disable session management
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/api/paaswordapp/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/paaswordapp/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/paaswordapp/**").permitAll()

                // Allow anonymous resource requests on the following URIs
                .antMatchers("/", "/login/**", "/login", "/register").permitAll()
                //Public access to static content
                .antMatchers("/build/**", "/images/**", "/webjars/**", "/js/**", "/fonts/**", "/css/**", "/favicon.ico", "/header", "/content", "/footer", "/component").permitAll()
                //Allow access only for GET methods
                .antMatchers(HttpMethod.GET, "/dashboard", "/user/**", "/application/**", "/model/**", "/activity/**", "/resource/**", "/documentation/**").permitAll()
                // All other request need to be authenticated
                .anyRequest().authenticated().and()
//                // custom JSON based authentication by POST of {"username":"<name>","password":"<password>"} which sets the token header upon authentication
//                .addFilterBefore(new StatelessLoginFilter("/api/v1/auth/login", tokenAuthenticationService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)
//                // custom Token based authentication based on the header previously given to the client
//                .addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .exceptionHandling().and()
                .anonymous().and()
                .servletApi().and()
                .headers().cacheControl();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService)
//                .passwordEncoder(new SHAPasswordEncoder());
//    }

}