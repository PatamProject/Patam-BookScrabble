package bookscrabble.server.cacheHandler;

import java.util.ArrayList;
import java.util.BitSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class BloomFilter {
	BitSet bitArray = null;
    ArrayList<MessageDigest> hashArr = null;
    private int size;

    BloomFilter(int size, String...hash)
    {
        this.size = size;
        bitArray = new BitSet(size);
        hashArr = new ArrayList<>();
        
        for (String hashFunction : hash) {
            try {
                MessageDigest md = MessageDigest.getInstance(hashFunction);
                hashArr.add(md);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("No such hash function exists: " + hashFunction);
            }
        }
    }

    void add(String word) //Run all hash functions on word
    {
        for (MessageDigest md : hashArr) 
        {
            byte[] hash = md.digest(word.getBytes());
            BigInteger bi = new BigInteger(1,hash);
            int index = bi.mod(BigInteger.valueOf(size)).intValue();
            bitArray.set(index, true);
        }
    }

    boolean contains(String word) //Run all hash functions and check for bits
    {
        for (MessageDigest md : hashArr)
        {
            byte[] hash = md.digest(word.getBytes());
            BigInteger bi = new BigInteger(1, hash);
            int index = bi.mod(BigInteger.valueOf(size)).intValue();
            if (!bitArray.get(index)) {
                return false;
            }
        }
        return true; //passed all hash checks
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitArray.length(); i++) 
            sb.append(bitArray.get(i) ? "1" : "0");
        return sb.toString();
    }
}
