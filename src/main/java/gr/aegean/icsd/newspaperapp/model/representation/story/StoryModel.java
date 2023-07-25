package gr.aegean.icsd.newspaperapp.model.representation.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

/**
 * Representation Model of the Story Entity
 */
public class StoryModel extends RepresentationModel<StoryModel> {


    private Long id;

    private Date creationDate;

    private StoryState state;

    private String name;

    private String rejectionReason;

    private String content;

    private List<Integer> commentsList;

    private String authorID;

    @JsonProperty("topicsList")
    private List<Integer> topicsList;



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

    public StoryState getState() {
        return state;
    }

    public void setState(StoryState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Integer> commentsList) {
        this.commentsList = commentsList;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public List<Integer> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<Integer> topicsList) {
        this.topicsList = topicsList;
    }
}
