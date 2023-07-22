package gr.aegean.icsd.newspaperapp.configuration;

import gr.aegean.icsd.newspaperapp.model.repository.UserRepository;
import gr.aegean.icsd.newspaperapp.security.CustomUserDetailsManager;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import java.util.Arrays;

import static jakarta.servlet.DispatcherType.*;
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final String apiBaseMapping = "/api/v0";
    private final String storiesMapping = apiBaseMapping + "/stories/**";
    private final String commentsMapping = apiBaseMapping + "/comments/**";
    private final String topicsMapping = apiBaseMapping + "/topics/**";


    @Value("${users.testCuratorPassword}")
    private String testCuratorPassword;

    @Value("${users.testJournalistPassword}")
    private String testJournalistPassword;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Configures the security filter chain to be used by Spring Security to secure the endpoints of the application.
     * Authentication and authorization rules are set up here.
     *
     * @param http HttpSecurity object used to configure the security filter chain.
     * @return a SecurityFilterChain object representing the configured security filter chain.
     */
    @Bean
    SecurityFilterChain createFilterChain(HttpSecurity http) throws Exception {

        http

                .authorizeHttpRequests((authorize) -> authorize

                        .dispatcherTypeMatchers(FORWARD, ERROR, INCLUDE).permitAll()

                        // Authentication endpoints
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/").permitAll()

                        // ### STORY ENDPOINTS ### //

                        // Create Story
                        .requestMatchers(HttpMethod.POST, storiesMapping).hasRole("JOURNALIST")

                        // Modify Story
                        .requestMatchers(HttpMethod.PUT, storiesMapping).hasRole("JOURNALIST")

                        // Submit, Approve, Reject, Publish Story
                        .requestMatchers(HttpMethod.PATCH, storiesMapping).hasAnyRole("CURATOR", "JOURNALIST")

                        // Delete Story
                        .requestMatchers(HttpMethod.DELETE, storiesMapping).hasAnyRole("CURATOR", "JOURNALIST")

                        // Search Story, Show all Stories, Show all comments for a Story
                        .requestMatchers(HttpMethod.GET, storiesMapping).permitAll()

                        // ### COMMENT ENDPOINTS ### //

                        // Create Comment
                        .requestMatchers(HttpMethod.POST, commentsMapping).permitAll()

                        // Modify Comment
                        .requestMatchers(HttpMethod.PUT, commentsMapping).hasRole("CURATOR")

                        // Approve Comment
                        .requestMatchers(HttpMethod.PATCH, commentsMapping).hasRole("CURATOR")

                        // Reject Comment
                        .requestMatchers(HttpMethod.DELETE, commentsMapping).hasRole("CURATOR")

                        // ### TOPIC ENDPOINTS ### //

                        // Create Topic
                        .requestMatchers(HttpMethod.POST, topicsMapping).hasAnyRole("JOURNALIST", "CURATOR")

                        // Search Topic, Show Topic, Show all Topics, Show a Topic's Stories
                        .requestMatchers(HttpMethod.GET, topicsMapping).permitAll()

                        // Modify Topic
                        .requestMatchers(HttpMethod.PUT, topicsMapping).hasAnyRole("JOURNALIST", "CURATOR")

                        // Approve Topic
                        .requestMatchers(HttpMethod.PATCH, topicsMapping).hasRole("CURATOR")

                        // Reject Topic
                        .requestMatchers(HttpMethod.DELETE, topicsMapping).hasRole("CURATOR")

                        .anyRequest().denyAll()

                )

                .headers(headers -> headers

                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)

                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                        )

                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'none'")
                        )

                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
                        )

                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(true)
                                .maxAgeInSeconds(31536000)
                        )

                )

                .requiresChannel(channel -> channel
                        .anyRequest().requiresSecure()
                )

                .sessionManagement((session) -> session

                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                )

                .csrf(AbstractHttpConfigurer::disable)

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> Customizer.withDefaults().customize(jwt))
                )

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    /**
     * Configures a DaoAuthenticationProvider to use in conjunction with Basic Authentication <br>
     *
     * The Provider preloads the database with two users, testCurator and testJournalist and uses
     * the {@link CustomUserDetailsManager}
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    DaoAuthenticationProvider createAuthenticationProvider() {

        CustomUserDetailsManager users = new CustomUserDetailsManager(userRepository);

        DelegatingPasswordEncoder passwordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        passwordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

        if (!users.userExists("testJournalist")) {

            String username = "testJournalist";
            String password = passwordEncoder.encode(testJournalistPassword);
            UserType role = UserType.JOURNALIST;

            UserDetails testJournalist = new gr.aegean.icsd.newspaperapp.model.entity.User(username, password, role);
            users.createUser(testJournalist);
        }

        if (!users.userExists("testCurator")) {

            String username = "testCurator";
            String password = passwordEncoder.encode(testCuratorPassword);
            UserType role = UserType.CURATOR;

            UserDetails testJournalist = new gr.aegean.icsd.newspaperapp.model.entity.User(username, password, role);
            users.createUser(testJournalist);
        }

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(users);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return authenticationProvider;

    }


    /**
     * Creates and configures a JWT Authentication converter to use in conjunction with OAuth2 authentication <br>
     *
     * The Converter matches scopes to roles, i.e. SCOPE_CURATOR is converted into ROLE_CURATOR
     *
     * @return Configured {@link JwtAuthenticationConverter}
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;

    }

    /**
     * Create and configure the HttpFirewall to allow only the subset of HTTP Methods used by the server
     *
     * @return Configured {@link StrictHttpFirewall}
     */
    @Bean
    public StrictHttpFirewall httpFirewall() {

        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowedHttpMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));

        return firewall;
    }
}
