package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.CommentState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "comments")
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
     * Date must strictly be in the format dd/MM/yyyy
     */
    @DateTimeFormat(pattern = "dd/MM/yyyy")
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
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "story", nullable = false)
    private Story story;

    /**
     * Author of the Comment. <br>
     * Many Comments can have the same Author <br>
     * Author may be null
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author")
    private User author;


    public Comment(User author, Story story, String content) {
        this.author = author;
        this.story = story;

        this.content = content;
        this.state = CommentState.SUBMITTED;
    }

    public Comment(Story story, String content) {
        this.story = story;

        this.content = content;
        this.state = CommentState.SUBMITTED;
    }

    public Comment() {}


    // SETTERS
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setState(CommentState state) {
        this.state = state;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public String getContent() {
        return this.content;
    }

    public CommentState getState() {
        return this.state;
    }

    public User getAuthor() {
        return this.author;
    }

    public Story getStory() {
        return this.story;
    }
}
