package com.thai.tiktokcrawler.tiktok.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HarryTran
 */
@Component
public class TelegramMessageTemplate {

    private static final String PARAM_PREFIX = "{{";
    private static final String PARAM_SUFFIX = "}}";
    private static final String TEMPLATE_FOLDER_PATH = "classpath:templates/telegram/";
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext();

    private String load(String fileName) throws IOException {
        InputStream ip = applicationContext.getResource(TEMPLATE_FOLDER_PATH + fileName).getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(ip));
        String result = br.lines().collect(Collectors.joining("\r\n"));
        br.close();
        return result;
    }

    public String load(String fileName, Map<String, Object> data) throws IOException {
        String message = load(fileName);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String param = PARAM_PREFIX + entry.getKey() + PARAM_SUFFIX;
            if (entry.getValue() != null && !"null".equals(entry.getValue())) {
                message = message.replace(param, entry.getValue().toString());
            }
        }
        return message;
    }
}
