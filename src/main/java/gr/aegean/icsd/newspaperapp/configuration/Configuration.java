package gr.aegean.icsd.newspaperapp.configuration;

import gr.aegean.icsd.newspaperapp.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Autowired
    private UserRepository repository;

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * Pre-loads the database with Users
     * @return True if users created successfully
     */
    @Bean
    @Transactional
    public boolean createUsers() {
        return true;
    }


}
