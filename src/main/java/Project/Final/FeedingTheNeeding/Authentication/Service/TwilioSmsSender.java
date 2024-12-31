package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.Config.TwilioSMSConfig;
import Project.Final.FeedingTheNeeding.Authentication.DTO.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("twilio")
public class TwilioSmsSender implements SmsSender {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSmsSender.class);

    private final TwilioSMSConfig twilioSMSConfig;

    @Autowired
    public TwilioSmsSender(TwilioSMSConfig twilioSMSConfig) {
        this.twilioSMSConfig = twilioSMSConfig;
    }

    @Override
    public void sendSms(SmsRequest smsRequest) {
        if(isPhoneNumberValid(smsRequest.getPhoneNumber())) {
            PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber(twilioSMSConfig.getTrialNumber());
            String message = smsRequest.getMessage();
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
            logger.info("send sms {}", smsRequest);
        }
        else
            throw new IllegalArgumentException("Invalid phone number");
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
       return true; // change
    }
}
