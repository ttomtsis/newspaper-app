package gr.aegean.icsd.newspaperapp.security;

import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsManager implements UserDetailsService, UserDetailsManager {

    private final UserRepository userRepository;

    public CustomUserDetailsManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(UserDetails user) {

        if (user instanceof User newUser) {
            userRepository.save(newUser);
        }
        else {
            throw new IllegalArgumentException("The given user is not a valid User Entity");
        }

    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Updating a user is not yet implemented");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Deleting a user is not yet implemented");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Updating a user's password is not yet implemented");
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> requestedUser = userRepository.findUserByUsername(username);

        if (requestedUser.isPresent()) {

            return requestedUser.get();

        }

        else {

            throw new UsernameNotFoundException("User with username: " + username + " was not found");

        }

    }
}
