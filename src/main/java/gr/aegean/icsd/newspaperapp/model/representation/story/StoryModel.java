package gr.aegean.icsd.newspaperapp.model.representation.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class StoryModel extends RepresentationModel<StoryModel> {

    /**
     * Primary Key of the Story Entity
     */
    private Long id;

    /**
     * Date the Story was created, assigned by the server
     * before persisting the entity in the database. <br>
     *
     * Date will be in the format E, dd/MMMM/yyyy, HH:mm:ss
     *
     */
    private Date creationDate;

    /**
     * State of the Story, valid states are declared in
     * {@link StoryState}
     *
     * @see StoryState
     */
    private StoryState state;

    /**
     * The name of the Story <br>
     *
     */
    @JsonProperty("name")
    private String name;

    /**
     * Reason that a Story was rejected <br>
     *
     * This field is not null IF AND ONLY IF a Story
     * has been rejected.
     */
    private String rejectionReason;

    /**
     * The content of the Story <br>
     *
     */
    @JsonProperty("content")
    private String content;

    /**
     * Comments associated with the Story. <br>
     *
     * One Story may have plenty of Comments <br>
     * Obviously a Comment can only belong to one Story
     */
    private List<Integer> commentsList;

    /**
     * Author of the Story. <br>
     *
     * Many Stories can have the same Author <br>
     * Only one Author per Story is allowed
     */
    private String authorID;

    /**
     * Topics that this Story belongs to. <br>
     *
     * A Story can belong in many Topics at once <br>
     * Many Topics can be associated with the same Story <br>
     */
    @JsonProperty("topicsList")
    private List<Integer> topicsList;


    public Long getId() {
        return this.id;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public StoryState getState() {
        return this.state;
    }

    public String getName() {
        return this.name;
    }

    public String getContent() {
        return this.content;
    }

    public String getRejectionReason() {
        return this.rejectionReason;
    }

    public String getAuthor() {
        return this.authorID;
    }

    public List<Integer> getTopics() {
        return this.topicsList;
    }

    public List<Integer> getComments() {
        return this.commentsList;
    }
}
