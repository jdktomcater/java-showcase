# showcase-hashmap

用于验证 `HashMap` 在多线程并发写入时不是线程安全的。

运行命令：

```bash
mvn -pl showcase-hashmap exec:java
```

可选参数依次是：轮数、线程数、每个线程写入数量。

```bash
mvn -pl showcase-hashmap exec:java -Dexec.args="200 64 20000"
```

输出中的 `HashMap expected != actual`、写入异常或超时，都说明并发写入破坏了 `HashMap` 的内部状态或导致数据丢失。`ConcurrentHashMap` 和 `Collections.synchronizedMap` 是对照组。

默认参数偏保守，通常会复现数据丢失并较快结束。提高线程数和写入量时，可能观察到线程超时或 Maven exec 插件提示写线程无法中断，这是更严重的并发破坏现象。
