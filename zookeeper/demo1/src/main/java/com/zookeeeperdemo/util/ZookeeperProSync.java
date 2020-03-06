package com.zookeeeperdemo.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 2020/3/6
 * Create By 肖章明
 * zookeeper分布式配置中心
 */
public class ZookeeperProSync implements Watcher {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        //zookeeper配置数据存放路径
        String path =  "/username";
        //连接zookeeper并注册默认监听器
        zk = new ZooKeeper("101.200.138.100:2181",5000,new ZookeeperProSync());
        //等待zookeeper连接成功的通知
        countDownLatch.await();
        //获取Path目录节点 的配置数据，并注册默认的监听器
        System.out.println(new String(zk.getData(path,true,stat)));

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if(Event.KeeperState.SyncConnected == event.getState()){ //zookeeper成功连接
            if(Event.EventType.None == event.getType() && null == event.getPath()){
                countDownLatch.countDown();
            }else if(event.getType() == Event.EventType.NodeDataChanged){ //zookeeper节点发生变化
                try{
                    System.out.println("配置已修改为："+new String(zk.getData(event.getPath(),true,stat)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
