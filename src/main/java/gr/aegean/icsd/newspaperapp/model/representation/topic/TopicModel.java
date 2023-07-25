package gr.aegean.icsd.newspaperapp.model.representation.topic;

import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.List;

/**
 * Representation Model of the Topic Entity
 */
public class TopicModel extends RepresentationModel<TopicModel> {


    private Long id;

    private Date creationDate;

    private TopicState state;

    private String name;

    private String authorID;

    private List<Integer> storiesList;

    private List<Integer> topicsList;

    private Integer parentTopicID;



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

    public TopicState getState() {
        return state;
    }

    public void setState(TopicState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public List<Integer> getStoriesList() {
        return storiesList;
    }

    public void setStoriesList(List<Integer> storiesList) {
        this.storiesList = storiesList;
    }

    public List<Integer> getTopicsList() {
        return topicsList;
    }

    public void setTopicsList(List<Integer> topicsList) {
        this.topicsList = topicsList;
    }

    public Integer getParentTopicID() {
        return parentTopicID;
    }

    public void setParentTopicID(Integer parentTopicID) {
        this.parentTopicID = parentTopicID;
    }
}
