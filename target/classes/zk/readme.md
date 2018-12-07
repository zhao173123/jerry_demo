zk的getData(),getChildren(),exists()都可以注册监听Watcher，监听有以下的几个特点：
1.一次性触发（one-time trigger）
当数据改变时一个Watch事件会产生并且被发送到客户端；但是客户端只会接收到一次这样的通知，
如果以后这个数据再次发生改变，之前设置的Watch客户端将不再接收到改变的通知。
2.发送客户端（send to the client）
表明了Watch的通知事件是从服务端异步发送给客户端的，即不同客户端收到消息Watch的时间可能不同；
但是zk保证了当一个客户端看到Watch事件之前是不会看到节点数据变化的。

zk的客户端和服务端通过Socket通信，由于网络等原因，监听事件不是必达；监听事件是异步发送至监视者的，
zk提供了保序性（order guarantee），即客户端只有首先看到了监听事件之后才会感知它所设置监视的
znode发生了变化（a client will never see a change for which it has set a watch until 
it first sees the watch event）；
********************************************************************************
Curator有2种选取领导者的方式Leader Latch & Leader Election
Leader Latch:随机选中一台，选中之后除非调用close()释放leadership，否则其他的后续选择无法成为leader。
			 典型代表spark。
Leader Election:可以对领导权进行控制，在适当的时候释放领导权，每个节点都有机会获得领导权。
			

