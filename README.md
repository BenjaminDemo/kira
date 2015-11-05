# kira

kira 是一个Java开发的HTTP服务器，使用了NIO和多线程技术实现。

# 使用

```java
Kira kira = new Kira(8089);
kira.addHandler(new StaticFileHandler("D:/hello"));
kira.start();
```

打开浏览器，输入 [http://127.0.0.1:8089/index.html](http://127.0.0.1:8089/index.html)