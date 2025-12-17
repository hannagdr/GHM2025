package hu.nye.ghm.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    @DisplayName("Login controller")
    void login() {
        assertEquals("login", new AuthController().login());
    }
}