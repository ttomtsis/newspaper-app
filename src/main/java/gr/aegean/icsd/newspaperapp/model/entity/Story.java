package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.StoryState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing the Story resource
 * @see #Story(String, User, String)
 * @see #Story(String, User, String, Set)
 * @see #Story(String, User, String, Topic) 
 */
@Entity
@Table(name = "story")
public class Story {

    /**
     * Primary Key of the Story Entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private StoryState state;

    /**
     * Sets the maximum allowed length of the Story's name
     * @see #name
     */
    @Transient
    private final int maximumNameLength = 50;

    /**
     * Sets the maximum allowed length of the Story's rejectionReason
     * @see #rejectionReason
     */
    @Transient
    private final int maximumRejectionReasonLength = 500;

    /**
     * Sets the maximum allowed length of the Story's content
     * @see #content
     */
    @Transient
    private final int maximumContentLength = 500;

    /**
     * The name of the Story <br>
     * Every Story name is unique and cannot be null, blank, or exceed {@link #maximumNameLength}
     */
    @NotBlank
    @Size(max = maximumNameLength)
    @Column(unique = true)
    private String name;

    /**
     * Reason that a Story was rejected. <br>
     * This field is not null IF AND ONLY IF a Story
     * has been rejected.
     */
    @Size(max = maximumRejectionReasonLength)
    private String rejectionReason;

    /**
     * The content of the Story <br>
     * Content cannot be null, empty, or exceed {@link #maximumContentLength}
     */
    @Size(max = maximumContentLength)
    @NotBlank
    private String content;

    /**
     * Comments associated with the Story. <br>
     * One Story may have plenty of Comments <br>
     * Obviously a Comment can only belong to one Story
     */
    @OneToMany(mappedBy = "storyID", targetEntity = Comment.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Comment> commentsList = new HashSet<Comment>();

    /**
     * Author of the Story. <br>
     * Many Stories can have the same Author <br>
     * Only one Author per Story is allowed
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "authorID", nullable = false, updatable = false)
    private User authorID;

    /**
     * Topics that this Story belongs to. <br>
     * A Story can belong in many Topics at once <br>
     * Many Topics can be associated with the same Story <br>
     */
    @ManyToMany(cascade = CascadeType.REFRESH, targetEntity = Topic.class)
    private Set<Topic> topicsList = new HashSet<Topic>();

    /**
     * Story constructor, used to create Story Entities that will be persisted in the
     * database
     * @param storyName Name of the Story, required
     * @param storyAuthor Author of the Story, required
     * @param storyContent Content of the Story, required
     * @see #Story(String, User, String, Set)
     * @see #Story(String, User, String, Topic) 
     */
    public Story(String storyName, User storyAuthor, String storyContent) {
        this.name = storyName;
        this.authorID = storyAuthor;
        this.content = storyContent;
        this.state = StoryState.CREATED;
    }

    /**
     * Story constructor, used to create Story Entities that will be persisted in the
     * database <br>
     * This constructor is used when a list of Topics, that this Story belongs to, is also specified
     * @param storyName Name of the Story, required
     * @param storyAuthor Author of the Story, required
     * @param storyContent Content of the Story, required
     * @param storyTopics A list of Topics this Story belongs to, optional
     * @see #Story(String, User, String)
     * @see #Story(String, User, String, Topic) 
     * @throws RuntimeException If the list of Topics is empty or null
     */
    public Story(String storyName, User storyAuthor, String storyContent, Set<Topic> storyTopics) {
        if (storyTopics != null && !storyTopics.isEmpty()) {
            this.name = storyName;
            this.authorID = storyAuthor;
            this.content = storyContent;
            topicsList.addAll(storyTopics);
            this.state = StoryState.CREATED;
        }
        else {
            throw new RuntimeException("You must provide at least 1 Topic");
        }

    }

    /**
     * Story constructor, used to create Story Entities that will be persisted in the
     * database <br>
     * This constructor is used when a single Topic, that this Story belongs to, is also specified
     * @param storyName Name of the Story, required
     * @param storyAuthor Author of the Story, required
     * @param storyContent Content of the Story, required
     * @param storyTopic The Topic this Story belongs to, optional
     * @see #Story(String, User, String)
     * @see #Story(String, User, String, Set)  
     * @throws RuntimeException If the list of Topics is empty or null
     */
    public Story(String storyName, User storyAuthor, String storyContent, Topic storyTopic) {

        if (storyTopic != null) {
            this.name = storyName;
            this.authorID = storyAuthor;
            this.content = storyContent;
            topicsList.add(storyTopic);
            this.state = StoryState.CREATED;
        }
        else {
            throw new RuntimeException("Topic cannot be null");
        }

    }
    
    public Story(){}

    /**
     * Generates the {@link #creationDate creationDate} of the Story <br>
     * before the Story is persisted in the database.
     */
    @PrePersist
    private void generateCreationDate() {
        this.creationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    // GETTERS

    /**
     * Get the id of the Story <br>
     * Can be null if the Story has not yet been persisted
     * @return {@link Story#id} of the Story
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the creationDate of the Story <br>
     * Can be null if the Story has not yet been persisted
     * @return {@link Story#creationDate} of the Story
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Get the state of the Story <br>
     * Valid states are defined in {@link StoryState}
     * @return {@link Story#state} of the Story
     */
    public StoryState getState() {
        return this.state;
    }

    /**
     * Get the name of the Story <br>
     * @return {@link Story#name} of the Story
     * @see #maximumNameLength
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the content of the Story <br>
     * @return {@link Story#content} of the Story
     * @see #maximumContentLength
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get the rejectionReason of the Story <br>
     * @return {@link Story#rejectionReason} of the Story
     * @see #maximumRejectionReasonLength
     */
    public String getRejectionReason() {
        return this.rejectionReason;
    }

    /**
     * Get the Author of the Story <br>
     * @return {@link Story#authorID} of the Story
     */
    public User getAuthorID() {
        return this.authorID;
    }

    /**
     * Get the Topics this Story belongs to <br>
     * @return {@link Story#topicsList} of the Story
     */
    public Set<Topic> getTopics() {
        return this.topicsList;
    }

    /**
     * Get the Comments associated with this Story <br>
     * @return {@link Story#commentsList} of the Story
     */
    public Set<Comment> getComments() {
        return this.commentsList;
    }

    // SETTERS

    /**
     * Updates the Story's name <br>
     * New name cannot be empty, null or be greater than {@link #maximumNameLength}
     * @param newName New name of the Story
     * @throws RuntimeException If new name does not conform to the constraints mentioned
     */
    public void setName(String newName) {
        if (newName != null && !newName.isBlank() && newName.length() <= maximumNameLength) {
            this.name = newName;
        }
        else {
            throw new RuntimeException("New name is not valid");
        }
    }

    /**
     * Updates the Story's content <br>
     * New content cannot be empty, null or be greater than {@link #maximumContentLength}
     * @param newContent New content of the Story
     * @throws RuntimeException If new content does not conform to the constraints mentioned
     */
    public void setContent(String newContent) {
        if (newContent != null && !newContent.isBlank() && newContent.length() <= maximumContentLength) {
            this.content = newContent;
        }
        else {
            throw new RuntimeException("New content is not valid");
        }
    }

    /**
     * Updates the Story's rejection reason <br>
     * New rejection reason cannot be empty, null or be greater than {@link #maximumRejectionReasonLength}
     * @param newReason New rejection reason for the Story
     * @throws RuntimeException If new rejectionReason does not conform to the constraints mentioned
     */
    public void setRejectionReason(String newReason) {
        if (newReason != null && !newReason.isBlank() && newReason.length() <= maximumContentLength) {
            this.rejectionReason = newReason;
        }
        else {
            throw new RuntimeException("The rejection reason is not valid");
        }
    }

    // UTILITY

    /**
     * Adds a Topic to the Topic list <br>
     * @param newTopic Topic to be added
     * @throws NullPointerException If newTopic is null
     */
    public void addTopic(Topic newTopic) {
        if (newTopic != null) {
            topicsList.add(newTopic);
        }
        else {
            throw new NullPointerException("New Topic cannot be null");
        }
    }

    /**
     * Remove a Topic from the Topic list <br>
     * Required to use before deleting an association <br>
     * Null and non-existing Topics, are safely ignored
     * without throwing an exception
     * @param topic Topic to be removed
     */
    public void removeTopic(Topic topic) {
        this.topicsList.remove(topic);
    }

}
