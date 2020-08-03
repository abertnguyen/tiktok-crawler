package com.thai.tiktokcrawler.tiktok.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwapElement {
    Double amount0In;
    Double amount0Out;
    Double amount1In;
    Double amount1Out;
    Double amountUSD;
    String sender;
    long timestamp;
    String to;
    Transaction transaction;
}
