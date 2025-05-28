package com.evofun.userservice.rest.controller.publicc;

import com.evofun.userservice.rest.dto.response.GameAccessResponse;
import com.evofun.userservice.rest.forGame.GameConnection;
import com.evofun.userservice.security.jwt.JwtUser;
import com.evofun.userservice.security.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

//@Tag(name = "methods_1")
@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameConnection gameConnection;
    private final JwtUtil jwtUtil;

    public GameController(GameConnection gameConnection, JwtUtil jwtUtil) {
        this.gameConnection = gameConnection;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/enter")
    public ResponseEntity<?> getAccessToGame(@AuthenticationPrincipal JwtUser principal) {
        if (!gameConnection.isGameServiceAlive()) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Game service is temporarily unavailable");
        }

        String wsLink = gameConnection.getWsLinkForGame();
        String gameToken = jwtUtil.generateGameToken(principal);

        GameAccessResponse response = new GameAccessResponse(gameToken, wsLink);
/*
        try {
            GameTokenResponse token = gameService.startSessionFor(userId);
            return ResponseEntity.ok(token);
        } catch (SomeGameException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start game");
        }*/


        return ResponseEntity.ok(response);
    }

}
