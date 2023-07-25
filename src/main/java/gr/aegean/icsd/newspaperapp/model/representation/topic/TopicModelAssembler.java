package gr.aegean.icsd.newspaperapp.model.representation.topic;

import gr.aegean.icsd.newspaperapp.controller.TopicController;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.model.entity.Topic;
import gr.aegean.icsd.newspaperapp.security.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Support class used to create Representation
 * Models of the Topic Entity
 */
@Component
public class TopicModelAssembler  extends RepresentationModelAssemblerSupport<Topic, TopicModel> {


    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     */
    public TopicModelAssembler() {
        super(TopicController.class, TopicModel.class);
    }


    /**
     * Creates a single Representation Model from the provided
     * Topic Entity
     *
     * @param entity Provided Topic Entity
     *
     * @return Representation Model with links attached
     */
    @Override
    @NonNull
    public TopicModel toModel(@NonNull Topic entity) {

        TopicModel newModel = new TopicModel();

        BeanUtils.copyProperties(entity, newModel);

        newModel.setAuthorID(entity.getAuthor().getUsername());

        List<Integer> associatedStoriesList = new ArrayList<>();

        for (Story associatedStory : entity.getStories()) {
            associatedStoriesList.add(associatedStory.getId().intValue());
        }
        newModel.setStoriesList(associatedStoriesList);

        List<Integer> childrenTopicsList = new ArrayList<>();

        for (Topic childTopic : entity.getChildrenTopics()) {
            childrenTopicsList.add(childTopic.getId().intValue());
        }
        newModel.setTopicsList(childrenTopicsList);

        if (entity.getParentTopic() != null) {
            newModel.setParentTopicID(entity.getParentTopic().getId().intValue());
        }
        else {
            newModel.setParentTopicID(null);
        }

        newModel.add(linkTo(methodOn(TopicController.class)
                .showTopic(entity.getId())).withSelfRel());

        newModel.add(linkTo(methodOn(TopicController.class)
                .showAllTopicsByName(entity.getName(), 0, 10))
                .withRel("Topics with similar names"));

        newModel.add(linkTo(methodOn(TopicController.class)
                .showAllTopics(0, 10))
                .withRel("All Topics"));

        if (UserUtils.isCurator()) {

            newModel.add(linkTo(methodOn(TopicController.class)
                    .approveTopic(entity.getId())).withRel("Approve Topic"));

            newModel.add(linkTo(methodOn(TopicController.class)
                    .rejectTopic(entity.getId())).withRel("Reject Topic"));

        }

        if(UserUtils.isCurator() || UserUtils.isJournalist()) {

            newModel.add(linkTo(methodOn(TopicController.class)
                    .updateTopic(entity.getId(), new TopicModel()))
                    .withRel("Update Topic"));

            newModel.add(linkTo(methodOn(TopicController.class)
                    .createTopic(new TopicModel()))
                    .withRel("Create Topic"));

        }

        return newModel;
    }


    public PagedModel<TopicModel> createPagedModel(Page<Topic> topicPage) {

        PagedModel<TopicModel> pagedModel = createPagedModelFromPage(topicPage);

        pagedModel.add(linkTo(methodOn(TopicController.class)
                .showAllTopics
                        (topicPage.getNumber(), topicPage.getSize()))
                .withSelfRel());

        if (topicPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(TopicController.class)
                    .showAllTopics
                            (topicPage.getNumber() + 1, topicPage.getSize()))
                    .withRel("next"));
        }


        if (topicPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(TopicController.class)
                    .showAllTopics
                            (topicPage.getNumber() - 1, topicPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }


    public PagedModel<TopicModel> createPagedModelForSearchByName(Page<Topic> topicPage, String name) {

        PagedModel<TopicModel> pagedModel = createPagedModelFromPage(topicPage);

        pagedModel.add(linkTo(methodOn(TopicController.class)
                .showAllTopicsByName
                        (name, topicPage.getNumber(), topicPage.getSize()))
                .withSelfRel());

        if (topicPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(TopicController.class)
                    .showAllTopicsByName
                            (name, topicPage.getNumber() + 1, topicPage.getSize()))
                    .withRel("next"));
        }


        if (topicPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(TopicController.class)
                    .showAllTopicsByName
                            (name, topicPage.getNumber() - 1, topicPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }


    /**
     * Utility method that creates a PagedModel from an input Page
     *
     * @param topicPage Input Page
     *
     * @return PagedModel
     */
    private PagedModel<TopicModel> createPagedModelFromPage ( Page<Topic> topicPage ) {

        // Convert Topics inside the page to TopicModels
        List<TopicModel> topicModels = topicPage.getContent().stream().map(this::toModel).toList();

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(topicPage.getSize(), topicPage.getNumber(), topicPage.getTotalElements());

        return PagedModel.of(topicModels, pageMetadata);
    }


}
