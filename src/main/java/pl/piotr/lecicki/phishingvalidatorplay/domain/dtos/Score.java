package pl.piotr.lecicki.phishingvalidatorplay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ConfidenceLevel;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ThreatType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Score {

    private ThreatType threatType;
    private ConfidenceLevel confidenceLevel;
}
