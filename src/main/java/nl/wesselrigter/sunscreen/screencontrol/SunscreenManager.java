package nl.wesselrigter.sunscreen.screencontrol;

import org.springframework.stereotype.Component;

@Component
public class SunscreenManager {

    private final WeatherService weatherService;
    private final SunscreenClient SunscreenClient;

    private boolean closeOnFailure = false;

    SunscreenManager(WeatherService weatherService, SunscreenClient sunscreenClient) {
        this.weatherService = weatherService;
        this.SunscreenClient = sunscreenClient;
    }

    public void checkWeather() {
        if (weatherService.willItRain(closeOnFailure)) {
            SunscreenClient.CloseSunscreen();
        } else {
            if (!SunscreenClient.isSunscreenOpen()) {
                SunscreenClient.setSunscreen(100);
            }
        }
    }
}
