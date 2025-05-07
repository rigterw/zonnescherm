package nl.wesselrigter.sunscreen.screencontrol;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SunscreenClient {

    private final StatusMonitor statusMonitor;
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${tahoma.api.key}")
    private String apiKey;
    @Value("${tahoma.device.url}")
    private String deviceUrl;
    @Value("${tahoma.hub.pin}")
    private String hubPin;
    @Value("${tahoma.hub.port}")
    private String hubPort;

    private boolean screenOpen = false;

    RestClient restClient;

    SunscreenClient(StatusMonitor statusMonitor, RestClient.Builder webClientBuilder) {
        this.restClient = webClientBuilder
                .baseUrl("https://gateway-" + hubPin + ".local:" + hubPort + "/enduser-mobile-web/1/enduserAPI")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.statusMonitor = statusMonitor;
    }

    // TODO: test
    public boolean isSunscreenOpen() {
        List<Map<String, Object>> response = restClient.get().uri("/setup/devices/" + deviceUrl + "/states").retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
        screenOpen = "open".equals(response.get(5).get("value"));

        return screenOpen;
    }

    public void setSunscreen(int value) {
        if (value > 100) {
            value = 100;
        } else if (value < 0) {
            value = 0;
        }

        try {

            statusMonitor.sunScreen = true;
        } catch (Exception e) {
            logger.error("Error setting sunscreen: ", e.getMessage());
            statusMonitor.sunScreen = false;
        }
    }

    public void CloseSunscreen() {
        setSunscreen(0);
    }
}
