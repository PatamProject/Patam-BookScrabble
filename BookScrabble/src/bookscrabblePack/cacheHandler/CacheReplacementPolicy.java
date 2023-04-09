package bookscrabblePack.cacheHandler;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove();
}
