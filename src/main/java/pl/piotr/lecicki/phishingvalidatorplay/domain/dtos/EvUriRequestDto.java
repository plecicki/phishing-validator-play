package pl.piotr.lecicki.phishingvalidatorplay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ThreatType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EvUriRequestDto {

    private String uri;
    private ThreatType[] threatTypes;
    private Boolean allowScan;
}
