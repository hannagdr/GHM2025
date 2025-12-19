package hu.nye.ghm.controller;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleUserRepository;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RaffleUserRepository repository;

    @BeforeEach
    void setup() {
        repository.save(RaffleUser.builder()
                .userName("test")
                .name("test test")
                .emailAddress("test@nowhere.eu")
                .password("testPassword")
                .build());
    }

    @Test
    void testLogin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document htmlDoc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNull(htmlDoc.selectFirst(".alert-danger"));
        assertNull(htmlDoc.selectFirst("div.alert-success:contains(Registration successful)"));
        assertNull(htmlDoc.selectFirst("div.alert-success:contains(You have been logged out.)"));
    }

    @Test
    void testError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login?error"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document htmlDoc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNotNull(htmlDoc.selectFirst(".alert-danger"));
        assertNull(htmlDoc.selectFirst(".alert-success"));
    }

    @Test
    void testLogout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login?logout"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document htmlDoc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNotNull(htmlDoc.selectFirst("div.alert-success:contains(You have been logged out.)"));
    }

    @Test
    void testSuccessfulRegistration() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login?registration_successful"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document htmlDoc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNotNull(htmlDoc.selectFirst("div.alert-success:contains(Registration successful)"));
    }
}