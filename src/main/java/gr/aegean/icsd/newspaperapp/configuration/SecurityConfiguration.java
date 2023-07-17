package gr.aegean.icsd.newspaperapp.configuration;

import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static jakarta.servlet.DispatcherType.*;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private DataSource dataSource;

    private final String apiBaseMapping = "/api/v0";
    private final String storiesMapping = apiBaseMapping + "/stories/**";
    private final String commentsMapping = apiBaseMapping + "/comments/**";
    private final String topicsMapping = apiBaseMapping + "/topics/**";

    /**
     * Configures the security filter chain to be used by Spring Security to secure the endpoints of the application.
     * Authentication and authorization rules are set up here.
     *
     * @param http HttpSecurity object used to configure the security filter chain.
     * @return a SecurityFilterChain object representing the configured security filter chain.
     */

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {

        http
//                .formLogin((formLogin) ->
//                        formLogin
//                                .loginPage("/")
//                                //.failureUrl("/authentication/login?failed")
//                                //.loginProcessingUrl("/authentication/login/process")
//                )

                .authorizeHttpRequests((authorize) -> authorize

                        .dispatcherTypeMatchers(FORWARD, ERROR, INCLUDE).permitAll()

                        // Authentication endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/").permitAll()

                        // ### STORY ENDPOINTS ### //

                        // Create Story
                        .requestMatchers(HttpMethod.POST, storiesMapping).hasRole("JOURNALIST")

                        // Modify Story
                        .requestMatchers(HttpMethod.PUT, storiesMapping).hasAnyRole("CURATOR", "JOURNALIST")

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

                .csrf(AbstractHttpConfigurer::disable)

//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )

                .oauth2Login(Customizer.withDefaults())

                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource) {

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!users.userExists("testCurator")) {

            String username = "testCurator";
            String password = passwordEncoder.encode("testCurator");
            UserType role = UserType.CURATOR;

            UserDetails testCurator = new gr.aegean.icsd.newspaperapp.model.entity.User(username, password, role);
            users.createUser(testCurator);
        }

        if (!users.userExists("testJournalist")) {

            String username = "testJournalist";
            String password = passwordEncoder.encode("testJournalist");
            UserType role = UserType.JOURNALIST;

            UserDetails testJournalist = new gr.aegean.icsd.newspaperapp.model.entity.User(username, password, role);
            users.createUser(testJournalist);
        }


        return users;

    }

    @Bean
    PasswordEncoder encoder() {

        DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

        return delegatingPasswordEncoder;

    }

}
