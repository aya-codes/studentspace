package com.ayacodes.studentspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class YourApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void matchUser_statusTransitionsFromWaitingToOk() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // First user joins -> should return "waiting"
        MvcResult startResult1 = mockMvc.perform(post("/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "alice",
                            "topic": "FRIENDSHIP"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("waiting"))
                .andReturn();

        String roomId = mapper.readTree(startResult1.getResponse().getContentAsString())
                .get("roomId").asText();

        // Second user joins -> should return "ok"
        mockMvc.perform(post("/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "username": "bob",
                            "topic": "FRIENDSHIP"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.roomId").value(roomId));

        // Now check the room status -> should be "ok"
        mockMvc.perform(get("/status/" + roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
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

