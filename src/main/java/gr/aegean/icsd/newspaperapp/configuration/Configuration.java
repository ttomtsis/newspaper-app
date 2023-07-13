package gr.aegean.icsd.newspaperapp.configuration;

import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.UserRepository;
import gr.aegean.icsd.newspaperapp.util.enums.UserType;
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
     * Pre-loads the database with Users <br>
     *
     * @return True
     */
    @Bean
    @Transactional
    public boolean createUsers() {

        if (repository.findByUsername("testCurator").isEmpty()) {
            repository.save(new User("testCurator", "password", UserType.CURATOR));
        }

        if (repository.findByUsername("testJournalist").isEmpty()) {
            repository.save(new User("testJournalist", "password", UserType.JOURNALIST));
        }

        return true;
    }


}
