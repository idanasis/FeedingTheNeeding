package Project.Final.FeedingTheNeeding.Authentication.Service;

import Project.Final.FeedingTheNeeding.Authentication.DTO.SmsRequest;

public interface SmsSender {

    void sendSms(SmsRequest smsRequest);
}
