package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, String> {

    Optional<User> findUserByUsername(String username);
}
