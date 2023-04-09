package bookscrabblePack.cacheHandler;

import java.io.IOException;
import java.util.ArrayList;

public class Dictionary 
{
    String[] fileNames = null;
    CacheManager lruCache = null;
    CacheManager lfuCache = null;
    BloomFilter bf = null;
    Dictionary(String... files)
    {
        this.fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++)
            fileNames[i] = files[i];
        
        lruCache = new CacheManager(400, new LRU()); //Used for real words
        lfuCache = new CacheManager(100, new LFU()); //Used for unreal words
        bf = new BloomFilter(256, "MD5", "SHA1");

        ArrayList<String> words = new ArrayList<>();
        for (String fileName : fileNames) {
            try {
                words = IOSearcher.pullWordsForFile(fileName);
            } catch (IOException e) {} 
            
            for (String word : words)
                bf.add(word);      
        }
    }

    boolean query(String word)
    {
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
        boolean res = false;
        try {
            res = IOSearcher.search(word, fileNames);
        } catch (IOException e) {
            return false;
        }
        if(res)
            lruCache.add(word);
        else
            lfuCache.add(word);    
        return res;
    }
}
