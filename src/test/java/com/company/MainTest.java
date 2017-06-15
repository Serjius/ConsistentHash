package com.company;

import com.google.common.hash.Hashing;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by pss on 11.06.17.
 *
 * Main unit tests for consistent hash cluster.
 * Test an expected key distribution in Empty, Static, Growing, Dyeing cluster.
 */
public class MainTest {
    private int GET_KEYS = 1_000_000;
    private int VIRTUAL_NODES = 1000;
    private List<Node> nodeList;
    private ConsistentHash<Node> cluster;


    @Test (expected=IllegalStateException.class)
    public void anEmptyCluster(){
        setEnvironmentForTest();
        runGetProcess();
    }
    @Test
    public void aStaticCluster() {
        setEnvironmentForTest();

        addNewNode("NodeA");
        addNewNode("NodeB");
        addNewNode("NodeC");
        addNewNode("NodeD");

        runGetProcess();

        testForPercent(20.00F, 30.00F);


    }

    @Test
    public void addNewNodeToTheCluster() {
        setEnvironmentForTest();

        addNewNode("NodeA");
        addNewNode("NodeB");
        addNewNode("NodeC");
        addNewNode("NodeD");

        runGetProcess();

        testForPercent(20.00F, 30.00F);

        addNewNode("NodeE");
        addNewNode("NodeF");
        addNewNode("NodeG");
        addNewNode("NodeH");

        runGetProcess();
        testForPercent(10.00F, 15.00F);
    }

    @Test
    public void removeNodesFromTheCluster() {
        setEnvironmentForTest();

        addNewNode("NodeA");
        addNewNode("NodeB");
        addNewNode("NodeC");
        addNewNode("NodeD");

        runGetProcess();
        testForPercent(20.00F, 30.00F);

        addNewNode("NodeE");
        addNewNode("NodeF");
        addNewNode("NodeG");
        addNewNode("NodeH");

        runGetProcess();
        testForPercent(10.00F, 15.00F);

        removeNode(7);
        removeNode(5);
        removeNode(3);
        removeNode(1);

        runGetProcess();
        testForPercent(20.00F, 30.00F);

    }

    private void testForPercent(float MinPossiblePercent, float MaxPossiblePercent) {
        for (Node node : nodeList) {
            float p = (float) node.getKeyCount() * 100 / GET_KEYS;
            String s = String.format("%.2f", p);
            assertTrue("Node " + node.getName() + " is idle: " + node.getKeyCount() + " (" + s + ")", p > MinPossiblePercent);
            assertTrue("Node " + node.getName() + " is overloaded: " + node.getKeyCount() + " (" + s + ")", p < MaxPossiblePercent);
        }
    }

    private void runGetProcess() {
        if (nodeList.isEmpty()){
            throw new IllegalStateException("Cluster is empty");
        }
        for (Node node : nodeList) {
            node.setZeroCount();
        }

        for (long i = 0; i <= GET_KEYS; i++) {
            cluster.get(i).incKeyCount();
        }
    }

    private void addNewNode(String nodeName) {
        Node node = new Node(nodeName);
        nodeList.add(node);
        cluster.add(node);
    }

    private void removeNode(int nodeIndex) {
        Node node = nodeList.remove(nodeIndex);
        cluster.remove(node);

    }

    private void setEnvironmentForTest() {
        nodeList = new ArrayList<>();
        cluster = new ConsistentHash<>(Hashing.murmur3_32(), VIRTUAL_NODES);
    }

}
