package hu.nye.ghm.controller;

import hu.nye.ghm.domain.Prize;
import hu.nye.ghm.domain.Raffle;
import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.PrizeRepository;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RaffleControllerViewIT {
    private static final String ADMIN_USERNAME = "admin";
    private static final String USER_USERNAME = "user";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RaffleUserRepository userRepository;
    @Autowired
    private PrizeRepository prizeRepository;
    @Autowired
    private RaffleRepository raffleRepository;
    private RaffleUser adminUser;
    private RaffleUser normalUser;
    private Map<String, RaffleUser> userStore;
    private Prize prizeOne;
    private Prize prizeTwo;


    @BeforeEach
    void setup() {
        createUsers();
        createPrizes();
    }

    public static Stream<Arguments> applyButtonTestData() {
        return Stream.of(
                Arguments.of("Normal User Not Applied", null, false, null, false, "Apply", false, true),
                Arguments.of("Normal User Already Applied", USER_USERNAME, false, null, false, "Applied", true, true),
                Arguments.of("Normal User Canceled", null, true, null, false, "Canceled", true, true),
                Arguments.of("Normal User Closed - Not Winner", USER_USERNAME, true, ADMIN_USERNAME, false, "Closed", true, true),
                Arguments.of("Normal User Closed - Winner", USER_USERNAME, true, USER_USERNAME, false, "Closed", true, true),

                Arguments.of("Admin User Not Applied", null, false, null, true, "Apply", false, false),
                Arguments.of("Admin User Canceled", null, true, null, true, "Canceled", true, true),
                Arguments.of("Admin User Closed - Not Winner", ADMIN_USERNAME, true, USER_USERNAME, true, "Closed", true, true),
                Arguments.of("Admin User Closed - Winner", ADMIN_USERNAME, true, ADMIN_USERNAME, true, "Closed", true, true)
        );
    }

    @ParameterizedTest(name = "Apply button test: {0}")
    @MethodSource("applyButtonTestData")
    void applyButton(String summary, String appliedUser, boolean isClosed, String winner, boolean isAdmin,
                     String expectedText, boolean isDisabled, boolean isVisible) throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(appliedUser == null ? List.of() : List.of(this.userStore.get(appliedUser)))
                .closed(isClosed)
                .winner(winner == null ? null : this.userStore.get(winner))
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(isAdmin ? getAdminUser() : getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element applyButton = doc.selectFirst("button.apply-button");
        assertNotNull(applyButton);
        assertEquals(expectedText, applyButton.text());
        assertEquals(isDisabled, applyButton.hasAttr("disabled") || applyButton.hasClass("disabled"));
        assertEquals(isVisible, !applyButton.hasAttr("hidden"));
    }


    public static Stream<Arguments> adminButtonsTestData() {
        return Stream.of(
                Arguments.of("No winner - Open", false, null, true),
                Arguments.of("No winner - Closed", true, null, false),
                Arguments.of("Has winner", true, USER_USERNAME, false)
        );
    }

    @ParameterizedTest(name = "Admin buttons: {0}")
    @MethodSource("adminButtonsTestData")
    void adminButtons(String summary, boolean isClosed, String winner, boolean areAdminButtonVisible) throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(isClosed)
                .winner(winner == null ? null : this.userStore.get(winner))
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element closeButton = doc.selectFirst("button.close-button");
        Element drawButton = doc.selectFirst("button.draw-button");
        assertNotNull(closeButton);
        assertNotNull(drawButton);
        assertEquals(areAdminButtonVisible, !closeButton.hasAttr("hidden"));
        assertEquals(areAdminButtonVisible, !drawButton.hasAttr("hidden"));
    }

    @Test
    void headerFields() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(false)
                .winner(null)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element raffleName = doc.selectFirst("div#raffle_name");
        assertNotNull(raffleName);
        assertEquals(newRaffle.getName(), raffleName.text());

        Element prizeName = doc.selectFirst("div#prize_name");
        assertNotNull(prizeName);
        String expectedPrizeName = "Prize: %s (%s)".formatted(newRaffle.getPrize().getName(), newRaffle.getPrize().getCategory());
        assertEquals(expectedPrizeName, prizeName.text());

        Element applicantInfo = doc.selectFirst("div#applicant_info");
        assertNotNull(applicantInfo);
        assertEquals("Number of applicants: " + newRaffle.getPlayers().size(), applicantInfo.text());
    }

    @Test
    void normalUserListOfApplicant() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(false)
                .winner(null)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element applicantList = doc.selectFirst("div#applicant_list");
        assertNull(applicantList);
    }

    @Test
    void adminUserListOfApplicant() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(false)
                .winner(null)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element applicantList = doc.selectFirst("div#applicant_list");
        assertNotNull(applicantList);

        Elements listElements = applicantList.select("li");
        assertEquals(newRaffle.getPlayers().size(), listElements.size());
        assertEquals(newRaffle.getPlayers().getFirst().getName(), listElements.getFirst().text());
    }

    @Test
    void openRaffleResultLineIsHidden() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(false)
                .winner(null)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element resultInfo = doc.selectFirst("#raffle-result-info");
        assertNotNull(resultInfo);
        assertTrue(resultInfo.hasAttr("hidden"));
    }

    @Test
    void canceledRaffleResultInfoIsHidden() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(true)
                .winner(null)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element resultInfo = doc.selectFirst("#raffle-result-info");
        assertNotNull(resultInfo);
        assertTrue(resultInfo.hasAttr("hidden"));
    }

    @Test
    void adminUserClosedRaffle() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(true)
                .winner(normalUser)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element resultInfo = doc.selectFirst("#raffle-result-info");
        assertNotNull(resultInfo);
        assertFalse(resultInfo.hasAttr("hidden"));

        Element adminResultInfo = resultInfo.selectFirst(".form-control");
        assertNotNull(adminResultInfo);
        assertEquals("The winner is: " + newRaffle.getWinner().getUserName(), adminResultInfo.text());
    }

    @Test
    void normalUserClosedRaffleNotWon() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(true)
                .winner(adminUser)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element resultInfo = doc.selectFirst("#raffle-result-info");
        assertNotNull(resultInfo);
        assertFalse(resultInfo.hasAttr("hidden"));

        Elements normalResultInfos = resultInfo.select(".form-control");
        assertNotNull(normalResultInfos);
        assertEquals(2, normalResultInfos.size());

        Element first = normalResultInfos.getFirst();
        assertEquals("Unfortunately, you did not win this time.", first.text());
        assertFalse(first.hasAttr("hidden"));

        Element last = normalResultInfos.getLast();
        assertEquals("Congratulations, you WON!", last.text());
        assertTrue(last.hasAttr("hidden"));
    }

    @Test
    void normalUserClosedRaffleWon() throws Exception {
        Raffle newRaffle = raffleRepository.save(Raffle.builder()
                .name("NewRaffle")
                .players(List.of(normalUser))
                .closed(true)
                .winner(normalUser)
                .prize(prizeOne)
                .build());
        long raffleId = newRaffle.getId();
        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffleId)
                        .with(getNormalUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Element resultInfo = doc.selectFirst("#raffle-result-info");
        assertNotNull(resultInfo);
        assertFalse(resultInfo.hasAttr("hidden"));

        Elements normalResultInfos = resultInfo.select(".form-control");
        assertNotNull(normalResultInfos);
        assertEquals(2, normalResultInfos.size());

        Element first = normalResultInfos.getFirst();
        assertEquals("Unfortunately, you did not win this time.", first.text());
        assertTrue(first.hasAttr("hidden"));

        Element last = normalResultInfos.getLast();
        assertEquals("Congratulations, you WON!", last.text());
        assertFalse(last.hasAttr("hidden"));
    }

    private void createUsers() {
        this.adminUser = userRepository.save(RaffleUser.builder()
                .userName(ADMIN_USERNAME)
                .password("")
                .name("Admin")
                .emailAddress("admin@test.eu")
                .roles(Set.of("ADMIN", "USER"))
                .build());
        this.normalUser = userRepository.save(RaffleUser.builder()
                .userName(USER_USERNAME)
                .password("")
                .name("User")
                .roles(Set.of("USER"))
                .emailAddress("user@test.eu")
                .build());
        this.userStore = Map.of(ADMIN_USERNAME, this.adminUser, USER_USERNAME, this.normalUser);
    }

    private void createPrizes() {
        this.prizeOne = prizeRepository.save(Prize.builder()
                .name("PrizeOne")
                .category("TheCategory")
                .build());
        this.prizeTwo = prizeRepository.save(Prize.builder()
                .name("PrizeTwo")
                .category("TheCategory")
                .build());
    }

    @NonNull
    private static UserRequestPostProcessor getAdminUser() {
        return user(ADMIN_USERNAME).roles("USER", "ADMIN");
    }

    @NonNull
    private static UserRequestPostProcessor getNormalUser() {
        return user(USER_USERNAME).roles("USER");
    }
}
