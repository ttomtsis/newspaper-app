package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * Topic's possible states <br>
 * {@link #SUBMITTED} <br>
 * {@link #APPROVED} <br>
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
    APPROVED
}
