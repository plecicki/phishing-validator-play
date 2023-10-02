package pl.piotr.lecicki.phishingvalidatorplay.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.piotr.lecicki.phishingvalidatorplay.config.GoogleCloudConfig;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.EvUriRequestDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.EvUriResponseDto;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class GoogleCloudClient {

    private final RestTemplate restTemplate;
    private final GoogleCloudConfig googleCloudConfig;

    public EvUriResponseDto evaluateUri(EvUriRequestDto evUriRequestDto) {
        URI uri = getEvaluateUriAddress();

        return restTemplate.postForObject(
                uri, evUriRequestDto, EvUriResponseDto.class
        );
    }

    private URI getEvaluateUriAddress() {
        return UriComponentsBuilder.fromHttpUrl(
                googleCloudConfig.getEndpoint() + googleCloudConfig.getVersion() + googleCloudConfig.getEvaluate()
        )
                .queryParam("token", googleCloudConfig.getToken())
                .build()
                .encode()
                .toUri();
    }
}
