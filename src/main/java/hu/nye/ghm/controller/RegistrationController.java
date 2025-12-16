package hu.nye.ghm.controller;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.service.RafflePlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final RafflePlayerService rafflePlayerService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("raffleUser", new RaffleUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RaffleUser raffleUser, Model model) {
        raffleUser.setPassword(passwordEncoder.encode(raffleUser.getPassword()));
        raffleUser.setRoles(Set.of("USER"));

        if (!rafflePlayerService.registerNewUser(raffleUser)) {
            model.addAttribute("error", "Username or email already exists. Please choose another.");
            return "register";
        } else {
            return "redirect:/login?registration_successful";
        }
    }
}

