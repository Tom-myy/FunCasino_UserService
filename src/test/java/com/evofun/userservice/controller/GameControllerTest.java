package com.evofun.userservice.controller;

import com.evofun.userservice.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final TestUtils testUtils = new TestUtils();
    //use login() from testUtils where it's possible

    @BeforeEach
    public void setMockMvc () {
        testUtils.setMockMvc(mockMvc);
    }

    @Transactional
    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        testUtils.register("IvanMyTest", "IvanovMyTest",
                "IvanNickMyTest", "+380960000000",
                "ivanovmytest@gmail.com", "passMyTest")
                .andExpect(status().isCreated());
    }

    @Transactional
    @Test
    void shouldLoginByEmailAndReturnToken() throws Exception {
        String email = "ivanovtest@gmail.com";
        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", "+380960000000",
                email, "passTest")
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "login": "%s",
                      "pass": "passTest"
                    }
                """.formatted(email);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Transactional
    @Test
    void shouldLoginByNicknameAndReturnToken() throws Exception {
        String nickName = "IvanNickMyTest";

        testUtils.register("IvanTest", "IvanovTest",
                nickName, "+380960000000",
                "ivanovtest@gmail.com", "passTest")
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "login": "%s",
                      "pass": "passTest"
                    }
                """.formatted(nickName);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Transactional
    @Test
    void shouldReturn409IfNicknameAlreadyExists() throws Exception {
        String nickname = "IvanNickTest";

        testUtils.register("IvanTest", "IvanovTest",
                nickname, "+380960000000",
                "ivanovtest@gmail.com", "passTest")
                .andExpect(status().isCreated());

        testUtils.register("IvanTest", "IvanovTest",
                nickname, "+380960000002",
                "ivanovtest2@gmail.com", "passTest")
                .andExpect(status().isConflict());
    }

    @Transactional
    @Test
    void shouldReturn409IfPhoneNumberAlreadyExists() throws Exception {
        String phoneNumber = "+380960000000";

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", phoneNumber,
                "ivanovtest@gmail.com", "passTest")
                .andExpect(status().isCreated());

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest2", phoneNumber,
                "ivanovtest2@gmail.com", "passTest")
                .andExpect(status().isConflict());
    }

    @Transactional
    @Test
    void shouldReturn409IfEmailAlreadyExists() throws Exception {
        String email = "ivanovtest@gmail.com";

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", "+380960000000",
                email, "passTest")
                .andExpect(status().isCreated());

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest2", "+380960000002",
                email, "passTest")
                .andExpect(status().isConflict());
    }
}