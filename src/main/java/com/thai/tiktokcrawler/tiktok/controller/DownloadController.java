package com.thai.tiktokcrawler.tiktok.controller;

import com.thai.tiktokcrawler.tiktok.helper.TikTokDownloaderV2;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/download")
@AllArgsConstructor
public class DownloadController {
    private TikTokDownloaderV2 tikTokDownloaderV2;
    @GetMapping("/{url}")
    public String download(@PathVariable("url") String url) throws IOException {
        String TikTokSaveLocation = "C:\\Users\\admin\\Downloads\\tiktok\\TikTok_" + System.currentTimeMillis() + ".mp4";
        tikTokDownloaderV2.download(TikTokSaveLocation, url);
        return "OK";
    }
}
