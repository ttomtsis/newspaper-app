package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * Topic's possible states <br>
 * {@link #SUBMITTED} <br>
 * {@link #APPROVED} <br>
 * {@link #REJECTED} <br>
 */
public enum TopicState  {
    /**
     * Initial state when a Topic is created. <br>
     * In this state, it is not published and can be modified
     */
    SUBMITTED,

    /**
     * An APPROVED Topic is published and accessible by all users. <br>
     * ONLY an APPROVED Topic can be associated with Stories
     */
    APPROVED,

    /**
     * This is not an actual state that the Topic entity can take, <br>
     * the REJECTED state is used solely to communicate to the service layer
     * that the client wishes to delete the Topic
     */
    REJECTED
}
