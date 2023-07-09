package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Date the Story was created, assigned by the server
     * before persisting the entity in the database. <br>
     * Date will be in the format E, dd/MMMM/yyyy, HH:mm:ss
     * @see #generateCreationDate()
     */
    @Temporal(TemporalType.DATE)
    @Column(updatable = false, nullable = false)
    private Date creationDate;

    /**
     * State of the Story, valid states are declared in
     * {@link StoryState}
     * @see StoryState
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private TopicState state;

    @Transient
    private final int maximumNameLength = 50;

    @NotBlank
    @Size(max = maximumNameLength)
    @Column(unique = true)
    private String name;

    /**
     * Author of the Topic. <br>
     * Many Topics can have the same Author <br>
     * Only one Author per Topic is allowed
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "authorID", nullable = false, updatable = false)
    private User authorID;

    @ManyToMany(mappedBy = "topicsList", cascade = CascadeType.REFRESH,
            targetEntity = Story.class)
    private Set<Story> storiesList;

    @OneToMany(mappedBy = "parentTopicID", targetEntity = Topic.class,
            cascade = {CascadeType.REFRESH})
    private Set<Topic> topicsList = new HashSet<Topic>();

    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Topic.class)
    @JoinColumn(name = "parentID")
    private Topic parentTopicID;

    public Topic(String name, User author) {
        this.name = name;
        this.authorID = author;
        this.state = TopicState.SUBMITTED;
    }

    public Topic(String name, User author, Topic parentTopic) {
        if (parentTopic != null) {
            this.name = name;
            this.authorID = author;
            this.parentTopicID = parentTopic;
            this.state = TopicState.SUBMITTED;
        }
        else {
            throw new RuntimeException("Parent Topic cannot be null");
        }
    }

    public Topic() {}

    @PrePersist
    private void generateCreationDate() {
        this.creationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public Set<Story> getStoriesList() {
        return this.storiesList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addStory(Story newStory) {
        if (newStory != null) {
            storiesList.add(newStory);
        }
        else {
            throw new NullPointerException("New Story cannot be null");
        }
    }

    public void removeStory(Story story) {
        storiesList.remove(story);
    }
}
