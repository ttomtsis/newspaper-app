package gr.aegean.icsd.newspaperapp.util.enums;

/**
 * Story's possible states <br>
 * {@link #CREATED} <br>
 * {@link #SUBMITTED} <br>
 * {@link #APPROVED} <br>
 * {@link #PUBLISHED} <br>
 */
public enum StoryState {
    /**
     * Initial state when a story is created, <br>
     * a story may be modified in this state.
     */
    CREATED,

    /**
     * Story is awaiting approval, may not be modified in this state <br>
     */
    SUBMITTED,

    /**
     * Story is awaiting to be published, may not be modified in this state <br>
     */
    APPROVED,

    /**
     * Story has been published and is accessible by all users <br>
     */
    PUBLISHED
}
