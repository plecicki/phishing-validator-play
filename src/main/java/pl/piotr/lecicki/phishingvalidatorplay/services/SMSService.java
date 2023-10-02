package pl.piotr.lecicki.phishingvalidatorplay.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.piotr.lecicki.phishingvalidatorplay.clients.GoogleCloudClient;
import pl.piotr.lecicki.phishingvalidatorplay.config.SMSConfig;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.EvUriRequestDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.EvUriResponseDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.Score;
import pl.piotr.lecicki.phishingvalidatorplay.domain.entities.PhishingValidatorUsers;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ConfidenceLevel;
import pl.piotr.lecicki.phishingvalidatorplay.domain.enums.ThreatType;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.MessageContainsDangerousLink;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.PhishingProtectionIsAlreadyWorking;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.ThisSenderDoesntUsePhishingProtection;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.WrongConfigMessage;
import pl.piotr.lecicki.phishingvalidatorplay.mappers.SMSMapper;
import pl.piotr.lecicki.phishingvalidatorplay.repositories.PhishingValidatorRepository;
import pl.piotr.lecicki.phishingvalidatorplay.repositories.SMSRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SMSService {

    private final SMSRepository smsRepository;
    private final SMSConfig smsConfig;
    private final SMSMapper smsMapper;
    private final GoogleCloudClient googleCloudClient;
    private final PhishingValidatorRepository phishingValidatorRepository;

    private final ThreatType[] ALL_THREAT_TYPES = {
            ThreatType.THREAT_TYPE_UNSPECIFIED,
            ThreatType.SOCIAL_ENGINEERING,
            ThreatType.MALWARE,
            ThreatType.UNWANTED_SOFTWARE };
    private final ConfidenceLevel[] SAVE_HIGH_CONFIDENCE_LEVELS = {
            ConfidenceLevel.EXTREMELY_HIGH,
            ConfidenceLevel.VERY_HIGH,
            ConfidenceLevel.HIGHER,
            ConfidenceLevel.HIGH,
            ConfidenceLevel.SAFE
    };
    private final List<ConfidenceLevel> SAVE_HIGH_CONFIDENCE_LEVEL_LIST =
            Arrays.asList(SAVE_HIGH_CONFIDENCE_LEVELS);

    public Boolean sendSMS(SMSDto smsDto) throws Exception{
        boolean isRecipientConfigNumber = smsDto.getRecipient().equals(smsConfig.getNumber());
        if (isRecipientConfigNumber) {
            return stopOrStartPhishingProtection(smsDto);
        }

        boolean isRecipientProtectedFromPhishing =
                phishingValidatorRepository.existsByPhoneNumber(smsDto.getRecipient());
        if (isRecipientProtectedFromPhishing) {
            checkIfMessageIsSafe(smsDto);
        }
        smsRepository.save(smsMapper.mapSMSDtoToSMSes(smsDto));
        return true;
    }

    private void checkIfMessageIsSafe(SMSDto smsDto) throws MessageContainsDangerousLink {
        List<String> urisInMessage = extractUris(smsDto.getMessage());
        boolean isDangerousUriInMessage = false;
        for (String uri : urisInMessage) {
            EvUriRequestDto evUriRequestDto = new EvUriRequestDto(
                    uri,
                    ALL_THREAT_TYPES,
                    true
            );
            EvUriResponseDto evUriResponseDto = googleCloudClient.evaluateUri(evUriRequestDto);
            for (Score score : evUriResponseDto.getScores()) {
                if (!SAVE_HIGH_CONFIDENCE_LEVEL_LIST.contains(score.getConfidenceLevel())) {
                    isDangerousUriInMessage = true;
                }
            }
            if (isDangerousUriInMessage) throw new MessageContainsDangerousLink();
        }
    }

    private Boolean stopOrStartPhishingProtection(SMSDto smsDto) throws PhishingProtectionIsAlreadyWorking, ThisSenderDoesntUsePhishingProtection, WrongConfigMessage {
        boolean isStartPhishingProtectionMessage =
                smsDto.getMessage().toUpperCase().equals(smsConfig.getStartPhishingProtectMess());
        boolean isStopPhishingProtectionMessage =
                smsDto.getMessage().toUpperCase().equals(smsConfig.getStopPhishingProtectMess());

        if (isStartPhishingProtectionMessage) {
            return startPhishingProtection(smsDto.getSender());
        } else if (isStopPhishingProtectionMessage) {
            return stopPhishingProtection(smsDto.getSender());
        } else {
            throw new WrongConfigMessage();
        }
    }

    private boolean startPhishingProtection(String sender) throws PhishingProtectionIsAlreadyWorking {
        if (!phishingValidatorRepository.existsByPhoneNumber(sender)) {
            phishingValidatorRepository.save(new PhishingValidatorUsers(0L, sender));
            return true;
        }
        throw new PhishingProtectionIsAlreadyWorking();
    }

    private boolean stopPhishingProtection(String sender) throws ThisSenderDoesntUsePhishingProtection {
        if (phishingValidatorRepository.existsByPhoneNumber(sender)) {
            phishingValidatorRepository.deleteByPhoneNumber(sender);
            return true;
        }
        throw new ThisSenderDoesntUsePhishingProtection();
    }

    public static List<String> extractUris(String message)
    {
        List<String> containedUris = new ArrayList<String>();
        String uriRegex = "((https?|ftp|gopher|telnet|file):(?://|\\\\\\\\\\\\\\\\)+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(uriRegex, Pattern.CASE_INSENSITIVE);
        Matcher uriMatcher = pattern.matcher(message);

        while (uriMatcher.find())
        {
            containedUris.add(message.substring(uriMatcher.start(0),
                    uriMatcher.end(0)));
        }

        return containedUris;
    }
}
