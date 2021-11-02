package io.github.fujang;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class TestZkClient {
    private static String connectString = "127.0.0.1:2181";
    private static int sessionTimeout = 2000;
    private static ZooKeeper zkClient = null;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
        Stat exists = zkClient.exists("/fujang", false);
        System.out.println(exists);
    }
}
