package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.TopicState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing the Topic resource
 *
 * @see #Topic(String, User)
 * @see #Topic(String, User, Topic)
 */
@Entity
@Table(name = "topic")
public class Topic {

    /**
     * Primary key of the Topic entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date the Topic was created, assigned by the server
     * before persisting the entity in the database. <br>
     *
     * Date will be in the format E, dd/MMMM/yyyy, HH:mm:ss
     * @see #generateCreationDate()
     */
    @Temporal(TemporalType.DATE)
    @Column(updatable = false, nullable = false)
    private Date creationDate;

    /**
     * State of the Topic, valid states are declared in
     * {@link TopicState}
     *
     * @see TopicState
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private TopicState state;

    /**
     * Sets the maximum allowed length of the Topic's name
     *
     * @see #name
     */
    @Transient
    private final int maximumNameLength = 50;

    /**
     * The name of the Topic <br>
     *
     * Every Topic name is unique and cannot be null, blank, or exceed {@link #maximumNameLength}
     */
    @NotBlank
    @Size(max = maximumNameLength)
    @Column(unique = true)
    private String name;

    /**
     * Author of the Topic <br>
     *
     * Many Topics can have the same Author <br>
     * Only one Author per Topic is allowed
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "authorID", nullable = false, updatable = false)
    private User authorID;

    /**
     * Stories associated with the Topic <br>
     *
     * Many Topics can be associated with the same Story <br>
     * Many Stories can be associated with the same Topic
     */
    @ManyToMany(mappedBy = "topicsList", cascade = CascadeType.REFRESH, targetEntity = Story.class)
    private final Set<Story> storiesList = new HashSet<>();

    /**
     * List of Children Topics <br>
     *
     * Each Topic can be a parent to other Topics
     */
    @OneToMany(mappedBy = "parentTopicID", targetEntity = Topic.class,
            cascade = {CascadeType.REFRESH})
    private final Set<Topic> topicsList = new HashSet<>();

    /**
     * Parent Topic of the Topic <br>
     *
     * Each Topic can be associated with a single parent Topic
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Topic.class)
    @JoinColumn(name = "parentID")
    private Topic parentTopicID;

    /**
     * Topic constructor, used to create Topic entities that will be persisted
     * in the database
     *
     * @param name Name of the Topic
     * @param author User that created the Topic
     */
    public Topic(String name, User author) {
        this.name = name;
        this.authorID = author;
        this.state = TopicState.SUBMITTED;
    }

    /**
     * Alternate Topic constructor, used to create Topic entities that will be persisted
     * in the database. This constructor also specifies a parent Topic field
     *
     * @param name Name of the Topic
     * @param author User that created the Topic
     * @param parentTopic Parent Topic that this Topic will be associated with
     *
     * @throws IllegalArgumentException If parent topic is null
     */
    public Topic(String name, User author, Topic parentTopic) {
        if (parentTopic != null) {
            this.name = name;
            this.authorID = author;
            this.parentTopicID = parentTopic;
            this.state = TopicState.SUBMITTED;
        }
        else {
            throw new IllegalArgumentException("Parent Topic cannot be null");
        }
    }

    public Topic() {}

    /**
     * Generates the {@link #creationDate creationDate} of the Topic <br>
     * before the Topic is persisted in the database.
     */
    @PrePersist
    private void generateCreationDate() {
        this.creationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    /**
     * Removes all the associations this Topic has
     * prior to removal <br><br>
     */
    @PreRemove
    private void removeForeignKeyConstraints() {

        for (Story story : storiesList) {
            story.removeTopic(this);
        }

        for (Topic childTopic : topicsList) {
            childTopic.setParent(null);
        }

    }

    // GETTERS

    /**
     * Get the id of the Topic <br>
     * Can be null if the Topic has not yet been persisted
     *
     * @return {@link Topic#id} of the Topic
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the creationDate of the Topic <br>
     * Can be null if the Topic has not yet been persisted
     *
     * @return {@link Topic#creationDate} of the Topic
     */
    public Date getCreationDate() { return this.creationDate; }

    /**
     * Get the state of the Topic <br>
     * Valid states are defined in {@link TopicState}
     *
     * @return {@link Topic#state} of the Topic
     */
    public TopicState getState() { return this.state; }

    /**
     * Get the name of the Topic <br>
     *
     * @return {@link Topic#name} of the Topic
     * @see #maximumNameLength
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the Stories associated with this Topic <br>
     *
     * @return {@link #storiesList} of the Topic
     */
    public Set<Story> getStories() {
        return this.storiesList;
    }

    /**
     * Get all the children Topics of  this Topic <br>
     *
     * @return {@link #topicsList} of the Topic
     */
    public Set<Topic> getChildrenTopics() { return this.topicsList; }

    /**
     * Get the parent Topic of this Topic <br>
     *
     * @return {@link #parentTopicID}
     */
    public Topic getParentTopic() { return this.parentTopicID; } // Make Optional ?

    /**
     * Get the Author who created this Topic <br>
     *
     * @return {@link #authorID}
     */
    public User getAuthor() { return this.authorID; }

    // SETTERS

    /**
     * Change the name of the Topic, to the new name
     * specified in the parameter <br>
     *
     * @param newName The new name of the Topic
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Change the state of the Topic <br>
     *
     * Valid Topic states are defined in {@link TopicState}
     *
     * @param newState New state of the Topic
     */
    public void setState(TopicState newState) {
        this.state = newState;
    }

    // UTILITY

    /**
     * Associate a new Story with this Topic <br><br>
     *
     * Note that Topics cannot create associations with Stories,
     * this is only a Foreign Key lifecycle management method <br><br>
     *
     * To create an association with a Story use the Story's
     * {@link Story#addTopic(Topic) addTopic} method
     *
     * @param newStory The Story that will be associated with this Topic
     */
    public void addStory(Story newStory) {
        storiesList.add(newStory);
    }

    /**
     * Remove the association between the Story and this Topic <br><br>
     *
     * Note that Topics cannot remove associations with Stories,
     * this is only a Foreign Key lifecycle management method <br><br>
     *
     * To remove an association with a Story use the Story's
     * {@link Story#removeTopic(Topic) removeTopic} method
     *
     * @param story The Story whose association will be removed
     */
    public void removeStory(Story story) {
        storiesList.remove(story);
    }

    /**
     * Create an association between this Topic and a child Topic <br><br>
     *
     * Note that a parent topic cannot create associations with children topics,
     * this is a Foreign Key lifecycle management method. <br><br>
     *
     * To create an association with a child Topic, use {@link #setParent(Topic) setParent}
     * from the child instead <br>
     *
     * @param childTopic The Topic that will be added as a child
     * @throws IllegalArgumentException When setting self as a child
     */
    public void addChild(Topic childTopic) {

        if (Objects.equals(childTopic, this)) {
            throw new IllegalArgumentException("Cannot set self as child topic");
        }
        this.topicsList.add(childTopic);

    }

    /**
     * Remove an association between this Topic and a child Topic <br><br>
     *
     * Note that a parent topic cannot remove associations with children topics,
     * this is a Foreign Key lifecycle management method. <br><br>
     *
     * To remove an association with a child Topic, use {@link #setParent(Topic) setParent}
     * with a null parent topic, from the child instead <br>
     *
     * @param childTopic The Topic that will be added as a child
     */
    public void removeChild(Topic childTopic) {
        this.topicsList.remove(childTopic);
    }

    /**
     * Create an association between this child topic and a parent
     *
     * @param parentTopic The Topic that will server as parent
     * @throws IllegalArgumentException When setting self as a parent
     */
    public void setParent(Topic parentTopic) {

        if (Objects.equals(parentTopic, this)) {
            throw new IllegalArgumentException("Cannot set self as parent topic");
        }

        this.parentTopicID = parentTopic;
    }

    /**
     * Create a Hash of an instantiated Topic
     *
     * @return A hash of the fields {@link #id}, {@link #authorID},
     * {@link #creationDate}, {@link #state}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, authorID, creationDate, state);
    }

    /**
     * Check if this Topic and the specified object are equal <br><br>
     *
     * Two Topics are equal if their id's, authors, creation dates and states
     * are equal
     *
     * @param obj The specified object to be compared with the Topic
     * @return True or False, depending on the result of the comparison
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Topic newTopic) {
            return Objects.equals(this.id, newTopic.getId())
                    && Objects.equals(this.authorID, newTopic.getAuthor())
                    && Objects.equals(this.creationDate, newTopic.getCreationDate())
                    && Objects.equals(this.state, newTopic.getState());
        }

        return false;

    }

}
