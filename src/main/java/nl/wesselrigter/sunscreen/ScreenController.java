package nl.wesselrigter.sunscreen;

import org.springframework.web.bind.annotation.RestController;

import nl.wesselrigter.sunscreen.screencontrol.StatusMonitor;
import nl.wesselrigter.sunscreen.screencontrol.SunscreenClient;
import nl.wesselrigter.sunscreen.screencontrol.SunscreenManager;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class ScreenController {

    private final SunscreenManager sunscreenManager;
    private final SunscreenClient sunscreenClient;
    private final StatusMonitor statusMonitor;

    public ScreenController(SunscreenManager sunscreenManager, StatusMonitor statusMonitor,
            SunscreenClient sunscreenClient) {
        this.sunscreenClient = sunscreenClient;
        this.statusMonitor = statusMonitor;
        this.sunscreenManager = sunscreenManager;
    }

    @GetMapping("/t")
    public void Test() {
        sunscreenManager.checkWeather();
    }

    @GetMapping("/status")
    public Map<String, Boolean> getStatus() {
        return statusMonitor.getStatus();
    }

    @PostMapping("/set/{value}")
    public void postMethodName(@RequestParam int value) {

        sunscreenClient.setSunscreen(value);
    }

}
