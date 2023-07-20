package gr.aegean.icsd.newspaperapp.model.service;

import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.model.entity.User;
import gr.aegean.icsd.newspaperapp.model.repository.StoryRepository;
import gr.aegean.icsd.newspaperapp.model.repository.TopicRepository;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Validated
public class StoryService {

    private final StoryRepository storyRepository;

    private final TopicRepository topicRepository;

    private final static Logger log = LoggerFactory.getLogger("Story Service");

    public StoryService(StoryRepository storyRepository, TopicRepository topicRepository) {
        this.storyRepository = storyRepository;
        this.topicRepository = topicRepository;
    }

    @PreAuthorize("hasAuthority('ROLE_JOURNALIST')")
    public Story createStory(@NotBlank String storyName, @NotBlank String storyContent, @NotNull List<Integer> topicIDs) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (topicIDs.isEmpty())  {

            Story newStory = new Story(storyName, new User(username), storyContent);

            return storyRepository.save(newStory);

        }
        else {

            Set<Topic> topicsList = createTopicsListFromIDs(topicIDs);
            Story newStory = new Story(storyName, new User(username), storyContent, topicsList);

            return storyRepository.save(newStory);

        }

    }

    private Set<Topic> createTopicsListFromIDs(List<Integer> topicIDs) {

        Set<Topic> topicsList = new HashSet<>();

        for (Integer id : topicIDs) {

            Optional<Topic> savedTopic = topicRepository.findById(Long.valueOf(id));

            if (savedTopic.isPresent() && savedTopic.get().getState().equals(TopicState.APPROVED)) {

                topicsList.add(savedTopic.get());

            }

        }

        return topicsList;

    }


}
