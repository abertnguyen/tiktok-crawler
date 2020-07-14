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
    @GetMapping()
    public String download(@RequestParam("url") String url,
                           @RequestParam("file_name") String fileName,
                           @RequestParam("header") String header,
                           @RequestParam("footer") String footer) throws IOException, InterruptedException {
        String fileLocation = tikTokDownloaderV2.download(url);
        tikTokDownloaderV2.clearWaterMark(fileLocation, fileName, header, footer);
        return "OK";
    }
}
