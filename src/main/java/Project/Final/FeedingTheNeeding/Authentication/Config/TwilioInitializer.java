package Project.Final.FeedingTheNeeding.Authentication.Config;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private final TwilioSMSConfig twilioSMSConfig;

    private final static Logger logger = LoggerFactory.getLogger(TwilioInitializer.class);

    @Autowired
    public TwilioInitializer(TwilioSMSConfig twilioSMSConfig) {
        this.twilioSMSConfig = twilioSMSConfig;

        Twilio.init(
                twilioSMSConfig.getAccountSid(),
                twilioSMSConfig.getAuthToken()
        );

        logger.info("Twilio initialized with SID: {}", twilioSMSConfig.getAccountSid());
    }
}
