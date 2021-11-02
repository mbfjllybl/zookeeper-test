package io.github.demo1;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributeLock {
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String rootNode = "locks";
    private String subNode = "seq-";

    private String waitPath;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    private String currentNode;

    public DistributeLock() throws KeeperException, InterruptedException, IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });
        connectLatch.await();
        Stat stat = zk.exists("/" + rootNode, false);

        if (stat == null) {
            System.out.println("Node root not exit");
            zk.create("/" + rootNode, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void zkLock() {
        try {
            currentNode = zk.create("/" +rootNode + "/" + subNode,  null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            Thread.sleep(10);

            List<String> childrenNodes = zk.getChildren("/" + rootNode, false);

            if (childrenNodes.size() == 1) {
                return;
            } else {
                Collections.sort(childrenNodes);
                String thisNode = currentNode.substring(("/" + rootNode + "/").length());
                System.out.println(thisNode);
                int index = childrenNodes.indexOf(thisNode);
                if (index == -1) {
                    System.out.println("error");
                } else if (index == 0) {
                    return;
                } else {
                    this.waitPath = "/" + rootNode + "/" + childrenNodes.get(index - 1);
                    zk.getData(waitPath, true, new Stat());

                    waitLatch.await();
                    return;
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void zkUnlock() throws KeeperException, InterruptedException {
        zk.delete(this.currentNode, -1);
    }
}
