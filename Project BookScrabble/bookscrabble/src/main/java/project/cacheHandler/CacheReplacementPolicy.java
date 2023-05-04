package project.cacheHandler;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove();
}
