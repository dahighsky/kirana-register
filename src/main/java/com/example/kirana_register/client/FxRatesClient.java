package com.example.kirana_register.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Component
public class FxRatesClient {

    private final WebClient webClient;
    private static final String FX_RATES_API_URL = "https://api.fxratesapi.com/latest";

    public FxRatesClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(FX_RATES_API_URL)
                .build();
    }

    public Map<String, BigDecimal> getExchangeRates() {
        try {
            return webClient.get()
                    .retrieve()
                    .bodyToMono(ExchangeRateResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .map(ExchangeRateResponse::rates)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Failed to retrieve exchange rates", e);
        }
    }

    private record ExchangeRateResponse(Map<String, BigDecimal> rates) {}
}
