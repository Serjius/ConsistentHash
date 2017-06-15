package com.company;

/**
 * Created by pss on 11.06.17.
 *
 * Cluster's node for the consistent hash
 * only name and count how many keys are on the node.
 */
public class Node {
    private final String name;
    private long keyCount;

    public Node(String name) {
        this.name = name;
        this.keyCount = 0;
    }

    public void incKeyCount() {
        keyCount++;
    }

    public long getKeyCount() {
        return keyCount;
    }

    public void setZeroCount() {
        keyCount = 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
