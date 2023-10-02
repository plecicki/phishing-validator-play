package pl.piotr.lecicki.phishingvalidatorplay.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.SMSes;

public class SMSMapperTest {

    @Test
    void testMapSMSDtoToSMSes() {
        //Given
        SMSMapper smsMapper = new SMSMapper();
        SMSDto smsDto = new SMSDto(
                "123456789","987654321","AaAaA"
        );

        //When
        SMSes sms = smsMapper.mapSMSDtoToSMSes(smsDto);

        //Then
        Assertions.assertEquals(0L, sms.getId());
        Assertions.assertEquals("123456789", sms.getSender());
        Assertions.assertEquals("987654321", sms.getRecipient());
        Assertions.assertEquals("AaAaA", sms.getMessage());
    }
}
