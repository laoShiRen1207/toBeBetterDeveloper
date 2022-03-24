package com.laoshiren.hello.juc.concurrent.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: juc
 * @package: com.laoshiren.hello.juc.concurrent.container
 * @className: MapMain
 * @author: laoshiren
 * @mail: xiangdehua@pharmakeyring.com
 * @description:
 * @menu:
 * @date: 2022/3/23 14:52
 */
public class MapMain {

    public static void main(String[] args) {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("a", "a");
    }

    /*

final V putVal(K key, V value, boolean onlyIfAbsent) {
    //键值不能为空，基操
    if (key == null || value == null) throw new NullPointerException();
    //计算键的hash值，用于确定在哈希表中的位置
    int hash = spread(key.hashCode());
     //一会用来记录链表长度的，忽略
    int binCount = 0;
    //无限循环，而且还是并发包中的类，盲猜一波CAS自旋锁
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f;
        int n, i, fh;
        if (tab == null || (n = tab.length) == 0) {
         //如果数组（哈希表）为空肯定是要进行初始化的，然后再重新进下一轮循环
            tab = initTable();
        }
        //如果哈希表该位置为null，直接CAS插入结点作为头结即可（注意这里会将f设置当前哈希表位置上的头结点）
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
             // 如果CAS成功，直接break结束put方法，失败那就继续下一轮循环
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null))) {
                break;
            }
        }
        //头结点哈希值为-1，这里只需要知道是因为正在扩容即可
        else if ((fh = f.hash) == MOVED)  {
        //帮助进行迁移，完事之后再来下一次循环
            tab = helpTransfer(tab, f);
        }
        //特殊情况都完了，这里就该是正常情况了，
        else {
            V oldVal = null;
            //在前面的循环中f肯定是被设定为了哈希表某个位置上的头结点，这里直接把它作为锁加锁了，防止同一时间其他线程也在操作哈希表中这个位置上的链表或是红黑树
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                //头结点的哈希值大于等于0说明是链表，下面就是针对链表的一些列操作
                    if (fh >= 0) {
                        ...实现细节略
                    }
                    //肯定不大于0，肯定也不是-1，还判断是不是TreeBin，所以不用猜了，肯定是红黑树，下面就是针对红黑树的情况进行操作
                    else if (f instanceof TreeBin) {
                      	//在ConcurrentHashMap并不是直接存储的TreeNode，而是TreeBin
                        ...实现细节略
                    }
                }
            }
          	//根据链表长度决定是否要进化为红黑树
            if (binCount != 0) {
                if (binCount >= TREEIFY_THRESHOLD) {
                //注意这里只是可能会进化为红黑树，如果当前哈希表的长度小于64，它会优先考虑对哈希表进行扩容
                    treeifyBin(tab, i);
                }
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    addCount(1L, binCount);
    return null;
}

     */
}
