package com.thai.tiktokcrawler.tiktok.task;

import com.thai.tiktokcrawler.tiktok.entity.Setting;
import com.thai.tiktokcrawler.tiktok.entity.TransactionHistory;
import com.thai.tiktokcrawler.tiktok.helper.TelegramHelper;
import com.thai.tiktokcrawler.tiktok.repository.SettingRepository;
import com.thai.tiktokcrawler.tiktok.repository.TransactionHistoryRepository;
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
import java.util.*;

@Component
@AllArgsConstructor
public class AmplTrackingTask {
    private static final String trackingUrl = "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2";
    private SettingRepository settingRepository;
    private TelegramHelper telegramHelper;
    private TelegramMessageTemplate telegramMessageTemplate;
    private TransactionHistoryRepository transactionHistoryRepository;
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
                List<TransactionHistory> transactionHistoryList = new ArrayList<>();
                for(SwapElement swapElement : swapResponse.getData().getSwaps()) {
                    TransactionHistory transactionHistory = TransactionHistory.builder()
                            .ethAmount(swapElement.getAmount0In() > 0 ? swapElement.getAmount0In() : swapElement.getAmount0Out())
                            .amplAmount(swapElement.getAmount1In() >0 ? swapElement.getAmount1In() : swapElement.getAmount1Out())
                            .type(swapElement.getAmount0In() > 0 ? "Bought" : "Sold")
                            .fromAddress(swapElement.getSender())
                            .toAddress(swapElement.getTo())
                            .txId(swapElement.getTransaction().getId())
                            .usdAmount(swapElement.getAmountUSD())
                            .createdTimeTx(new Date(swapElement.getTimestamp() * 1000))
                            .build();
                    transactionHistoryList.add(transactionHistory);
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
                        data.put("txId", swapElement.getTransaction().getId());
                        data.put("totalBought", round(totalBought(30, swapElement.getAmountUSD())));
                        data.put("totalSold", round(totalSold(30, swapElement.getAmountUSD())));
                        String message = telegramMessageTemplate.load("ampl-tracking.html", data);
                        telegramHelper.sendHTMLMessage(message);
                    }
                    maxTimeRequest = "" + swapElement.getTimestamp();
                }
                transactionHistoryRepository.saveAll(transactionHistoryList);
            }
        }
        setting.setValue(maxTimeRequest);
        settingRepository.save(setting);
    }

    public Double totalBought(int limit, Double amountUsd) {
        return getTotalByType(limit, amountUsd, "Bought");
    }

    public Double totalSold(int limit, Double amountUsd) {
        return getTotalByType(limit, amountUsd, "Sold");
    }

    public Double getTotalByType(int limit, Double amountUsd, String type) {
        Date date = add(new Date(), -limit, Calendar.MINUTE);
        Double amount = amountUsd;
        List<TransactionHistory> transactionHistoryList = transactionHistoryRepository.findAllByTypeAndCreatedTimeTxBetween(type, date, new Date());
        for(TransactionHistory transactionHistory : transactionHistoryList) {
            amount += transactionHistory.getUsdAmount();
        }
        return amount;
    }

    public static Date add(Date date, int unit, int calendar) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendar, unit); //minus number would decrement the days
        return cal.getTime();
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
