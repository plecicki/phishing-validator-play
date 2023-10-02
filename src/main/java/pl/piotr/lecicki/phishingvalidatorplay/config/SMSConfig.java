package pl.piotr.lecicki.phishingvalidatorplay.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SMSConfig {

    @Value("${sms.config.number}")
    private String number;

    @Value("${sms.config.phishing.protection.start.message}")
    private String startPhishingProtectMess;

    @Value("${sms.config.phishing.protection.stop.message}")
    private String stopPhishingProtectMess;
}
