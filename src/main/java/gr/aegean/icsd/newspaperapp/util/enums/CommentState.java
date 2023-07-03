package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * Comment's possible states <br>
 * {@link #SUBMITTED} <br>
 * {@link #APPROVED} <br>
 * {@link #REJECTED} <br>
 */
public enum CommentState {
    /**
     * Initial state when a comment is created, <br>
     * currently not published and can be modified
     */
    SUBMITTED,

    /**
     * An APPROVED comment is published and accessible by all users
     */
    APPROVED,

    /**
     * This is not an actual state that the Comment entity can take, <br>
     * the REJECTED state is used solely to communicate to the service layer
     * that the client wishes to delete the Comment
     */
    REJECTED
}
