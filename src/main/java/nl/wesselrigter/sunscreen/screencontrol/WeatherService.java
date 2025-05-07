package nl.wesselrigter.sunscreen.screencontrol;

import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    private final RestClient restClient;
    private List<Boolean> rainForecast;

    private final StatusMonitor statusMonitor;
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.lat}")
    private float lat;
    @Value("${weather.lon}")
    private float lon;

    WeatherService(StatusMonitor statusMonitor) {
        this.statusMonitor = statusMonitor;
        this.restClient = RestClient.create();
    }

    public Boolean willItRain(Boolean trueOnFailure) {
        String url = "https://gadgedts.buienradar.nl/data/raintext?lat={lat}&lon={lon}";

        try {

            String response = restClient.get().uri(url, Map.of("lat", lat, "lon", lon)).retrieve().body(String.class);

            rainForecast = parseResponse(response);
            statusMonitor.rainApi = true;
        } catch (Exception e) {
            logger.error("Error fetching rain data: ", e.getMessage());
            statusMonitor.rainApi = false;
        }

        if (rainForecast.size() == 0) {
            return trueOnFailure;
        }
        // By using a list, we can continue checking even if the api fails
        return rainForecast.removeFirst();
    }

    private List<Boolean> parseResponse(String response) {
        String[] lines = response.split("\n");
        List<Boolean> rainForecast = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split("|");
            rainForecast.add(!parts[0].equals("000"));
        }

        return rainForecast;
    }
}
