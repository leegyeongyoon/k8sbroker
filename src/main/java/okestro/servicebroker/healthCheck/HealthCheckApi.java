package okestro.servicebroker.healthCheck;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckApi {
    @GetMapping("/healthcheck")
    private String Hello(){
        return "Hello, I'm alive.";
    }
}
