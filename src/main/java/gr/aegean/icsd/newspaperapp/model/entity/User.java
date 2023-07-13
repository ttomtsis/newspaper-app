package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing the User resource
 *
 * @see #User(String, String, UserType)
 */
@Entity
@Table(name = "user")
public class User {

    /**
     * Primary Key of the User Entity
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * Sets the maximum allowed length of the User's username
     *
     * @see #username
     */
    @Transient
    private final int maximumUsernameLength = 20;

    /**
     * Sets the minimum allowed length of the User's username
     *
     * @see #username
     */
    @Transient
    private final int minimumUsernameLength = 3;

    /**
     * Sets the minimum allowed length of the User's password
     *
     * @see #username
     */
    @Transient
    private final int minimumPasswordLength = 5;

    /**
     * The username used in order to login to the News server <br>
     *
     * Each username must be unique and conform to specific length standards
     */
    @NotBlank
    @Size(min = minimumUsernameLength, max = maximumUsernameLength)
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The password used in order to login to the News server <br>
     */
    @NotBlank
    @Size(min = minimumPasswordLength)
    @Column(nullable = false)
    private String password;

    /**
     * The role of the User in the News Server <br>
     *
     * Valid User roles are specified in {@link UserType}
     *
     * @see UserType
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserType role;

    /**
     * Comments associated with the User <br>
     * A User can create a multitude of Comments, but a Comment can be created by a single user
     */
    @OneToMany(mappedBy = "authorID", targetEntity = Comment.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<Comment> commentsList = new HashSet<>();

    /**
     * Stories associated with the User <br>
     * A User can create a multitude of Stories, but a Story can be created by a single user
     */
    @OneToMany(mappedBy = "authorID", targetEntity = Story.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<Story> storiesList = new HashSet<>();

    /**
     * Topics associated with the User <br>
     * A User can create a multitude of Topics, but a Topic can be created by a single user
     */
    @OneToMany(mappedBy = "authorID", targetEntity = Topic.class,
            cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private final Set<Topic> topicsList = new HashSet<>();

    /**
     * User constructor, used to create User entities that will be persisted
     * in the database <br>
     *
     * @param username Username that will be used to login
     * @param password Password that will be used to login
     * @param role Role a User is going to have in the system
     */
    public User(String username, String password, UserType role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {}

    /**
     * Get the id of the User <br>
     *
     * @return {@link User#id} of the User
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the username of the User <br>
     *
     * @return {@link User#username} of the User
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the password of the User <br>
     *
     * @return {@link User#password} of the User
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the role of the User <br>
     *
     * @return {@link User#role} of the User
     */
    public UserType getRole() {
        return this.role;
    }

    /**
     * Get the Comments associated with the User <br>
     *
     * @return {@link User#commentsList} of the User
     */
    public Set<Comment> getComments() {
        return this.commentsList;
    }

    /**
     * Get the Stories associated with the User <br>
     *
     * @return {@link User#storiesList} of the User
     */
    public Set<Story> getStories() {
        return this.storiesList;
    }

    /**
     * Get the Topics associated with the User <br>
     *
     * @return {@link User#topicsList} of the User
     */
    public Set<Topic> getTopics() {
        return this.topicsList;
    }

    /**
     * Create a Hash of an instantiated User
     *
     * @return A hash of the fields {@link #id}, {@link #username},
     * {@link #password}, {@link  #role}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }

    /**
     * Check if this User and the specified object are equal <br><br>
     *
     * Two Users are equal if their id's, usernames, passwords and roles
     * are equal
     *
     * @param obj The specified object to be compared with the User
     * @return True or False, depending on the result of the comparison
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof User newUser) {
            return Objects.equals(this.id, newUser.getId())
                    && Objects.equals(this.username, newUser.getUsername())
                    && Objects.equals(this.password, newUser.getPassword())
                    && Objects.equals(this.role, newUser.getRole());
        }

        return false;

    }
}
