package pl.piotr.lecicki.phishingvalidatorplay.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.MessageContainsDangerousLink;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.PhishingProtectionIsAlreadyWorking;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.ThisSenderDoesntUsePhishingProtection;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.WrongConfigMessage;

@ControllerAdvice
public class GlobalHttpExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MessageContainsDangerousLink.class)
    public ResponseEntity<Object> handleMessageContainsDangerousLink(MessageContainsDangerousLink exception) {
        return new ResponseEntity<>("Message contains at least one dangerous link", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PhishingProtectionIsAlreadyWorking.class)
    public ResponseEntity<Object> handlePhishingProtectionIsAlreadyWorking(PhishingProtectionIsAlreadyWorking exception) {
        return new ResponseEntity<>("This sender is already using this protection", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ThisSenderDoesntUsePhishingProtection.class)
    public ResponseEntity<Object> handleThisRecipientDoesntUsePhishingProtection(ThisSenderDoesntUsePhishingProtection exception) {
        return new ResponseEntity<>("This sender doesn't use phishing protection so it is impossible to delete him", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongConfigMessage.class)
    public ResponseEntity<Object> handleWrongConfigMessage(WrongConfigMessage exception) {
        return new ResponseEntity<>("Wrong config message", HttpStatus.BAD_REQUEST);
    }
}
