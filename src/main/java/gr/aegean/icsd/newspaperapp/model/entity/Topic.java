package gr.aegean.icsd.newspaperapp.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Author of the Topic. <br>
     * Many Topics can have the same Author <br>
     * Only one Author per Topic is allowed
     */
    @ManyToOne(cascade = CascadeType.REFRESH, targetEntity = User.class)
    @JoinColumn(name = "authorID", nullable = false, updatable = false)
    private User authorID;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
