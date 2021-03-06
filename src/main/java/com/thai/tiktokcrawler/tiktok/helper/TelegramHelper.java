package com.thai.tiktokcrawler.tiktok.helper;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HarryTran
 */
@Component
@Log4j2
public class TelegramHelper {
    public void sendHTMLMessage(String message) {
        sendMessage(message, "HTML");
    }

    public void sendMarkdownMessage(String message) {
        sendMessage(message, "Markdown");
    }

    public void sendMessage(String message, String parseMode) {
        try {
            String url = "https://api.telegram.org/bot1356869800:AAFhDHMUFIBlgnp9FWLzsvy8zNxzufXkWFs/sendMessage?chat_id=-1001463101645&text={text}";
            Map<String, String> params = new HashMap<>();
            params.put("text", message);

            if (parseMode != null) {
                url += "&parse_mode={parse_mode}&disable_web_page_preview={disable_web_page_preview}";
                params.put("parse_mode", parseMode);
                params.put("disable_web_page_preview", "true");
            }
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class, params);
        } catch (HttpClientErrorException.BadRequest e) {
            String response = e.getResponseBodyAsString();
            log.error("Telegram response error: " + response, e);
        } catch (Exception e) {
            log.error("Send Telegram message has occurred error ", e);
        }
    }
}
