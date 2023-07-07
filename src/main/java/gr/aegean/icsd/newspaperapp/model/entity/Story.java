package gr.aegean.icsd.newspaperapp.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Story {
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Comments associated with the Story. <br>
     */
    @OneToMany(mappedBy = "storyID", targetEntity = Comment.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Comment> commentsList = new HashSet<Comment>();

    /**
     * Author of the Story. <br>
     * Many Stories can have the same Author <br>
     */
//    @ManyToOne(cascade = CascadeType.REFRESH)
//    @JoinColumn(name = "authorID", nullable = false, updatable = false)
//    private User author;

    public Story(){}

    public Long getId() {
        return id;
    }

    public void addComment(Comment newComment) {
        if (newComment != null) {
            commentsList.add(newComment);
        }
        else {
            throw new RuntimeException("Null comment cannot be added to the comments list");
        }
    }

    public Set<Comment> getComments() {
        return this.commentsList;
    }


}
