package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * User's possible roles <br>
 * {@link #JOURNALIST} <br>
 * {@link #CURATOR} <br>
 * Any other user is considered a visitor by the applicaiton <br>
 */
public enum UserType {

    /**
     * A Journalist has all the permissions of a non-authenticated user in addition to the following: <br>
     * Stories: Create, edit, submit, view, search <br>
     * Topics: Create, modify, view, search <br>
     * Comments: Create, modify
     */
    JOURNALIST,

    /**
     * A Curator resembles an administrator, however the two should not be confused <br>
     * The Curator has all the permissions of a Journalist in addition to the following: <br>
     * Stories: Publish, Approve, Reject
     * Topics: Approve, Reject
     * Comments: Approve, Reject
     */
    CURATOR
}
