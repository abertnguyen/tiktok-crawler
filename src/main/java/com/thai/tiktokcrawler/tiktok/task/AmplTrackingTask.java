package com.thai.tiktokcrawler.tiktok.task;

import com.thai.tiktokcrawler.tiktok.entity.Setting;
import com.thai.tiktokcrawler.tiktok.helper.TelegramHelper;
import com.thai.tiktokcrawler.tiktok.repository.SettingRepository;
import com.thai.tiktokcrawler.tiktok.response.SwapElement;
import com.thai.tiktokcrawler.tiktok.response.SwapResponse;
import com.thai.tiktokcrawler.tiktok.util.TelegramMessageTemplate;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class AmplTrackingTask {
    private static final String trackingUrl = "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2";
    private SettingRepository settingRepository;
    private TelegramHelper telegramHelper;
    private TelegramMessageTemplate telegramMessageTemplate;
    @Scheduled(fixedDelay = 10000)
    public void trackingAmplTransaction() throws IOException {
        Setting setting = settingRepository.findFirstByKey("MAX_TIME_REQUEST");
        Double limitNotify = settingRepository.findFirstByKey("LIMIT_NOTIFICATION").getValueAsDouble();
        String maxTimeRequest = setting.getValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = getDefaultHeaders();
        String body = "{\"query\":\"{\\r\\n  swaps(orderBy: timestamp, orderDirection: asc,\\r\\n  where: {pair: \\\"0xc5be99a02c6857f9eac67bbce58df5572498f40c\\\",\\r\\n  timestamp_gt: %s}){\\r\\n    transaction {\\r\\n      id\\r\\n    },\\r\\n    amount0In,\\r\\n    amount1In,\\r\\n    amount0Out,\\r\\n    amount1Out,\\r\\n    amountUSD,\\r\\n    timestamp,\\r\\n    sender,\\r\\n    to\\r\\n  }\\r\\n}\",\"variables\":{}}";
        body = String.format(body, setting.getValue());
        HttpEntity<String> entity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<SwapResponse> responseEntity = restTemplate.exchange(trackingUrl, HttpMethod.POST, entity, SwapResponse.class);
        if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            SwapResponse swapResponse = responseEntity.getBody();
            if(swapResponse.getData() != null && !CollectionUtils.isEmpty(swapResponse.getData().getSwaps())) {
                for(SwapElement swapElement : swapResponse.getData().getSwaps()) {
                    if(swapElement.getAmount0In() >= limitNotify || swapElement.getAmount0Out() >= limitNotify) {
                        String type = swapElement.getAmount0In() > 0 ? "Bought" : "Sold";
                        Map<String, Object> data = new HashMap<>();
                        data.put("type", "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB" + type + "\uD83D\uDCB0");
                        if(type.equalsIgnoreCase("Sold")) {
                            data.put("amplValue", round(swapElement.getAmount1In()));
                            data.put("ethValue", round(swapElement.getAmount0Out()));
                            String circle = "";
                            for(int i = 0 ; i < swapElement.getAmount0Out(); i++) {
                                circle += "\uD83D\uDD34";
                            }
                            data.put("circle", circle);
                            data.put("rate", round(swapElement.getAmountUSD()/swapElement.getAmount1In(), 4));
                        } else {
                            data.put("amplValue", round(swapElement.getAmount1Out()));
                            data.put("ethValue", round(swapElement.getAmount0In()));
                            String circle = "";
                            for(int i = 0 ; i < swapElement.getAmount0In().intValue(); i++) {
                                circle += "\uD83D\uDFE2";
                            }
                            data.put("circle", circle);
                            data.put("rate", round(swapElement.getAmountUSD()/swapElement.getAmount1Out(), 4));
                        }
                        data.put("usdValue", round(swapElement.getAmountUSD()));
                        data.put("time", simpleDateTime(new Date(swapElement.getTimestamp() * 1000)));
//                        data.put("transactionId", swapElement.getTransaction().getId());
                        String message = telegramMessageTemplate.load("ampl-tracking.html", data);
                        telegramHelper.sendHTMLMessage(message);
                    }
                    maxTimeRequest = "" + swapElement.getTimestamp();
                }
            }
        }
        setting.setValue(maxTimeRequest);
        settingRepository.save(setting);
    }

    public HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public String simpleDateTime(Date d) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(d);
    }

    public double round(double num1, int place) {
        if (place < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(num1);
        bd = bd.setScale(place, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double round(double num) {
        return round(num, 2);
    }
}
