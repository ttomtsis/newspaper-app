package gr.aegean.icsd.newspaperapp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private DataSource dataSource;

    private final String apiBaseMapping = "/api/v0";
    private final String storiesMapping = apiBaseMapping + "/stories";
    private final String commentsMapping = apiBaseMapping + "/comments";
    private final String topicsMapping = apiBaseMapping + "/topics";

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize

                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()

                        // Authentication endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

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
                        .requestMatchers(HttpMethod.GET, storiesMapping + "/**").permitAll()

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
                        .requestMatchers(HttpMethod.GET, topicsMapping + "/**").permitAll()

                        // Modify Topic
                        .requestMatchers(HttpMethod.PUT, topicsMapping).hasAnyRole("JOURNALIST", "CURATOR")

                        // Approve Topic
                        .requestMatchers(HttpMethod.PATCH, topicsMapping).hasRole("CURATOR")

                        // Reject Topic
                        .requestMatchers(HttpMethod.DELETE, topicsMapping).hasRole("CURATOR")

                        .anyRequest().denyAll()
                )

                // Implement in future commits
                .csrf().disable();

        return http.build();
    }

    @Bean
    UserDetailsManager users(DataSource dataSource) {

        UserDetails testCurator = User.builder()
                .username("testCurator")
                .password("testCurator")
                .roles("CURATOR")
                .disabled(false)
                .build();

        UserDetails testJournalist = User.builder()
                .username("testJournalist")
                .password("testJournalist")
                .roles("JOURNALIST")
                .disabled(false)
                .build();

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        users.createUser(testCurator);
        users.createUser(testJournalist);

        return users;

    }

}
