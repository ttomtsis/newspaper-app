package gr.aegean.icsd.newspaperapp.model.repository;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
}
