package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * Comment's possible states <br>
 * {@link #SUBMITTED} <br>
 * {@link #APPROVED} <br>
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
    APPROVED
}
