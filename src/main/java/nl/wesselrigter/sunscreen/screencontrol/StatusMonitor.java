package nl.wesselrigter.sunscreen.screencontrol;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class StatusMonitor {

    boolean rainApi = false;
    boolean sunScreen = false;
    boolean sunsetApi = false;

    public Map<String, Boolean> getStatus() {
        return Map.of(
                "rainApi", rainApi,
                "sunScreen", sunScreen,
                "sunsetApi", sunsetApi);
    }
}
