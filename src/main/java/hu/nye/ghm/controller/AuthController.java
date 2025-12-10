package hu.nye.ghm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * A felhasználó azonosítására használt Controller.
 */
@Controller
public class AuthController {
    /**
     * A bejelentkezéshez használt Template nevét visszaadó metódus.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * Beállítja, hogy az alap URL melyik Template-et állítsa be
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/raffleview")
    public String raffleview() {
        return "raffleview";
    }
}
