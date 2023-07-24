package gr.aegean.icsd.newspaperapp.model.representation.comment;

import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

/**
 * Representation model of the Comment entity <br>
 */
public class CommentModel extends RepresentationModel<CommentModel> {


    /**
     * Primary Key of the Comment Entity
     */
    private Long id;

    /**
     * Date the Comment was created, assigned by the server
     * before persisting the entity in the database. <br>
     * Date will be in the format E, dd/MMMM/yyyy, HH:mm:ss
\     */
    private Date creationDate;

    /**
     * State of the Comment, valid states are declared in
     * {@link CommentState}
     * @see CommentState
     */
    private CommentState state;

    /**
     * Content of the Comment <br>
     * Content cannot be null, blank or exceed
     */
    private String content;

    /**
     * Story that the Comment belongs to <br>
     * Many Comments belong to One Story
     */
    private Integer storyID;

    /**
     * Author of the Comment. <br>
     * Many Comments can have the same Author <br>
     * Author may be null
     */
    private String authorID;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CommentState getState() {
        return state;
    }

    public void setState(CommentState state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStoryID() {
        return storyID;
    }

    public void setStoryID(Integer storyID) {
        this.storyID = storyID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
