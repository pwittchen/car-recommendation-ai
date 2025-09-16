package com.github.pwittchen.backend.controller;

import com.github.pwittchen.backend.model.CarPreferences;
import com.github.pwittchen.backend.model.CarRecommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/car")
public class CarRecommendationController {

    private final static Logger LOG = LoggerFactory.getLogger(CarRecommendationController.class);
    private final ChatClient chat;

    public CarRecommendationController(ChatClient.Builder chat) {
        this.chat = chat.build();
    }

    @GetMapping("recommend")
    public Mono<CarRecommendation> recommend(
            @RequestParam(defaultValue = "true") boolean isNew,
            @RequestParam(defaultValue = "0") int budget,
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "petrol") String fuel,
            @RequestParam(defaultValue = "") String brand,
            @RequestParam(defaultValue = "") String comment
    ) {
        LOG.info("GOT recommendation request");
        return chat
                .prompt(createPrompt(new CarPreferences(isNew, budget, type, fuel, brand, comment)))
                .stream()
                .content()
                .reduce(new StringBuilder(), StringBuilder::append)
                .map(StringBuilder::toString)
                .map(CarRecommendation::new);
    }

    private String createPrompt(final CarPreferences preferences) {
        return "You are the car advisor." +
                "Having the following information about the user car preferences:" +
                (preferences.isNew() ? "- new car" : "- used car") +
                "- max budget: " + preferences.budget() + " PLN" +
                "- type: " + preferences.type() +
                "- fuel: " + preferences.fuel() +
                "- brand: " + preferences.brand() +
                " and user additional comment: " + preferences.comment() +
                "recommend the best car you know, which meets provided criteria." +
                "Give only the car brand and name with a short description in one sentence.";
    }
}
