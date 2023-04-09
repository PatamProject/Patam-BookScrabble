package bookscrabblePack.cacheHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class IOSearcher {
    static boolean search(String word, String...strs) throws IOException
    {
        BufferedReader bf = null;
        ArrayList<String> fileNames = new ArrayList<>();
        Collections.addAll(fileNames,strs);
        for (String name : fileNames) //Open all files
        {    
            try {
                bf = new BufferedReader(new FileReader(name));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                bf.close();
                throw new RuntimeException("File" + name + "not found");
            }
            
            String s;
            while((s = bf.readLine()) != null) //search for word inside file
            if(s.contains(word))
            {
                bf.close();
                return true;
            }
            bf.close();
        }
        return false;
    }

    static ArrayList<String> pullWordsForFile(String fileName) throws IOException 
    { //Opens file and returns an array of all words inside
        BufferedReader bf = null;
        ArrayList<String> wordsFromFile = new ArrayList<>();   
        try {
            bf = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File" + fileName + "not found");
        }
        
        String s;
        while((s = bf.readLine()) != null)
        {
            String[] words = s.split(" ");   
            for (String word : words)
                if(word.length() > 0)
                    wordsFromFile.add(word);       
        }
        bf.close();
        return wordsFromFile;
    }
}
