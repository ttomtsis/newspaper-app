package gr.aegean.icsd.newspaperapp.controller;

import gr.aegean.icsd.newspaperapp.model.representation.comment.CommentModelAssembler;
import gr.aegean.icsd.newspaperapp.model.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Currently tests whether the Comment endpoints have been created successfully
 */
@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService service;

    @MockBean
    private CommentModelAssembler assembler;

    private final String commentBaseMapping = "/api/v0/comments";
    private final String storiesBaseMapping = "/api/v0/stories";
    private final int mockCommentID = 1;
    private final int mockStoryID = 1;
    private final String content = "{}";


    /**
     * TC-1
     */
    @Test
    public void testCreateCommentMapping() throws Exception {
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().isCreated()
                );
    }

    /**
     * TC-2
     */
    @Test
    public void testCreateCommentEmptyBody() throws Exception {
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    /**
     * TC-3
     */
    @Test
    public void testCreateCommentEmptyContentType() throws Exception {
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    /**
     * TC-4
     */
    @Test
    public void testCreateCommentUnknownHeaders() throws Exception {
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("test132","test")
                                .content(content)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().isCreated()
                );
    }

    /**
     * TC-5
     */
    @Test
    public void testCreateCommentBadHeaders() throws Exception {
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType("application/test")
                                .content(content)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    /**
     * TC-6
     */
    @Test
    public void testCreateCommentInvalidContent() throws Exception {
        String invalidContent = "me lene bob";
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidContent)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
        invalidContent = "{";
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidContent)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
        invalidContent = "[]";
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidContent)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    /**
     * TC-7
     */
    @Test
    public void testCreateCommentInvalidCombination() throws Exception {
        String JSONString = "TEST";
        this.mockMvc
                .perform(
                        post(commentBaseMapping)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JSONString)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }


    @Test
    public void testShowAllCommentsForAStory() throws Exception {
        String requestURL = storiesBaseMapping + "/" + mockStoryID + "/comments";
        this.mockMvc
                .perform(
                        get(requestURL)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().isOk()
                );

        this.mockMvc
                .perform(
                        get(requestURL)
                                .param("sortType", "DESC")
                )
                .andDo(
                  print()
                )
                .andExpect(
                        status().isOk()
                );
    }


    @Test
    public void testApproveComment() throws Exception {
        this.mockMvc
                .perform(
                        patch(commentBaseMapping + "/" + mockCommentID)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().isNoContent()
                );
    }


    @Test
    public void testDeleteComment() throws Exception {
        this.mockMvc
                .perform(
                        delete(commentBaseMapping + "/" + mockCommentID)
                )
                .andDo(
                        print()
                )
                .andExpect(
                        status().isNoContent()
                );
    }
}
