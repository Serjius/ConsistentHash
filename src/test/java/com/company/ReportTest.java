package com.company;

import com.company.utils.Statistics;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pss on 11.06.17.
 * Report tests for consistent hash cluster.
 *
 * Calculate simple statistic (expected average, median, stddev) for:
 *      different hash functions
 *      detailed tests for specific hash function
 *      different cluster size.
 *
 * Like:
 * ================================================
 * Function:Hashing.murmur3_32(0) Nodes:313 Virtual nodes:10,000 (total nodes:3,130,000) Cluster build time 6.5784 sec
 * It took 1,731,566,409 ns (1.7316 sec) to test 1,000,000 keys. 1731.5664 ns per key. 577511.7805 keys per sec
 * Keys Per Node: Expected 3194.89 (0.32 %) keys. Actual median = 3193.00 (0.32 %) StdDev = 67.6834 keys.
 */
public class ReportTest {
    private int GET_KEYS = 1_000_000;
    private List<Node> nodeList;
    private ConsistentHash<Node> consistentHash;

    @Test
    public void reportDifferentHashFunction() {
        runForReport(313, Hashing.murmur3_32(), 10_000);
        runForReport(313, Hashing.murmur3_128(), 10_000);
        runForReport(313, Hashing.sha1(), 10_000);
        runForReport(313, Hashing.sha256(), 10_000);
        runForReport(313, Hashing.sha512(), 10_000);
        runForReport(313, Hashing.md5(), 10_000);
    }


    @Test
    public void reportMurmur32() {
        runForReport(16, Hashing.murmur3_32(), 10);
        runForReport(16, Hashing.murmur3_32(), 100);
        runForReport(16, Hashing.murmur3_32(), 1_000);
        runForReport(16, Hashing.murmur3_32(), 10_000);
        runForReport(16, Hashing.murmur3_32(), 100_000);
    }

    @Test
    public void reportMurmur128() {
        runForReport(16, Hashing.murmur3_128(), 10);
        runForReport(16, Hashing.murmur3_128(), 100);
        runForReport(16, Hashing.murmur3_128(), 1_000);
        runForReport(16, Hashing.murmur3_128(), 10_000);
        runForReport(16, Hashing.murmur3_128(), 100_000);
    }

    @Test
    public void reportGrowNodes() {
        runForReport(3, Hashing.murmur3_32(), 1_000);
        runForReport(10, Hashing.murmur3_32(), 1_000);
        runForReport(100, Hashing.murmur3_32(), 1_000);
        runForReport(300, Hashing.murmur3_32(), 1_000);
        runForReport(1000, Hashing.murmur3_32(), 1_000);
        runForReport(3000, Hashing.murmur3_32(), 1_000);
    }

    private void runForReport(int nodeCount, HashFunction hashFunction, int NumberOfVirtualNodes) {
        runForReport(nodeCount, hashFunction, NumberOfVirtualNodes, false);
    }

    private void runForReport(int nodeCount, HashFunction hashFunction, int NumberOfVirtualNodes, boolean showNodeStatistic) {
        if (nodeCount < 1 || nodeCount > 10000) {
            throw new IllegalArgumentException("Node number must be 1-10000");
        }

        nodeList = new ArrayList<>();
        consistentHash = new ConsistentHash<>(hashFunction, NumberOfVirtualNodes);

        long initClusterBegin = System.nanoTime();
        for (int i = 0; i < nodeCount; i++) {
            addNewNode("Node" + i);
        }
        long initClusterEnd = System.nanoTime();

        long initClusterTime = initClusterEnd - initClusterBegin;
        long runTime = runGetProcess();
        reportForRun(showNodeStatistic, initClusterTime, runTime);
    }

    private void reportForRun(boolean showNodeStatistic, long initClusterTime, long runTime) {
        System.out.println("================================================");
        System.out.println(
                "Function:" + consistentHash.getHashFunction()
                        + " Nodes:" + String.format("%,d", nodeList.size())
                        + " Virtual nodes:" + String.format("%,d", consistentHash.getNumberOfReplicas())
                        + " (total nodes:"+ String.format("%,d", consistentHash.getNumberOfReplicas()*nodeList.size())+")"
                        + " Cluster build time "+ String.format("%.4f", (double)initClusterTime*1e-9)+ " sec"
                       );
        System.out.println(
                "It took "+ String.format("%,d", runTime)
                        +" ns (" + String.format("%.4f", (double)runTime*1e-9)
                        +" sec) to test "+String.format("%,d", GET_KEYS)
                        +" keys. " +String.format("%.4f", (double)runTime/GET_KEYS)
                        +" ns per key. "+String.format("%.4f", (double)GET_KEYS*1e9/runTime)
                        +" keys per sec"
        );

        long[] anArray = new long[nodeList.size()];
        int i = 0;

        for (Node node : nodeList) {
            if (showNodeStatistic) {
                float p = (float) node.getKeyCount() * 100 / GET_KEYS;
                String s = String.format("%.2f", p);
                System.out.println("Node " + node.getName() + " was gotten: " + node.getKeyCount() + " (" + s + " %)");
            }
            anArray[i] = node.getKeyCount();
            i++;
        }
        Statistics st = new Statistics(anArray);
        double expectedPercent = (double) GET_KEYS / nodeList.size();
        double median = st.getMedian();
        System.out.println(
                "Keys Per Node: Expected " + String.format("%.2f", expectedPercent)
                        + " (" + String.format("%.2f", expectedPercent * 100 / GET_KEYS) + " %)"  +" keys."
                        + " Actual median = " + String.format("%.2f", median)
                        + " (" + String.format("%.2f", median * 100 / GET_KEYS) + " %)"
                        + " StdDev = " + String.format("%.4f", st.getStdDev())+" keys."
        );
    }

    private long runGetProcess() {
        for (Node node : nodeList) {
            node.setZeroCount();
        }

        long getByKeyBegin = System.nanoTime();
        for (long i = 0; i <= GET_KEYS; i++) {
            consistentHash.get(i).incKeyCount();
        }
        long getByKeyEnd = System.nanoTime();

        return getByKeyEnd - getByKeyBegin;
    }

    private void addNewNode(String nodeName) {
        Node node = new Node(nodeName);
        nodeList.add(node);
        consistentHash.add(node);
    }

    private void removeNode(int nodeIndex) {
        Node node = nodeList.remove(nodeIndex);
        consistentHash.remove(node);

    }
}
