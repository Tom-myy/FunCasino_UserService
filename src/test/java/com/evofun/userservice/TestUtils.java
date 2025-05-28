package com.evofun.userservice;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestUtils {

    private MockMvc mockMvc;

    public void setMockMvc (MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions register(String name, String surname, String nickname, String phoneNumber, String email, String pass) throws Exception {
        String request = """
                {
                          "name": "%s",
                          "surname": "%s",
                          "nickname": "%s",
                          "phoneNumber": "%s",
                          "email": "%s",
                          "pass": "%s"
                }
                """.formatted(name, surname, nickname, phoneNumber, email, pass);

        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
    }



    public MvcResult login(String login, String pass) throws Exception {
        String request = """
                {
                          "login": "%s",
                          "pass": "%s"
                }
                """.formatted(login, pass);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        return loginResult;
    }
}
