package bookscrabble.server.cacheHandler;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove();
}
