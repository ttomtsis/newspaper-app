package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

/**
 * Entity representing the Comment resource
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
     * Content of the Comment
     */
    @NotBlank
    private String content;

    /**
     * State of the Comment, valid states are declared in
     * {@link CommentState}
     * @see CommentState
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private CommentState state;

    /**
     * Story that the Comment belongs to <br>
     * Many Comments belong to One Story
     */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "story", nullable = false, updatable = false)
    private Story story;

    /**
     * Author of the Comment. <br>
     * Many Comments can have the same Author <br>
     * Author may be null
     */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "author", updatable = false)
    private User author;

    /**
     * Constructor used when the Comment's Author is logged into the system
     * @param authorID ID of the Author
     * @param storyID ID of the Story this Comment belongs to
     * @param content Content of the Comment
     */
    public Comment(Story storyID, String content, User authorID) {
        if ( authorID == null ) { throw new RuntimeException("The Author you provided is null"); }

        this.author = authorID;
        this.story = storyID;

        this.content = content;
        this.state = CommentState.SUBMITTED;
    }

    /**
     * Constructor used when the Comment's Author is a Visitor
     * @param storyID ID of the Story this Comment belongs to
     * @param content Content of the Comment
     */
    public Comment(Story storyID, String content) {
        this.story = storyID;

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
    public void setContent(String content) {
        if ( content != null && !content.isBlank()) {
            this.content = content;
        }
        else {
            throw new RuntimeException("Content cannot be null");
        }
    }

    public void setState(CommentState state) {
        if (state != null) {
            this.state = state;
        }
        else {
            throw new RuntimeException("State cannot be null");
        }
    }

    // GETTERS
    public Long getId() {
        return this.id;
    }

    // Can be null when the Comment has not been persisted yet
    // Wrap in Optional ?
    public Date getCreationDate() {
        return this.creationDate;
    }

    @NotNull
    public String getContent() {
        return this.content;
    }

    @NotNull
    public CommentState getState() {
        return this.state;
    }

    public Optional<User> getAuthor() {
        return Optional.ofNullable(this.author);
    }

    @NotNull
    public Story getStory() {
        return this.story;
    }
}
