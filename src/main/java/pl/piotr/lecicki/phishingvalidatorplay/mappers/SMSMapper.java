package pl.piotr.lecicki.phishingvalidatorplay.mappers;

import org.springframework.stereotype.Service;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.SMSes;

@Service
public class SMSMapper {

    public SMSes mapSMSDtoToSMSes(SMSDto smsDto) {
        return new SMSes(
                0L,
                smsDto.getSender(),
                smsDto.getRecipient(),
                smsDto.getMessage()
        );
    }
}
