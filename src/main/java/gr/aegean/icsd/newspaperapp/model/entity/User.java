package gr.aegean.icsd.newspaperapp.model.entity;

import gr.aegean.icsd.newspaperapp.util.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jfr.BooleanFlag;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing the User resource
 *
 * @see #User(String, String, UserType)
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Sets the maximum allowed length of the User's username
     *
     * @see #username
     */
    @Transient
    private final int maximumUsernameLength = 50;

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
     * Sets the maximum allowed length of the User's password <br>
     *
     * Equal to the maximum varchar size MySQL can accommodate ( 255 )
     *
     * @see #password
     */
    @Transient
    private final int maximumPasswordLength = 255;

    /**
     * The username used in order to log in to the News server <br>
     *
     * Each username must be unique and conform to specific length standards
     */
    @Id
    @NotBlank
    @Size(min = minimumUsernameLength, max = maximumUsernameLength)
    @Column(unique = true, nullable = false, name = "username")
    private String username;

    /**
     * The password used in order to log in to the News server <br>
     */
    @NotBlank
    @Size(min = minimumPasswordLength, max = maximumPasswordLength)
    @Column(nullable = false, name = "password")
    private String password;

    /**
     * Boolean flag setting whether the User's account is enabled or not
     */
    @BooleanFlag
    @NotNull
    @Column(nullable = false, name = "enabled")
    private boolean accountEnabled;

    /**
     * Boolean flag setting whether the User's account has expired or not
     */
    @BooleanFlag
    @NotNull
    @Column(name = "account_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean accountNonExpired;

    /**
     * Boolean flag setting whether the User's account has been locked or not
     */
    @BooleanFlag
    @NotNull
    @Column(name = "account_non_locked", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean accountNonLocked;

    /**
     * Boolean flag setting whether the User's credentials have expired or not
     */
    @BooleanFlag
    @NotNull
    @Column(name = "credentials_non_expired", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean credentialsNonExpired;

    /**
     * Authorities and Roles granted to this User <br>
     *
     * Valid Roles for Users are specified in {@link UserType}
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "username"))
    @Column(name = "authority")
    private final Set<GrantedAuthority> authorities = new HashSet<>();

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
     * @param username Username that will be used to log in
     * @param password Password that will be used to log in
     * @param role Role a User is going to have in the system
     */
    public User(String username, String password, UserType role) {
        this.username = username;
        this.password = password;
        this.accountEnabled = true;

        String userRole = "ROLE_" + role.toString();
        authorities.add(new SimpleGrantedAuthority(userRole));
    }

    public User() {}


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

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.accountEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
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
     * @return A hash of the fields {@link #username},
     * {@link #password}, {@link  #authorities}
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password, authorities);
    }

    /**
     * Check if this User and the specified object are equal <br><br>
     *
     * Two Users are equal if their id's, usernames, passwords and
     * granted authorities are equal
     *
     * @param obj The specified object to be compared with the User
     * @return True or False, depending on the result of the comparison
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof User newUser) {
            return  Objects.equals(this.username, newUser.getUsername())
                    && Objects.equals(this.password, newUser.getPassword())
                    && Objects.equals(this.authorities, newUser.getAuthorities());
        }

        return false;

    }
}
