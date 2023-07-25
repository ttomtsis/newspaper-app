package gr.aegean.icsd.newspaperapp.model.representation.comment;

import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

/**
 * Representation model of the Comment entity <br>
 */
public class CommentModel extends RepresentationModel<CommentModel> {


    private Long id;

    private Date creationDate;

    private CommentState state;

    private String content;

    private Integer storyID;

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
