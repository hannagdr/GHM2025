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
import java.util.stream.StreamSupport;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RaffleControllerIT {
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
    private Prize prizeOne;
    private Prize prizeTwo;

    @BeforeEach
    void setup() {
        createUsers();
        createPrizes();
    }

    @Test
    void newRaffle() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/raffle")
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements title = doc.select("h2:contains(Create new raffle)");
        assertFalse(title.isEmpty());

        Elements optionOne = doc.select("select:contains(" + prizeOne.getName() + ")");
        assertFalse(optionOne.isEmpty());
        Elements optionTwo = doc.select("select:contains(" + prizeTwo.getName() + ")");
        assertFalse(optionTwo.isEmpty());
        Elements saveButton = doc.select("button:contains(Create)");
        assertFalse(saveButton.isEmpty());
    }

    @Test
    void modifyRaffle() throws Exception {
        Raffle raffle = raffleRepository.save(Raffle.builder()
                .name("Raffle")
                .prize(prizeTwo)
                .build());

        MvcResult mvcResult = mockMvc.perform(get("/raffle/" + raffle.getId() + "/edit")
                        .with(getAdminUser()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        Document doc = Jsoup.parse(mvcResult.getResponse().getContentAsString());
        Elements title = doc.select("h2:contains(Edit raffle)");
        assertFalse(title.isEmpty());

        Elements optionOne = doc.select("select:contains(" + prizeOne.getName() + ")");
        assertFalse(optionOne.isEmpty());
        Elements optionTwo = doc.select("select:contains(" + prizeTwo.getName() + ")");
        assertFalse(optionTwo.isEmpty());
        Elements saveButton = doc.select("button:contains(Save)");
        assertFalse(saveButton.isEmpty());
    }

    @Test
    void createNewRaffle() throws Exception {
        String newRaffleName = "NewRaffle";
        mockMvc.perform(post("/raffle")
                        .with(csrf())
                        .param("name", newRaffleName)
                        .param("prizeId", prizeTwo.getId().toString())
                        .with(getAdminUser()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(StreamSupport.stream(raffleRepository.findAll().spliterator(), false)
                .anyMatch(raffle -> raffle.getName().equals(newRaffleName)));
    }

    @Test
    void updateExistingRaffle() throws Exception {
        String oldRaffleName = "OldRaffleName";
        String newRaffleName = "NewRaffle";
        Raffle existingRaffle = raffleRepository.save(Raffle.builder()
                .name(oldRaffleName)
                .prize(prizeOne)
                .build());
        long raffleId = existingRaffle.getId();
        mockMvc.perform(post("/raffle")
                        .with(csrf())
                        .param("id", Long.toString(raffleId))
                        .param("name", newRaffleName)
                        .param("prizeId", Long.toString(prizeTwo.getId()))
                        .with(getAdminUser()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(raffleRepository.findById(raffleId)
                .filter(raffle -> prizeTwo.getId().equals(raffle.getPrize().getId()))
                .filter(raffle -> newRaffleName.equals(raffle.getName()))
                .isPresent());
    }

    @Test
    void drawRaffle() throws Exception {
        String raffleName = "NewRaffle";
        Raffle raffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .prize(prizeOne)
                .players(List.of(normalUser, adminUser))
                .build());
        long raffleId = raffle.getId();
        String referer = "TestReferer";
        mockMvc.perform(post("/raffle/" + raffleId + "/draw")
                        .with(csrf())
                        .with(getAdminUser())
                        .header("Referer", referer))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + referer));

        assertTrue(raffleRepository.findById(raffleId)
                .filter(Raffle::isClosed)
                .filter(raf -> raf.getWinner() != null)
                .isPresent());
    }

    @Test
    void applyRaffle() throws Exception {
        String raffleName = "NewRaffle";
        Raffle raffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .prize(prizeOne)
                .build());
        long raffleId = raffle.getId();
        String referer = "TestReferer";
        mockMvc.perform(post("/raffle/" + raffleId + "/apply")
                        .with(csrf())
                        .with(getAdminUser())
                        .header("Referer", referer))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + referer));

        assertTrue(raffleRepository.findById(raffleId)
                .filter(not(Raffle::isClosed))
                .filter(raf -> !raf.getPlayers().isEmpty())
                .isPresent());
    }

    @Test
    void closeRaffle() throws Exception {
        String raffleName = "NewRaffle";
        Raffle raffle = raffleRepository.save(Raffle.builder()
                .name(raffleName)
                .prize(prizeOne)
                .build());
        long raffleId = raffle.getId();
        String referer = "TestReferer";
        mockMvc.perform(post("/raffle/" + raffleId + "/close")
                        .with(csrf())
                        .with(getAdminUser())
                        .header("Referer", referer))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + referer));

        assertTrue(raffleRepository.findById(raffleId)
                .filter(Raffle::isClosed)
                .isPresent());
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
        return user("admin").roles("USER", "ADMIN");
    }

    @NonNull
    private static UserRequestPostProcessor getNormalUser() {
        return user("user").roles("USER");
    }
}