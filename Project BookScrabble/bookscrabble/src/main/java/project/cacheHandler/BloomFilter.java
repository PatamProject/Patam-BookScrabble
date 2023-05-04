package project.cacheHandler;

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
        MessageDigest md;
        this.size = size;
        bitArray = new BitSet();
        hashArr = new ArrayList<>();
        for (int i = 0; i < hash.length; i++) //add all hash functions to hashArr
        {
            try {
                md = MessageDigest.getInstance(hash[i]);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Hash["+i+"] -> No such hash function exists!");
            }
            hashArr.add(md);
        } 
    }

    void add(String word) //Run all hash functions on word
    {
        for (MessageDigest md : hashArr) 
        {
            BigInteger bi = new BigInteger(1,md.digest(word.getBytes()));
            int hashV = Math.abs(bi.abs().intValue() % size);
            bitArray.set(hashV,true);
        }
    }

    boolean contains(String word) //Run all hash functions and check for bits
    {
        for (MessageDigest md : hashArr)
        {
            BigInteger bi = new BigInteger(1,md.digest(word.getBytes()));
            int hashV = Math.abs(bi.abs().intValue() % size);
            if(!bitArray.get(hashV)) //bit is set to 0 --> the word is not in the bf
                return false;
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
