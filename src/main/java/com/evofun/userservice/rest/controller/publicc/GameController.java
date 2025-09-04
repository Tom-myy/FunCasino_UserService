package com.evofun.userservice.rest.controller.publicc;

import com.evofun.userservice.rest.dto.response.GameAccessResponse;
import com.evofun.userservice.rest.exception.ServiceUnavailable;
import com.evofun.userservice.rest.forGame.GameServiceClient;
import com.evofun.userservice.security.jwt.JwtUser;
import com.evofun.userservice.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameServiceClient gameServiceClient;
    private final JwtUtil jwtUtil;

    public GameController(GameServiceClient gameServiceClient, JwtUtil jwtUtil) {
        this.gameServiceClient = gameServiceClient;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/enter")
    public ResponseEntity<?> getAccessToGame(@AuthenticationPrincipal JwtUser principal) {
        if (!gameServiceClient.isGameServiceAlive()) {
            throw new ServiceUnavailable(
                    "Game service is unavailable.",
                    "Some service is temporarily unavailable on the server."
            );
        }

        String wsLink = gameServiceClient.getWsLinkForGame();
        String gameToken = jwtUtil.generateGameToken(principal);

        GameAccessResponse response = new GameAccessResponse(gameToken, wsLink);

        return ResponseEntity.ok(response);
    }

}
