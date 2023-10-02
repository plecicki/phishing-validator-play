package pl.piotr.lecicki.phishingvalidatorplay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SMSDto {
    private String sender;
    private String recipient;
    private String message;
}
