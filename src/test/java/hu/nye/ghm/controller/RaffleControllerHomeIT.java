package hu.nye.ghm.controller;

import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleRepository;
import hu.nye.ghm.repository.RaffleUserRepository;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RaffleControllerHomeIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RaffleUserRepository userRepository;
    @Autowired
    private RaffleRepository raffleRepository;
    private RaffleUser adminUser;
    private RaffleUser normalUser;

    @BeforeEach
    void setup() {
        createUsers();
    }

    @Test
    void adminUserHomePage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNotNull(doc.selectFirst("a#create-raffle-button"));
    }

    @Test
    void normalUserHomePage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        assertNull(doc.selectFirst("a#create-raffle-button"));
    }

    @Test
    void normalUserWithOpenRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(false)
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Apply", false);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), false);
        // Draw gomb
        assertDrawButton(columns.get(2), false);
        // Close gomb
        assertCloseButton(columns.get(2), false);
    }

    @Test
    void normalUserWithAppliedRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(false)
                .players(List.of(normalUser))
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Applied", false);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), false);
        // Draw gomb
        assertDrawButton(columns.get(2), false);
        // Close gomb
        assertCloseButton(columns.get(2), false);
    }

    @Test
    void normalUserWithAppliedAndCanceledRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(true)
                .players(List.of(normalUser))
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Canceled", false);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), false);
        // Draw gomb
        assertDrawButton(columns.get(2), false);
        // Close gomb
        assertCloseButton(columns.get(2), false);
    }

    @Test
    void normalUserWithAppliedAndClosedRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(true)
                .players(List.of(normalUser))
                .winner(adminUser)
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Closed", false);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), false);
        // Draw gomb
        assertDrawButton(columns.get(2), false);
        // Close gomb
        assertCloseButton(columns.get(2), false);
    }

    @Test
    void adminUserWithOpenRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(false)
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        assertFalse(viewButton.getFirst().hasAttr("hidden"));
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Apply", true);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), true);
        // Draw gomb
        assertDrawButton(columns.get(2), true);
        // Close gomb
        assertCloseButton(columns.get(2), true);
    }

    @Test
    void adminUserWithClosedRaffle() throws Exception {
        String raffleName = "TheRaffle";
        Raffle openRaffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .closed(true)
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/")
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements dataRows = doc.select(".table-striped > tbody > tr");
        assertNotNull(dataRows);
        assertEquals(1, dataRows.size());
        Element firstRow = dataRows.getFirst();
        Elements columns = firstRow.getElementsByTag("td");
        // Első oszlop - ID
        assertEquals(openRaffle.getId().toString(), columns.getFirst().text());
        // Második oszlop - Név
        assertEquals(raffleName, columns.get(1).text());
        // Harmadik oszlop - Gombok
        // View gomb
        Elements viewButton = columns.get(2).getElementsByAttributeValue("href", "/raffle/" + openRaffle.getId());
        assertFalse(viewButton.isEmpty());
        assertFalse(viewButton.getFirst().hasAttr("hidden"));
        // Apply/Applied/Canceled/Closed gomb
        assertApplyButton(columns.get(2), "Canceled", false);
        // Edit gomb
        assertEditButton(columns.get(2), openRaffle.getId(), false);
        // Draw gomb
        assertDrawButton(columns.get(2), false);
        // Close gomb
        assertCloseButton(columns.get(2), false);
    }

    private static void assertApplyButton(Element thirdColumn, String expectedText, boolean isHidden) {
        Elements applyButton = thirdColumn.getElementsByClass("apply-button");
        assertFalse(applyButton.isEmpty());
        assertEquals(expectedText, applyButton.getFirst().text());
        assertEquals(isHidden, applyButton.hasAttr("hidden"));
    }

    private static void assertCloseButton(Element thirdColumn, boolean isExpected) {
        Elements closeButton = thirdColumn.getElementsByClass("close-button");
        assertEquals(isExpected, !closeButton.isEmpty() && !closeButton.hasAttr("hidden"));
    }

    private static void assertDrawButton(Element thirdColumn, boolean isExpected) {
        Elements closeButton = thirdColumn.getElementsByClass("draw-button");
        assertEquals(isExpected, !closeButton.isEmpty() && !closeButton.hasAttr("hidden"));
    }

    private static void assertEditButton(Element thirdColumn, long raffleId, boolean isExpected) {
        Elements editButton = thirdColumn.getElementsByAttributeValue("href", "/raffle/" + raffleId + "/edit");
        assertEquals(isExpected, !editButton.isEmpty() && !editButton.hasAttr("hidden"));
    }

    private void createUsers() {
        this.adminUser = userRepository.save(RaffleUser.builder()
                .userName("admin")
                .password("")
                .name("Admin")
                .emailAddress("admin@test.eu")
                .roles(Set.of("ADMIN", "USER"))
                .build());
        this.normalUser = userRepository.save(RaffleUser.builder()
                .userName("user")
                .password("")
                .name("User")
                .roles(Set.of("USER"))
                .emailAddress("user@test.eu")
                .build());
    }

    @NonNull
    private static UserRequestPostProcessor getAdminUser() {
        return user("admin").roles("USER", "ADMIN");
    }

    @NonNull
    private static UserRequestPostProcessor getNormalUser() {
        return user("user").roles("USER");
    }
}