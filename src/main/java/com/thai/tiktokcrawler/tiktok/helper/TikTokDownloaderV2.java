package com.thai.tiktokcrawler.tiktok.helper;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Component
public class TikTokDownloaderV2 {
    public String download(String videoUrl) throws IOException {
//        String fileLocation = "C:\\Users\\user\\Downloads\\tiktok\\intput.mp4";
        String fileLocation = "C:\\Users\\admin\\Downloads\\tiktok\\input.mp4";
        URL url = new URL(videoUrl);
        //Create a URL connection
        URLConnection conn = url.openConnection();

        //Set the user agent so TikTok will think we're a person using a browser instead of a program
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        //Set up the bufferedReader
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        /*
         * Read every line until we get
         * to a string with the text 'videoObject'
         * which is where misc. information about
         * the user is stored and where the video
         * URL is stored too
         */
        String data;
        while ((data = in.readLine()) != null) {
            if (data.contains("videoObject")) {
                // Read up until we reach a string in the HTML file valled 'videoObject'
                break;
            }
        }

        //Close the bufferedReader as we don't need it anymore
        in.close();

        /*
         * Because we are viewing the raw source code from
         * the website, there's a lot of trash including but not
         * limited to HTML tags, javascript, random text, and so
         * on. We don't want that. That's why it will be cropped
         * out down below
         */

        //Crop out the useless tags and code behind the VideoObject string
        data = data.substring(data.indexOf("VideoObject"));


        //Grab the thumb nail URL
        String thumbnailURL = data.substring(data.indexOf("thumbnailUrl") + 16);
        thumbnailURL = thumbnailURL.substring(0, thumbnailURL.indexOf("\""));

        // Print out the thumbnail URL
        System.out.println("ThumbnailURL: " + thumbnailURL);

        //Grab content URL (video file)
        String contentURL = data.substring(data.indexOf("contentUrl") + 13);
        contentURL = contentURL.substring(0, contentURL.indexOf("?"));

        //Print out the video URL
        System.out.println("ContentURL: " + contentURL);

        //Now that we have the Thumbnail and Video URL, we can download them!
        downloadVideoFile(fileLocation, contentURL);
        return fileLocation;
    }

    /*
     * This method actually downloads the video
     */
    private void downloadVideoFile(String fileLocation, String url) throws IOException {
        File file = new File(fileLocation);
        //Set up the stream and connect to the video URL
        InputStream inputStream = new URL(url).openStream();

        //Set up the buffer to store the inputstream bytes into
        //It can be anything, but 512 is an okay buffer size
        byte[] videoBuffer = new byte[512];

        //Set up the file output stream
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        /*
         * When the streams ends (which means we downloaded the whole video)
         * it will return '-1'
         * While we store the bytes into the videoBuffer from inputStream
         * and the stream doesn't equal -1, write each byte from the buffer into the file.
         * When the stream equals -1, close the file as the video is complete
         */
        int len;
        while ((len = inputStream.read(videoBuffer)) != -1) {
            fileOutputStream.write(videoBuffer, 0, len);
        }

        //Close the file stream
        fileOutputStream.close();

        //Done message
        System.out.println("Video Downloaded!");
    }

    public void clearWaterMark(String input, String fileName, String header, String footer) throws IOException {
        String output = "des\\" + fileName + ".mp4";
        String cmd = "C:\\ffmpeg\\bin\\ffmpeg.exe -y -i " + input + " -vf \"drawbox=0:0:iw:75:pink@1:t=fill,drawbox=0:ih-75:iw:75:pink@1:t=fill," +
                "drawtext=fontfile=/Windows/Fonts/Roboto-Bold.ttf: text='" + header + "': fontcolor=white: fontsize=35: x=(w-tw)/2:y=35, " +
                "drawtext=fontfile=/Windows/Fonts/Roboto-Bold.ttf: text='" + footer + "': fontcolor=white: fontsize=35:x=(w-tw)/2: y=(h-text_h)-35\" -codec:a copy -preset" +
                " ultrafast -c:a copy " + output;
        Process p = Runtime.getRuntime().exec(cmd);
        InputStreamReader isr = new InputStreamReader(p.getErrorStream());
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println("done");
    }
}
