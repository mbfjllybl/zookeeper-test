package io.github.demo1;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributeLockTest {
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributeLock lock1 = new DistributeLock();
        final DistributeLock lock2 = new DistributeLock();



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.zkLock();
                    System.out.println("Thread 1 has been catch lock");
                    Thread.sleep(1000 * 5);
                    lock1.zkUnlock();
                    System.out.println("Thread 1 has been release lock");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // Thread.sleep(1000 * 5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("Thread 2 has been catch lock");
                    Thread.sleep(1000 * 5);
                    lock2.zkUnlock();
                    System.out.println("Thread 2 has been release lock");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
