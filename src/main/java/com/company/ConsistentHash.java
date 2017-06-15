package com.company;


/**
 * Created by pss on 11.06.17.
 *
 * Naive consistent hash
 *
 * The main idea from Tom White (http://www.tom-e-white.com/2007/11/consistent-hashing.html)
 */

import com.google.common.hash.HashFunction;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;


public class ConsistentHash<T> {
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Long, T> theRing = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            theRing.put(
                    hashFunction.hashString(node.toString() + ":" + i, StandardCharsets.UTF_8).padToLong()
                    , node
            );
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            theRing.remove(
                    hashFunction.hashString(node.toString() + ":" + i, StandardCharsets.UTF_8).padToLong()
            );
        }
    }

    public T get(Long key) {
        if (theRing.isEmpty()) {
            return null;
        }
        Long hash = hashFunction.hashLong(key).padToLong();
        if (!theRing.containsKey(hash)) {
            SortedMap<Long, T> tailMap = theRing.tailMap(hash);
            hash = tailMap.isEmpty() ? theRing.firstKey() : tailMap.firstKey();
        }
        return theRing.get(hash);
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }
}
