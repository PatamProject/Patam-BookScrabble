package project.server.cacheHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Class used to search a word in the given files by reading it
public class IOSearcher {
    // Method used to determine if a given word is in one of the files
    static Boolean search(String word, String ... fileName) throws IOException {
        List<String> fileNames = new ArrayList<>();
        Collections.addAll(fileNames, fileName); // Adding the given file names to the list

        for (String name : fileNames) { // Loop through the names list
            BufferedReader reader = new BufferedReader(new FileReader(name)); // Declaration of reader
            String line = reader.readLine();
            while (line != null) {
                line = line.trim(); // Remove the spaces from the line
                line = line.toUpperCase(); // Convert the line to upper case
                String wordsInLine[] = line.split(" "); // Split the line to words
                for (String wordInLine : wordsInLine) { // Loop through the words in the line
                    wordInLine = wordInLine.trim(); // Remove the spaces from the word
                    if (wordInLine.equals(word)) { // Check if the word is equal to the given word
                        reader.close();
                        return true;
                    }
                }
                line = reader.readLine();
            }
            reader.close(); // Close the reader
        }
        return false;
    }
}