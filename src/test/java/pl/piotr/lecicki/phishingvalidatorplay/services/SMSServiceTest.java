package pl.piotr.lecicki.phishingvalidatorplay.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.piotr.lecicki.phishingvalidatorplay.clients.GoogleCloudClient;
import pl.piotr.lecicki.phishingvalidatorplay.config.SMSConfig;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.EvUriResponseDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.Score;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.SMSes;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ConfidenceLevel;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ThreatType;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.MessageContainsDangerousLink;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.PhishingProtectionIsAlreadyWorking;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.ThisSenderDoesntUsePhishingProtection;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.WrongConfigMessage;
import pl.piotr.lecicki.phishingvalidatorplay.mappers.SMSMapper;
import pl.piotr.lecicki.phishingvalidatorplay.repositories.PhishingValidatorRepository;
import pl.piotr.lecicki.phishingvalidatorplay.repositories.SMSRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SMSServiceTest {

    @InjectMocks
    private SMSService smsService;

    @Mock
    private SMSConfig smsConfig;

    @Mock
    private PhishingValidatorRepository phishingValidatorRepository;

    @Mock
    private SMSRepository smsRepository;

    @Mock
    private GoogleCloudClient googleCloudClient;

    @Mock
    private SMSMapper smsMapper;

    private final ThreatType[] ALL_THREAT_TYPES = {
            ThreatType.THREAT_TYPE_UNSPECIFIED,
            ThreatType.SOCIAL_ENGINEERING,
            ThreatType.MALWARE,
            ThreatType.UNWANTED_SOFTWARE };

    @Test
    void sendSaveMessage() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","987654321",
                "Look at this Google link https://www.google.pl/"
        );
        Score scores[] = {
                new Score(ThreatType.THREAT_TYPE_UNSPECIFIED, ConfidenceLevel.SAFE),
                new Score(ThreatType.MALWARE, ConfidenceLevel.SAFE),
                new Score(ThreatType.UNWANTED_SOFTWARE, ConfidenceLevel.SAFE),
                new Score(ThreatType.SOCIAL_ENGINEERING, ConfidenceLevel.SAFE)
        };
        EvUriResponseDto evUriResponseDto = new EvUriResponseDto(scores);
        SMSes sms = new SMSes(0L, "123456789","987654321",
                "Look at this Google link https://www.google.pl/");

        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getRecipient())).thenReturn(true);
        when(googleCloudClient.evaluateUri(any())).thenReturn(evUriResponseDto);
        when(smsMapper.mapSMSDtoToSMSes(smsDto)).thenReturn(sms);
        when(smsConfig.getNumber()).thenReturn("1234");

        //When
        Boolean result = smsService.sendSMS(smsDto);

        //Then
        Assertions.assertTrue(result);

        //CleanUp
        //Do nothing
    }

    @Test
    void sendUnsaveMessage() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","987654321",
                "Look at this Google link https://www.gaagle.pl/"
        );
        Score scores[] = {
                new Score(ThreatType.THREAT_TYPE_UNSPECIFIED, ConfidenceLevel.SAFE),
                new Score(ThreatType.MALWARE, ConfidenceLevel.SAFE),
                new Score(ThreatType.UNWANTED_SOFTWARE, ConfidenceLevel.LOW),
                new Score(ThreatType.SOCIAL_ENGINEERING, ConfidenceLevel.SAFE)
        };
        EvUriResponseDto evUriResponseDto = new EvUriResponseDto(scores);
        SMSes sms = new SMSes(0L, "123456789","987654321",
                "Look at this Google link https://www.gaagle.pl/");

        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getRecipient())).thenReturn(true);
        when(googleCloudClient.evaluateUri(any())).thenReturn(evUriResponseDto);
        when(smsConfig.getNumber()).thenReturn("1234");

        //When & Then
        Assertions.assertThrows(
                MessageContainsDangerousLink.class,
                () -> smsService.sendSMS(smsDto),
                "Expected sendSMS() to throw, but it didn't"
        );

        //CleanUp
        //Do nothing
    }

    @Test
    void sendConfigMessage_StartProtection() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","1234",
                "START"
        );

        when(smsConfig.getNumber()).thenReturn("1234");
        when(smsConfig.getStartPhishingProtectMess()).thenReturn("START");
        when(smsConfig.getStopPhishingProtectMess()).thenReturn("STOP");
        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getSender())).thenReturn(false);

        //When
        Boolean result = smsService.sendSMS(smsDto);

        //Then
        Assertions.assertTrue(result);

        //CleanUp
        //Do nothing
    }

    @Test
    void sendConfigMessage_StartProtection_IsAlreadyWorking() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","1234",
                "START"
        );

        when(smsConfig.getNumber()).thenReturn("1234");
        when(smsConfig.getStartPhishingProtectMess()).thenReturn("START");
        when(smsConfig.getStopPhishingProtectMess()).thenReturn("STOP");
        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getSender())).thenReturn(true);

        //When & Then
        Assertions.assertThrows(
                PhishingProtectionIsAlreadyWorking.class,
                () -> smsService.sendSMS(smsDto),
                "Expected sendSMS() to throw, but it didn't"
        );

        //CleanUp
        //Do nothing
    }

    @Test
    void sendConfigMessage_StopProtection() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","1234",
                "STOP"
        );

        when(smsConfig.getNumber()).thenReturn("1234");
        when(smsConfig.getStartPhishingProtectMess()).thenReturn("START");
        when(smsConfig.getStopPhishingProtectMess()).thenReturn("STOP");
        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getSender())).thenReturn(true);

        //When
        Boolean result = smsService.sendSMS(smsDto);

        //Then
        Assertions.assertTrue(result);

        //CleanUp
        //Do nothing
    }

    @Test
    void sendConfigMessage_StopProtection_LackOfPhishingProtection() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","1234",
                "STOP"
        );

        when(smsConfig.getNumber()).thenReturn("1234");
        when(smsConfig.getStartPhishingProtectMess()).thenReturn("START");
        when(smsConfig.getStopPhishingProtectMess()).thenReturn("STOP");
        when(phishingValidatorRepository.existsByPhoneNumber(smsDto.getSender())).thenReturn(false);

        //When & Then
        Assertions.assertThrows(
                ThisSenderDoesntUsePhishingProtection.class,
                () -> smsService.sendSMS(smsDto),
                "Expected sendSMS() to throw, but it didn't"
        );

        //CleanUp
        //Do nothing
    }

    @Test
    void sendConfigMessage_WrongMessage() {
        //Given
        SMSDto smsDto = new SMSDto(
                "123456788","1234",
                "ST OP"
        );

        when(smsConfig.getNumber()).thenReturn("1234");
        when(smsConfig.getStartPhishingProtectMess()).thenReturn("START");
        when(smsConfig.getStopPhishingProtectMess()).thenReturn("STOP");

        //When & Then
        Assertions.assertThrows(
                WrongConfigMessage.class,
                () -> smsService.sendSMS(smsDto),
                "Expected sendSMS() to throw, but it didn't"
        );

        //CleanUp
        //Do nothing
    }
}
