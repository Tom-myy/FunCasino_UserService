package com.evofun.userservice.rest.forGame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GameServiceClient {

    @Value("${game-service.base-url}")
    private String baseGameServiceUrl;

    public boolean isGameServiceAlive() {
        try {
            //TODO put port through file (application-dev.yml)
            WebClient client = WebClient.create(baseGameServiceUrl);

            client.get()//TODO understand...
                    .uri("/actuator/health")
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getWsLinkForGame() {
        //TODO put port through file (application-dev.yml)
        WebClient client = WebClient.create(baseGameServiceUrl);

        return client.get()
                .uri("/api/game-service/gameAccess")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
