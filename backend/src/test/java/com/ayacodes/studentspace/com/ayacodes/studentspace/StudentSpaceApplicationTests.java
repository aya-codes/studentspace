package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class YourApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void matchUser_returnsOk() throws Exception {
        mockMvc.perform(post("/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "alice",
                        "topic": "FRIENDSHIP"
                    }
                """))
                .andExpect(status().isOk());
    }

    @Test
    void matchUser_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "alice",
                        "topic": "NOTATOPIC"
                    }
                """))
                .andExpect(status().isBadRequest());
    }
}

