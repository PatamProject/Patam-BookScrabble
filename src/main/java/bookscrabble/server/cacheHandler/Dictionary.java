package bookscrabble.server.cacheHandler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Dictionary 
{
    String[] dictionaryFileNames = null;
    CacheManager lruCache = null;
    CacheManager lfuCache = null;
    BloomFilter bf = null;

    Dictionary(String... files)
    {
        this.dictionaryFileNames = new String[files.length];
        boolean isRealDictionary = false;
        for (int i = 0; i < files.length; i++)
        {
            if(files[i].contains("Dictionary"))
                isRealDictionary = true;
            dictionaryFileNames[i] = files[i]; 
        }

        lruCache = new CacheManager(400, new LRU()); //Used for real words
        lfuCache = new CacheManager(100, new LFU()); //Used for unreal words
        int size;
        if(isRealDictionary)
            size = (int)(Math.pow(2, 20));
        else
            size = (int)(Math.pow(2, 17));
        bf = new BloomFilter(size, "MD5","SHA1","SHA256","SHA384","MD2","SHA512");

        try {
            for (String fileName : dictionaryFileNames) {
                File file = new File(fileName);
                Scanner reader = new Scanner(file); // Declaring Scanner
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    line = line.trim();
                    line = line.toUpperCase();
                    String[] words; // Array of words
                    words = line.split(" "); // Splitting the array to words
                    for (String word : words) // Adding words in separate
                    {
                        word = fixWord(word);
                        if(word.length() > 0)
                        {
                            if(word.length() == 1 && (word.charAt(0) != 'A' && word.charAt(0) != 'I'))
                                continue; //Ignore single letter words except A and I     
                            
                            bf.add(word);   
                        }
                    }
                }
                reader.close();
            }
        }catch (Exception e){ 
            System.out.println("Problem in reading dictionary file " + e.getMessage());
            e.printStackTrace();
        }
    }

    boolean query(String word)
    {
        if(word.length() == 0)
            return false;

        boolean res = false;
        if(lruCache.query(word))
            return true;
        else if(lfuCache.query(word))   
            return false;
        else
        {
            res = bf.contains(word);
            if(res)
                lruCache.add(word);
            else
                lfuCache.add(word);    
        }
        return res;         
    }   

    boolean challenge(String word)
    {
        if(word.length() == 0)
            return false;

        boolean res = false;
        try {
            res = IOSearcher.search(word, dictionaryFileNames);
        } catch (IOException e) {
            return false;
        }
        if(res)
            lruCache.add(word);
        else
            lfuCache.add(word);    
        return res;
    }

    private String fixWord(String word) //removes all non alphabetic characters from the end of the word
    {
        word = word.toUpperCase();
        if(word.length() == 0)
            return word;    
        int i = word.length() - 1;
        while(i >= 0 && (word.charAt(i) < 'A' || word.charAt(i) > 'Z'))
            i--;
        return word.substring(0, i + 1);    
    }
}
