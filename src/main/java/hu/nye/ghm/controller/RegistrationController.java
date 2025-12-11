package hu.nye.ghm.controller;

import hu.nye.ghm.domain.RaffleUser;
import hu.nye.ghm.repository.RaffleUserRepository;
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
    private final RaffleUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("raffleUser", new RaffleUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RaffleUser raffleUser, Model model) {
        // Check for duplicate username
        if (userRepository.findByUserName(raffleUser.getUserName()).isPresent()) {
            model.addAttribute("error", "Username already exists. Please choose another.");
            return "register";
        }

        // Check for duplicate email
        if (userRepository.findByEmailAddress(raffleUser.getEmailAddress()).isPresent()) {
            model.addAttribute("error", "Email address already registered. Please use another.");
            return "register";
        }

        raffleUser.setPassword(passwordEncoder.encode(raffleUser.getPassword()));
        raffleUser.setRoles(Set.of("USER"));

        userRepository.save(raffleUser);

        return "redirect:/login?success";
    }
}

