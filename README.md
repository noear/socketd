<h1 align="center" style="text-align:center;">
  SocketD
</h1>
<p align="center">
	<strong>基于连接的可扩展消息传输协议</strong>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/socketd">
        <img src="https://img.shields.io/maven-central/v/org.noear/socketd.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
   <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/socketd/stargazers'>
        <img src='https://gitee.com/noear/socketd/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/socketd/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/socketd.svg?logo=github" alt="github star"/>
    </a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-870505482-orange"/></a>
</p>


<hr />



SocketD 是一个基于连接的、可扩展的、消息驱动的传输协议。具有异步，背压流控，双向通讯，多路复用，断线重连，支持签权，基于主题消息等特性。

* 具有语言无关性的二进制通信协议（支持 tcp, ws, udp）
* 异步非阻塞消息驱动通信
* 可以进行流量控制、自动连接恢复
* 支持双向通信（如：单链接双向 RPC 接口调用）
* 更加适合分布式通信场景
* 支持 ssl，支持国密 ssl
* 消息有由元信息和数据组成，通过元信息实现可扩展性
* 自动分片（数据超出 16Mb，会自动分片、自动重组）
* 接口简单

### 快速入门与学习

目前已完成 java 部分，学习可见：[《快速入门》](_docs/)

### 适用场景

可用于 MSG、RPC、IM、MQ，等一些的场景开发，可替代 http, websocket, grpc 等一些协议。比如移动设备与服务器的连接，比如一些微服务场景等等。


### 简单的协议格式

* codec

```
[len:int][flag:int][sid:str(<64)][\n][topic:str(<512)][\n][metaString:str(<4k)][\n][data:bts(<16m)]
```

* flag

| Flag      | Server                       | Client                       | 备注           |
|-----------|------------------------------|------------------------------|--------------|
| Unknown   | ::close()                    | ::close()                    |              |
| Connect   | /                            | c(Connect)->s                |              |
| Connack   | s(Connack),s::c.onOpen()->c  | s(Connack)->c::onOpen()      |              |
| Ping      | /                            | c(Ping)->s                   |              |
| Pong      | s(Pong)->c                   | /                            |              |
| Close     | s(Close)->c                  | c(Close)->s                  | 用于特殊场景（如：T人） |
| Message   | s(Message)->c                | c(Message)->s                |              |
| Request   | s(Request)->c(Reply)->s      | c(Request)->s(Reply)->c      |              |
| Subscribe | s(Subscribe)->c(Reply?..)->s | c(Subscribe)->s(Reply?..)->c |              |
| Reply     | s(Reply)->c                  | c(Reply)->s                  |              |
| ReplyEnd  | s(ReplyEnd)->c               | c(ReplyEnd)->s               | 结束答复         |




