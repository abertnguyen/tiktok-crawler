package com.thai.tiktokcrawler.tiktok.controller;

import com.thai.tiktokcrawler.tiktok.helper.TikTokDownloaderV2;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/download")
@AllArgsConstructor
public class DownloadController {
    private TikTokDownloaderV2 tikTokDownloaderV2;
    @GetMapping("/{url}")
    public String download(@PathVariable("url") String url, @RequestParam("name") String name) throws IOException, InterruptedException {
        String fileLocation = tikTokDownloaderV2.download(url);
        tikTokDownloaderV2.clearWaterMark(fileLocation, name);
        return "OK";
    }
}
