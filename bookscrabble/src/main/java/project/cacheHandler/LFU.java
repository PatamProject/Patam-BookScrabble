package project.cacheHandler;

import java.util.HashMap;
import java.util.PriorityQueue;

public class LFU implements CacheReplacementPolicy{
    PriorityQueue<String> queue = null;
    HashMap<String,Integer> map = null;
    LFU()
    {
        this.queue = new PriorityQueue<>((x,y)-> map.get(x) - map.get(y)); //Min-Heap based on count
        this.map = new HashMap<>();      
    }

    @Override
    public void add(String word) 
    {
        if(!map.containsKey(word))
        {
            map.put(word,1);
            queue.add(word);
        }
        else //Word already in LFU, count++, and update heap
        {
            int count = map.get(word);
            map.replace(word, count, count + 1);
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
        int count = map.get(toBeRemovedWord);
        if(count > 1)
            map.replace(toBeRemovedWord,count,count - 1);
        else
            map.remove(toBeRemovedWord);
        return toBeRemovedWord;       
    }
}
