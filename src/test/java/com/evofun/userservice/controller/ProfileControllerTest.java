package com.evofun.userservice.controller;

import com.evofun.userservice.TestUtils;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final TestUtils testUtils = new TestUtils();

    @BeforeEach
    public void setMockMvc () {
        testUtils.setMockMvc(mockMvc);
    }

/*    public ProfileControllerTest(TestUtils testUtils) {
        this.testUtils = testUtils;
    }*/

    @Transactional
    @Test
    void shouldReturn401IfNoTokenForProfile() throws Exception {
        mockMvc.perform(get("/api/profile/me"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void shouldReturnProfileInfoWhenAuthorized() throws Exception {
        String email = "ivanovtest@gmail.com";
        String pass = "passTest";

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", "+380960000000",
                email, pass)
                .andExpect(status().isCreated());

        MvcResult loginResult = testUtils.login(email, pass);

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(get("/api/profile/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    void shouldReturn401IfNoTokenForProfileUpdating() throws Exception {
        mockMvc.perform(get("/api/profile/update"))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void shouldUpdateNameAndSurname() throws Exception {
        String email = "ivanovtest@gmail.com";
        String pass = "passTest";

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", "+380960000000",
                email, pass)
                .andExpect(status().isCreated());

        MvcResult loginResult = testUtils.login(email, pass);

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        String updateBody = """
                {
                          "name": "NewNameTest",
                          "surname": "NewSurnameTest"
                }
                """;

        MvcResult updateResult = mockMvc.perform(patch("/api/profile/update")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andReturn();

        String updateResultJson = updateResult.getResponse().getContentAsString();

        String expectedUpdateResult = """
                {
                          "name": "NewNameTest",
                          "surname": "NewSurnameTest",
                          "nickname": "IvanNickTest",
                          "phoneNumber": "+380960000000",
                          "email": "ivanovtest@gmail.com"
                }
                """;

        JSONAssert.assertEquals(expectedUpdateResult, updateResultJson, false);
    }

    @Transactional
    @Test
    void shouldReturn409WhenUpdatingEmailToAlreadyUsed() throws Exception {
        String email = "ivanovtest@gmail.com";
        String pass = "passTest";

        testUtils.register("IvanTest", "IvanovTest",
                "IvanNickTest", "+380960000000",
                email, pass)
                .andExpect(status().isCreated());

        MvcResult loginResult = testUtils.login(email, pass);

        String token = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        //second User
        String email2 = "ivanovtest2@gmail.com";

        testUtils.register("IvanTest2", "IvanovTest2",
                "IvanNickTest2", "+380960000002",
                email2, pass)
                .andExpect(status().isCreated());
        //

        String updateBody = """
                {
                          "email": "%s"
                }
                """.formatted(email2);//email from 2nd user -> fail

        mockMvc.perform(patch("/api/profile/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(updateBody))
                .andExpect(status().isConflict());
    }

/*    private ResultActions register(String name, String surname, String nickname, String phoneNumber, String email, String pass) throws Exception {
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



    private MvcResult authentication(String authentication, String pass) throws Exception {
        String request = """
                {
                          "authentication": "%s",
                          "pass": "%s"
                }
                """.formatted(authentication, pass);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        return loginResult;
    }*/
}
