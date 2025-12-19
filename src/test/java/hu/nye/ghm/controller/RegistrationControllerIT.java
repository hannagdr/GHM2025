package hu.nye.ghm.controller;

import hu.nye.ghm.repository.RaffleUserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RaffleUserRepository repository;

    @BeforeEach
    void setup() {
    }

    @Test
    void registerPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void successfulRegistering() throws Exception {
        String testUserName = "test.person";
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("name", "Test Person")
                        .param("userName", testUserName)
                        .param("emailAddress", "test.person@test.eu")
                        .param("password", "test.password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?registration_successful"));

        assertTrue(repository.findByUserName(testUserName).isPresent());
    }
}