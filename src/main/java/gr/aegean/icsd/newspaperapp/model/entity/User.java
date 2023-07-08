package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserType role;

    @OneToMany(mappedBy = "authorID", targetEntity = Comment.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Comment> commentsList = new HashSet<Comment>();

    @OneToMany(mappedBy = "authorID", targetEntity = Story.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Story> storiesList = new HashSet<Story>();

    @OneToMany(mappedBy = "authorID", targetEntity = Topic.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Topic> topicsList = new HashSet<Topic>();

    public User(String username, String password, UserType role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {}

    public Long getId() {
        return id;
    }
}
