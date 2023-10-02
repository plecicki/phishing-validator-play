package pl.piotr.lecicki.phishingvalidatorplay.controllers;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.piotr.lecicki.phishingvalidatorplay.config.SMSConfig;
import pl.piotr.lecicki.phishingvalidatorplay.domain.dtos.SMSDto;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.MessageContainsDangerousLink;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.PhishingProtectionIsAlreadyWorking;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.ThisSenderDoesntUsePhishingProtection;
import pl.piotr.lecicki.phishingvalidatorplay.exceptions.WrongConfigMessage;
import pl.piotr.lecicki.phishingvalidatorplay.services.SMSService;

import static org.mockito.Mockito.when;

@SpringJUnitWebConfig
@WebMvcTest(SMSController.class)
public class SMSControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final SMSConfig smsConfig = new SMSConfig();

    @MockBean
    private SMSService smsService;

    @Test
    void shouldSendSaveSMS() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", "987654321",
                "That is Google website https://www.google.pl/");

        when(smsService.sendSMS(smsDto)).thenReturn(true);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldntSendDangerousSMS() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", "987654321",
                "That is Google website https://www.gaagle.pl/");

        when(smsService.sendSMS(smsDto)).thenThrow(MessageContainsDangerousLink.class);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldSendConfigSMS_StartProtection() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", smsConfig.getNumber(),
                smsConfig.getStartPhishingProtectMess());

        when(smsService.sendSMS(smsDto)).thenReturn(true);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldSendConfigSMS_StopProtection() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", smsConfig.getNumber(),
                smsConfig.getStopPhishingProtectMess());

        when(smsService.sendSMS(smsDto)).thenReturn(true);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldSendConfigSMS_StartProtection_IsAlreadyProtected() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", smsConfig.getNumber(),
                smsConfig.getStartPhishingProtectMess());

        when(smsService.sendSMS(smsDto)).thenThrow(PhishingProtectionIsAlreadyWorking.class);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldSendConfigSMS_StopProtection_WasntProtectedBefore() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", smsConfig.getNumber(),
                smsConfig.getStopPhishingProtectMess());

        when(smsService.sendSMS(smsDto)).thenThrow(ThisSenderDoesntUsePhishingProtection.class);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void shouldSendConfigSMS_WrongConfigMessage() throws Exception {
        //Given
        SMSDto smsDto = new SMSDto("123456789", smsConfig.getNumber(),
                smsConfig.getStopPhishingProtectMess() + "AAA");

        when(smsService.sendSMS(smsDto)).thenThrow(WrongConfigMessage.class);

        Gson gson = new Gson();
        String jsonContent = gson.toJson(smsDto);

        //When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/v1/sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }
}
