package project.server.cacheHandler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LRU implements CacheReplacementPolicy{
    Queue<String> queue = null;
    HashSet<String> hashSet = null;
    LRU() 
    {
        this.queue = new LinkedList<>();
        hashSet = new HashSet<>();            
    }
    
    @Override
    public void add(String word) 
    {
        if(!hashSet.contains(word))
        {
            queue.add(word);
            hashSet.add(word);
        }
        else //Word already in LRU, move it to the end of the queue.
        {
            queue.remove(word);
            queue.add(word);
        }

    }

    @Override
    public String remove() 
    {
        if(queue.isEmpty())
            return null;
        String toBeRemovedWord = queue.poll();    
        hashSet.remove(toBeRemovedWord);
        return toBeRemovedWord;       
    }
}
