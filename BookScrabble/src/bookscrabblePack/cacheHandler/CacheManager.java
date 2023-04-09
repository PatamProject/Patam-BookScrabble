package bookscrabblePack.cacheHandler;

import java.util.HashSet;

public class CacheManager {
	HashSet<String> hashSet;
    CacheReplacementPolicy crp;
    final int size;

    CacheManager(int size, CacheReplacementPolicy crp)
    {
        this.size = size;
        this.crp = crp;
        hashSet = new HashSet<>(size);
    }
	
    boolean query(String word)
    {
        return hashSet.contains(word);
    }

    void add(String word)
    {
        crp.add(word);
        if(!query(word)) //if word isn't in cache
        {
            hashSet.add(word);
            if(hashSet.size() > size)
                hashSet.remove(crp.remove());    
        }
    }
}
