package com.thai.tiktokcrawler.tiktok.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwapData {
    @JsonProperty("swaps")
    List<SwapElement> swaps;
}
