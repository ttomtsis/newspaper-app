package gr.aegean.icsd.newspaperapp.model.representation.comment;

import gr.aegean.icsd.newspaperapp.controller.CommentController;
import gr.aegean.icsd.newspaperapp.model.entity.Comment;
import gr.aegean.icsd.newspaperapp.security.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CommentModelAssembler extends RepresentationModelAssemblerSupport<Comment, CommentModel> {


    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     */
    public CommentModelAssembler() {
        super(CommentController.class, CommentModel.class);
    }

    @Override
    @NonNull
    public CommentModel toModel(@NonNull Comment entity) {

        CommentModel newModel = new CommentModel();

        BeanUtils.copyProperties(entity, newModel);

        boolean commentHasAuthor = entity.getAuthor().isPresent();
        if (commentHasAuthor) { newModel.setAuthorID(entity.getAuthor().get().getUsername()); }

        Long parentStoryID = entity.getStory().getId();
        newModel.setStoryID(parentStoryID.intValue());

        try {
            newModel.add(linkTo(methodOn(CommentController.class)
                    .showAllCommentsForAStory(parentStoryID, 0, 10)).withSelfRel());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (UserUtils.isCurator()) {

            newModel.add(linkTo(methodOn(CommentController.class)
                    .updateComment(entity.getId(), new CommentModel())).withRel("Update Comment"));

            newModel.add(linkTo(methodOn(CommentController.class)
                    .approveComment(entity.getId())).withRel("Approve Comment"));

            newModel.add(linkTo(methodOn(CommentController.class)
                    .rejectComment(entity.getId())).withRel("Reject Comment"));

        }


        return newModel;
    }

    public PagedModel<CommentModel> createPagedModel(Page<Comment> commentPage, long parentStoryID) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        PagedModel<CommentModel> pagedModel = createPagedModelFromPage(commentPage);

        pagedModel.add(linkTo(methodOn(CommentController.class)
                .showAllCommentsForAStory
                        (parentStoryID, commentPage.getNumber(), commentPage.getSize()))
                .withSelfRel()
        );

        if (commentPage.hasNext()) {
            pagedModel.add(linkTo(methodOn(CommentController.class)
                    .showAllCommentsForAStory
                            (parentStoryID, commentPage.getNumber() + 1, commentPage.getSize()))
                    .withRel("next")
            );
        }

        if (commentPage.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(CommentController.class)
                    .showAllCommentsForAStory
                            (parentStoryID, commentPage.getNumber() - 1, commentPage.getSize()))
                    .withRel("previous")
            );
        }

        return pagedModel;
    }


    public PagedModel<CommentModel> GenerifiedCreatePagedModel(Page<Comment> commentPage,
                                                               Method targetMethod,
                                                               Object ... methodArgsCurrentPage)
            throws InvocationTargetException, IllegalAccessException {

        PagedModel<CommentModel> pagedModel = createPagedModelFromPage(commentPage);

        pagedModel.add(linkTo(targetMethod
                .invoke(methodOn
                        (CommentController.class), 0, commentPage.getNumber(), commentPage.getSize()))
                .withSelfRel()
        );


        if (commentPage.hasNext()) {
            pagedModel.add(linkTo(targetMethod
                            .invoke(methodOn
                                    (CommentController.class), 0, commentPage.getNumber() + 1, commentPage.getSize()))
                            .withSelfRel()
            );
        }

        if (commentPage.hasPrevious()) {
            pagedModel.add(linkTo(targetMethod
                            .invoke(methodOn
                                    (CommentController.class), 0, commentPage.getNumber() - 1, commentPage.getSize()))
                            .withSelfRel()
            );
        }

        return pagedModel;
    }


    private PagedModel<CommentModel> createPagedModelFromPage ( Page<Comment> commentPage ) {

        // Convert Comments inside the page to CommentModels
        List<CommentModel> commentModels = commentPage.getContent().stream().map(this::toModel).toList();

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(commentPage.getSize(), commentPage.getNumber(), commentPage.getTotalElements());

        return PagedModel.of(commentModels, pageMetadata);
    }
}
