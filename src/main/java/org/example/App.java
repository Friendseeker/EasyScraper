package org.example;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        // Scrape movie lines and CSID, output a csv file.

         Scanner scanner = new Scanner(System.in);  // Create a Scanner object
         System.out.println("Enter Path to HTML file. \n" +
                "Do not use Safari to save HTML file as it changes the file in unwanted way \n" +
         "recommended to save using FireFox and SAVE THE WHOLE PAGE!");
        // class names are CHANGED when using Safari to save WTF
         String input = scanner.nextLine(); // Set the path

        File in = new File(input); // No escape issue, which I appreciate
        Document doc = Jsoup.parse(in, null);

        // Stage 1 Test, if this shows incorrect result then need fixes.
        // System.out.printf("Title: %s\n", doc.title());

        // class that contains reply: actual_text post_region_text

        Elements replies = doc.getElementsByClass("actual_text post_region_text");
        List<String> textReplies = replies.eachText();

        // Open the CSV file
        FileWriter w = new FileWriter("MovieLog.csv");
        CSVWriter writer = new CSVWriter(w);
        // Feed in 1st row (headings)
        String[] entries = ("ID#link").split("#");
        // Spilt is a powerful function
        writer.writeNext(entries); // Write 1st row

        // Extracting CS ID and url links

        Pattern idPattern = Pattern.compile("\\w\\d\\w\\d\\w");
        Pattern urlPattern = Pattern.compile(
                "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})");
        // thanks google, I will never figure the f****** url matching regex out by self.

        for (String r : textReplies) {
            Matcher idMatcher = idPattern.matcher(r);
            Matcher urlMatcher = urlPattern.matcher(r);
            if (idMatcher.find() & urlMatcher.find()) {
                String url = urlMatcher.group(0);
                String id = idMatcher.group(0);
                // lesson, cannot call .group(0) twice in a row.
                // for weird reason I don't know
                // thank you unwanted side effect for wasting me 30 min to debug.
                System.out.println(id + " " + url); // For debugging purpose
                // Writing
                writer.writeNext(new String[]{id, url});
            }
            // why didn't IntelliJ tell me that I need to wrap statements in if with {}...
        }

        // Closes Writer
        writer.close();

        // Final Message
        System.out.println("IDs and Movie Links are now saved as MovieLog.csv");
    }
}
