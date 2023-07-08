package gr.aegean.icsd.newspaperapp.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "authorID", targetEntity = Comment.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Comment> commentsList = new HashSet<Comment>();

    public Long getId() {
        return id;
    }
}
