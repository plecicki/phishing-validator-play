package pl.piotr.lecicki.phishingvalidatorplay.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoogleCloudConfig {

    @Value("${google.cloud.endpoint}")
    private String endpoint;

    @Value("${google.cloud.version}")
    private String version;

    @Value("${google.cloud.evaluate}")
    private String evaluate;

    @Value("${google.cloud.token}")
    private String token;
}
