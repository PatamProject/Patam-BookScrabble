package project.cacheHandler;

import java.util.HashMap;

public class DictionaryManager {
    HashMap<String,Dictionary> mapOfDictionaries = null;
    private static DictionaryManager myDictionaryManager = null;

    private DictionaryManager()
    {
        mapOfDictionaries = new HashMap<>();
    }

    public static DictionaryManager get()
    {
        if(myDictionaryManager != null)
            return myDictionaryManager;
        myDictionaryManager = new DictionaryManager();
        return myDictionaryManager;
    }

    public boolean query(String...args)
    {
        boolean res = false;
        String word = args[args.length -1];
        for (int i = 0; i < args.length - 1; i++) 
        {
            if(!mapOfDictionaries.containsKey(args[i]))
                mapOfDictionaries.put(args[i], new Dictionary(args[i]));
            res |= mapOfDictionaries.get(args[i]).query(word);
        }
        return res; 
    }

    public boolean challenge(String...args)
    {
        boolean res = false;
        String word = args[args.length -1];
        for (int i = 0; i < args.length - 1; i++) 
        {
            if(!mapOfDictionaries.containsKey(args[i]))
                mapOfDictionaries.put(args[i], new Dictionary(args[i]));
            res |= mapOfDictionaries.get(args[i]).challenge(word);
        }
        return res; 
    }

    public int getSize()
    {
        return mapOfDictionaries.size();
    }
}
