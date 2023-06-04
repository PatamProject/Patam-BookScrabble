package project.server.cacheHandler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Dictionary 
{
    String[] dictionaryFileName = null;
    CacheManager lruCache = null;
    CacheManager lfuCache = null;
    BloomFilter bf = null;

    Dictionary(String... files)
    {
        this.dictionaryFileName = new String[files.length];
        for (int i = 0; i < files.length; i++)
            dictionaryFileName[i] = files[i]; 
        
        lruCache = new CacheManager(400, new LRU()); //Used for real words
        lfuCache = new CacheManager(100, new LFU()); //Used for unreal words
        bf = new BloomFilter((int)(Math.pow(2, 17)), "MD5","SHA1","SHA256","SHA384","MD2","SHA512");

        try {
            for (String fileName : dictionaryFileName) {
                File file = new File(fileName);
                Scanner reader = new Scanner(file); // Declaring Scanner
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    line = line.trim();
                    line = line.toUpperCase();
                    String[] words; // Array of words
                    words = line.split(" "); // Splitting the array to words
                    for (String word : words) // Adding words in separate
                        if(word.length() > 0)
                        {
                            if(word.length() == 1 && (word.charAt(0) != 'A' && word.charAt(0) != 'I'))
                                continue; //Ignore single letter words except A and I     
                            word = fixWord(word);
                            bf.add(word);   
                        }

                }
                reader.close();
            }
        }catch (Exception e){ 
            System.out.println("Problem in reading dictionary file " + e.getMessage());
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
            res = IOSearcher.search(word, dictionaryFileName);
        } catch (IOException e) {
            return false;
        }
        if(res)
            lruCache.add(word);
        else
            lfuCache.add(word);    
        return res;
    }

    private String fixWord(String word)
    {
        word = word.toUpperCase();
        char lastChar = word.charAt(word.length() - 1);
        while(lastChar < 'A' || lastChar > 'Z')
            word = word.substring(0, word.length() - 1);
        return word;
    }
}
