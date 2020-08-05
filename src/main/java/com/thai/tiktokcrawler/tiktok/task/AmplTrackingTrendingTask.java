package com.thai.tiktokcrawler.tiktok.task;

import com.thai.tiktokcrawler.tiktok.entity.TransactionHistory;
import com.thai.tiktokcrawler.tiktok.helper.TelegramHelper;
import com.thai.tiktokcrawler.tiktok.repository.SettingRepository;
import com.thai.tiktokcrawler.tiktok.repository.TransactionHistoryRepository;
import com.thai.tiktokcrawler.tiktok.util.TelegramMessageTemplate;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@AllArgsConstructor
public class AmplTrackingTrendingTask {
    private static final String trackingUrl = "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2";
    private SettingRepository settingRepository;
    private TelegramHelper telegramHelper;
    private TelegramMessageTemplate telegramMessageTemplate;
    private TransactionHistoryRepository transactionHistoryRepository;

    @Scheduled(fixedDelay = 60000)
    public void trackingAmplTransaction() throws IOException {
        Double timeToTracking = settingRepository.findFirstByKey("TIME_TO_TRACKING").getValueAsDouble();
        Double rateToNoti = settingRepository.findFirstByKey("RATE_TO_NOTI").getValueAsDouble();
        Double totalBought = round(totalBought(timeToTracking.intValue(), 0d));
        Double totalSold = round(totalSold(timeToTracking.intValue(), 0d));
        Map<String, Object> data = new HashMap<>();
        data.put("totalBought", totalBought);
        data.put("totalSold", totalSold);
        data.put("timeToTracking", timeToTracking);
        if (totalBought == 0d && totalSold == 0d) return;
        if (totalBought == 0d && totalSold > 0) {
            data.put("trending", "SOLDDDDDD");
            String message = telegramMessageTemplate.load("ampl-tracking-trend.html", data);
            telegramHelper.sendHTMLMessage(message);
        }
        if (totalBought > 0d && totalSold == 0d) {
            data.put("trending", "BOUGHTTTT");
            String message = telegramMessageTemplate.load("ampl-tracking-trend.html", data);
            telegramHelper.sendHTMLMessage(message);
        }
        if (totalBought / totalSold > rateToNoti) {
            data.put("trending", "Boughtttt");
            data.put("rate", round(totalBought / totalSold));
            String message = telegramMessageTemplate.load("ampl-tracking-trend.html", data);
            telegramHelper.sendHTMLMessage(message);
        } else if (totalSold / totalBought > rateToNoti) {
            data.put("trending", "Soldddddd");
            data.put("rate", round(totalSold / totalBought));
            String message = telegramMessageTemplate.load("ampl-tracking-trend.html", data);
            telegramHelper.sendHTMLMessage(message);
        }
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
        for (TransactionHistory transactionHistory : transactionHistoryList) {
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
