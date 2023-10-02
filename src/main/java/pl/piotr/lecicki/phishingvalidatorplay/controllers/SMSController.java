package pl.piotr.lecicki.phishingvalidatorplay.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.services.SMSService;

@RestController
@RequestMapping("/v1/sms")
@RequiredArgsConstructor
public class SMSController {

    private final SMSService smsService;

    @Transactional
    @PostMapping
    public ResponseEntity<Boolean> sendSMS(@RequestBody SMSDto smsDto) throws Exception {
        return ResponseEntity.ok(smsService.sendSMS(smsDto));
    }
}
