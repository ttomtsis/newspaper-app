package gr.aegean.icsd.newspaperapp.security;

import gr.aegean.icsd.newspaperapp.configuration.SecurityConfiguration;
import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModelAssembler;
import gr.aegean.icsd.newspaperapp.model.representation.story.StoryModelAssembler;
import gr.aegean.icsd.newspaperapp.model.representation.topic.TopicModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.CommentService;
import gr.aegean.icsd.newspaperapp.model.service.StoryService;
import gr.aegean.icsd.newspaperapp.model.service.TopicService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Import(SecurityConfiguration.class)
@WebMvcTest

@DisplayName("Request Mapping Tests")
@Tag("Security")
@Tag("Request-Mappings")
public class RequestMappingTests {

    @Autowired
    MockMvc mvc;

    private final static String apiBaseMapping = "/api/v0";
    private final static String storiesMapping = apiBaseMapping + "/stories";
    private final static String commentsMapping = apiBaseMapping + "/comments";
    private final static String topicsMapping = apiBaseMapping + "/topics";

    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentModelAssembler commentModelAssembler;

    @MockBean
    private StoryService storyService;
    @MockBean
    private StoryModelAssembler storyModelAssembler;

    @MockBean
    private TopicService topicService;
    @MockBean
    private TopicModelAssembler topicModelAssembler;

    private final String getUrlGenerator = "gr.aegean.icsd.newspaperapp.security.RequestMappingTests#getUrlGenerator";
    private static Stream<String>  getUrlGenerator() {
        return Stream.of(
                storiesMapping,              // Show all Stories
                storiesMapping + "?name=bob",        // Search specific Story
                storiesMapping + "/1/comments",      // Show All Comments for a Story
                topicsMapping + "?name=bob",         // Search Topic
                topicsMapping + "/1",                // Show Topic
                topicsMapping,                       // Show All Topics
                topicsMapping + "/1/stories"         // Show A Topic's Stories
        );
    }


    @Nested
    @DisplayName("Curator user")
    @WithMockUser(roles = "CURATOR")
    @Tag("Curator")
    class curatorTests {

        @ParameterizedTest
        @MethodSource(getUrlGenerator)
        void getEndpoints(String url) throws Exception {
            mvc.perform(get(url))
                    .andExpect(status().isOk());
        }

    }

    @Nested
    @DisplayName("Visitor user")
    @WithMockUser(roles = "JOURNALIST")
    @Tag("Journalist")
    class journalistTests {

        @ParameterizedTest
        @MethodSource(getUrlGenerator)
        void getEndpoints(String url) throws Exception {
            mvc.perform(get(url))
                    .andExpect(status().isOk());
        }

    }

    @Nested
    @DisplayName("Visitor user")
    @WithAnonymousUser
    @Tag("Visitor")
    class visitorTests {

        @ParameterizedTest
        @MethodSource(getUrlGenerator)
        void getEndpoints(String url) throws Exception {
            mvc.perform(get(url))
                    .andExpect(status().isOk());
        }

    }


}
