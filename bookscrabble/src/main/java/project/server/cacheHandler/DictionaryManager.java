package project.server.cacheHandler;

import java.util.HashMap;

import project.server.serverHandler.BookScrabbleHandler;

public class DictionaryManager {
    HashMap<String,Dictionary> mapOfDictionaries = null;
    private static DictionaryManager myDictionaryManager = null;

    private DictionaryManager()
    {
        mapOfDictionaries = new HashMap<>(){{
            for (int i = 0; i < BookScrabbleHandler.dictionaries.length; i++)
                put(BookScrabbleHandler.dictionaries[i], new Dictionary(BookScrabbleHandler.dictionaries[i]));         
        }};
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
        word = word.toUpperCase();
        if(word.length() == 0)
            return false;

        for (int i = 0; i < args.length - 1; i++) 
        {
            if(!mapOfDictionaries.containsKey(args[i]))
                mapOfDictionaries.put(args[i], new Dictionary(args[i]));
            res |= mapOfDictionaries.get(args[i]).query(word);
            if(res)
                return true;
        }
        return res; 
    }

    public boolean challenge(String...args)
    {
        boolean res = false;
        String word = args[args.length -1];
        if(word.length() == 0)
            return false;
        word = word.toUpperCase();
        for (int i = 0; i < args.length - 1; i++) 
        {
            if(!mapOfDictionaries.containsKey(args[i]))
                mapOfDictionaries.put(args[i], new Dictionary(args[i]));
            res |= mapOfDictionaries.get(args[i]).challenge(word);
            if(res)
                return true;
        }
        return res; 
    }

    public int getSize()
    {
        return mapOfDictionaries.size();
    }
}
