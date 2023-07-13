package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Entity representing the Comment resource
 * @see #Comment(Story, String)
 * @see #Comment(Story, String, User)
 */
@Entity
@Table(name = "comment")
public class Comment {

    /**
     * Primary Key of the Comment Entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date the Comment was created, assigned by the server
     * before persisting the entity in the database. <br>
     * Date will be in the format E, dd/MMMM/yyyy, HH:mm:ss
     * @see #generateCreationDate()
     */
    @Temporal(TemporalType.DATE)
    @Column(updatable = false, nullable = false)
    private Date creationDate;

    /**
     * State of the Comment, valid states are declared in
     * {@link CommentState}
     * @see CommentState
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommentState state;

    /**
     * Sets the maximum allowed length of the Comment's content
     * @see #content
     */
    @Transient
    private final int maxContentLength = 500;

    /**
     * Content of the Comment <br>
     * Content cannot be null, blank or exceed {@link #maxContentLength}
     * @see #maxContentLength
     */
    @Size(max = maxContentLength)
    @NotBlank
    private String content;

    /**
     * Story that the Comment belongs to <br>
     * Many Comments belong to One Story
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = Story.class)
    @JoinColumn(name = "story_id", nullable = false, updatable = false)
    private Story storyID;

    /**
     * Author of the Comment. <br>
     * Many Comments can have the same Author <br>
     * Author may be null
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "author_id", updatable = false)
    private User authorID;

    /**
     * Constructor used when the Comment's Author is logged into the system
     * @param authorID ID of the Author
     * @param storyID ID of the Story this Comment belongs to
     * @param content Content of the Comment
     * @throws RuntimeException If the provided Author is null
     */
    public Comment(Story storyID, String content, User authorID) {
        if ( authorID == null ) { throw new NullPointerException("The Author you provided is null"); }

        this.authorID = authorID;
        this.storyID = storyID;

        this.content = content;
        this.state = CommentState.SUBMITTED;
    }

    /**
     * Constructor used when the Comment's Author is a Visitor
     * @param storyID ID of the Story this Comment belongs to
     * @param content Content of the Comment
     */
    public Comment(Story storyID, String content) {
        this.storyID = storyID;

        this.content = content;
        this.state = CommentState.SUBMITTED;
    }

    public Comment() {}

    /**
     * Generates the {@link #creationDate creationDate} of the Comment <br>
     * before the Comment is persisted in the database.
     */
    @PrePersist
    private void generateCreationDate() {
        this.creationDate = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    // SETTERS

    /**
     * Change the content of the comment <br>
     * New content must not be blank or exceed {@link #maxContentLength}
     * @param content New content
     * @throws RuntimeException If content is blank or null
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Change the state of the Comment <br>
     * Valid Comment states are defined in {@link CommentState}
     * @param state New state of the Comment
     * @throws RuntimeException If the state is null
     */
    public void setState(CommentState state) {
        this.state = state;
    }

    // GETTERS

    /**
     * Get the id of the Comment <br>
     * Can be null if the Comment has not yet been persisted
     * @return {@link Comment#id} of the Comment
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the creationDate of the Comment <br>
     * Can be null if the Comment has not yet been persisted
     * @return {@link Comment#creationDate} of the Comment
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Get the content of the Comment <br>
     * Cannot be null or blank
     * @return {@link Comment#content} of the Comment
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Get the state of the Comment <br>
     * Cannot be null or blank, valid states are
     * defined in {@link CommentState}
     * @return {@link Comment#state} of the Comment
     */
    public CommentState getState() {
        return this.state;
    }

    /**
     * Get the Author of the Comment <br>
     * Can be null if Comment was created by a visitor
     * @return {@link Comment#authorID} of the Comment
     * @see User
     */
    public Optional<User> getAuthor() {
        return Optional.ofNullable(this.authorID);
    }

    /**
     * Get the Story associated with the Comment <br>
     * Cannot be null, all Comments are associated with a Story
     * @see Story
     * @return {@link Comment#storyID} of the Comment
     */
    public Story getStory() {
        return this.storyID;
    }

    /**
     * Create a Hash of an instantiated Comment
     *
     * @return A hash of the fields {@link #id}, {@link #authorID},
     * {@link #creationDate}, {@link #state}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, authorID, creationDate, state);
    }

    /**
     * Check if this Comment and the specified object are equal <br><br>
     *
     * Two Comments are equal if their id's, authors, creation dates and states
     * are equal
     *
     * @param obj The specified object to be compared with the Comment
     * @return True or False, depending on the result of the comparison
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Comment newComment) {
            return Objects.equals(this.id, newComment.getId())
                    && Objects.equals(this.authorID, newComment.getAuthor().get())
                    && Objects.equals(this.creationDate, newComment.getCreationDate())
                    && Objects.equals(this.state, newComment.getState());
        }

        return false;

    }
}
