package gr.aegean.icsd.newspaperapp.model.representation.story;

import gr.aegean.icsd.newspaperapp.controller.StoryController;
import gr.aegean.icsd.newspaperapp.model.entity.Story;
import gr.aegean.icsd.newspaperapp.security.UserUtils;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StoryModelAssembler  extends RepresentationModelAssemblerSupport<Story, StoryModel> {


    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     */
    public StoryModelAssembler() {
        super(StoryController.class, StoryModel.class);
    }


    @Override
    @NonNull
    public StoryModel toModel(@NonNull Story entity) {

        StoryModel newModel = new StoryModel();

        BeanUtils.copyProperties(entity, newModel);

        newModel.setAuthorID(entity.getAuthor().getUsername());

        newModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByName
                        (entity.getName(), 0, 10))
                .withSelfRel());

        newModel.add(linkTo(methodOn(StoryController.class)
                .showAllStories
                        (0, 10))
                .withRel("Show all Stories"));

        if (UserUtils.isCurator()) {

            newModel.add(linkTo(methodOn(StoryController.class)
                    .approveStory
                            (entity.getId(), StoryState.APPROVED))
                    .withRel("Approve Story"));

            newModel.add(linkTo(methodOn(StoryController.class)
                    .rejectStory
                            (entity.getId(), StoryState.CREATED, ""))
                    .withRel("Reject Story"));

            newModel.add(linkTo(methodOn(StoryController.class)
                    .publishStory
                            (entity.getId(), StoryState.PUBLISHED))
                    .withRel("Publish Story"));

        }
        else if (UserUtils.isJournalist()) {

            newModel.add(linkTo(methodOn(StoryController.class)
                    .updateStory
                            (entity.getId(), new StoryModel()))
                    .withRel("Update Story"));

            newModel.add(linkTo(methodOn(StoryController.class)
                    .createStory
                            (new StoryModel()))
                    .withRel("Create Story"));

        }


        return newModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowAllStories(Page<Story> storyPage) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStories
                        (storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStories
                            (storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStories
                            (storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowAllStoriesByName(Page<Story> storyPage, String name) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByName
                        (name, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByName
                            (name, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByName
                            (name, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowAllStoriesByContent(Page<Story> storyPage, String content) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByContent
                        (content, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByContent
                            (content, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByContent
                            (content, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowAllStoriesByNameAndContent(Page<Story> storyPage, String name, String content) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByNameAndContent
                        (name, content, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByNameAndContent
                            (name, content, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByNameAndContent
                            (name, content, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowAllStoriesByDate(Page<Story> storyPage, Date minDate, Date maxDate) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByDate
                        (minDate, maxDate, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByDate
                            (minDate, maxDate, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByDate
                            (minDate, maxDate, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;

    }



    public PagedModel<StoryModel> createPagedModelForShowAllStoriesByState(Page<Story> storyPage, StoryState state) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showAllStoriesByState
                        (state, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByState
                            (state, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showAllStoriesByState
                            (state, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    public PagedModel<StoryModel> createPagedModelForShowATopicsStories(Page<Story> storyPage, long topicId) {

        PagedModel<StoryModel> pagedModel = createPagedModelFromPage(storyPage);

        pagedModel.add(linkTo(methodOn(StoryController.class)
                .showATopicsStories
                        (topicId, storyPage.getNumber(), storyPage.getSize()))
                .withSelfRel());

        if (storyPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showATopicsStories
                            (topicId, storyPage.getNumber() + 1, storyPage.getSize()))
                    .withRel("next"));
        }

        if (storyPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(StoryController.class)
                    .showATopicsStories
                            (topicId, storyPage.getNumber() - 1, storyPage.getSize()))
                    .withRel("previous"));
        }

        return pagedModel;
    }



    private PagedModel<StoryModel> createPagedModelFromPage (Page<Story> storyPage ) {

        // Convert Comments inside the page to CommentModels
        List<StoryModel> commentModels = storyPage.getContent().stream().map(this::toModel).toList();

        PagedModel.PageMetadata pageMetadata = new PagedModel
                .PageMetadata
                    (storyPage.getSize(), storyPage.getNumber(), storyPage.getTotalElements());

        return PagedModel.of(commentModels, pageMetadata);
    }



}
