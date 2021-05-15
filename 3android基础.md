# 1.序列化

**概念：**将数据结构或对象**转化为二进制串**的过程

**序列化方案：**

- serializable （externalizable，类似parcelable）
- parcelable （android独有）
- json/xml/protbuf



**serializable**

- serialVersionUID作用：通常是个哈希码，用于**对象版本控制**。
- 不指定serialVersionUID（会有一个默认值）：类中**字段**发生**变更**，已序列化的数据**无法**恢复。（新UID和旧UID不同-->InvalidClassException）
- transient：瞬态变量，字段**不会**被序列化。
- 序列化类中的引用类也必须序列化。



**serializable与parcelable比较**

- serializable通过io对硬盘操作，速度慢； parcelable在内存中操作，速度快。
- serializable大小不受限制； parcelable一般不超过1M。
- serializable大量使用反射，产生内存碎片。



# 2.json

**描述：**一种轻量级的数据交换格式。

gson/fastjson

gson

- @serializable：注解，转化key
- @Expose：是否参与序列化/反序列化
- 自定义TypeAdapter/自定义JsonDeserializer：解决解析异常



Gson原理

- 反射创建该类的对象
- 把json中对应的值赋值给对象属性
- 返回对象。



基于事件驱动解析：边读边解析。（区别于一次性读到内存中解析）

**JsonElement子类**

- JsonArray
- JsonNull
- JsonObject
- JsonPrimitive： java的基本类型

**TpyeToken**

**适配器模式：**eg 电源转换器

TypeAdapter： json串 <------> type

- 基本数据类型**子类**
- ReflectiveTypeAdapter



# 3.RxJava

响应式编程： 起点---步骤一-----步骤二（一的参数）------步骤三（二的参数）----> 终点 （不间断）

> 被观察者（observable）-------> 观察者（observer）

**核心思想：** 从起点开始，将事件流向终点。可以对事件拦截/改变，下一个拦截只关联上一个拦截。

**应用场景**

- rxjava配合retrofit
- 防抖（RxHandler）
- 网络嵌套
- doOnNext



**Rxjava方法：**

- map(new Function)
- flatMap(new Function)
- subscribeOn(...) / observeOn(...)

- doOnNext(new Consumer)
- subscribe(new Observer())



>  事件执行中（如网络请求中），关闭页面： onDestroy中要销毁 disposable.dispose() (*onSubscribe(Disposable d)*)



- **标准观察者模式**

  被观察者（拥有**集合容器**管理观察者）  ----   多个观察者

- **RxJava的观察者模式** 

  多个被观察者（create/map等创建多个Observable）， 一个观察者

  中间有一个抽象层（发射器，转换，onNext），降低耦合度。 （中间层装"包裹"，再拆"包裹"，包裹嵌套）



**Hook**：钩子，在流程执行中，插入新的执行。

```java
RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
  @Override
  public Scheduler apply(Scheduler scheduler) throws Exception {
    Log.i("xxx","全局监听  scheduler："+ scheduler);
    return scheduler;
  }
});
```



**线程切换：**最终都是交给线程池管理

- subscribeOn： 给上面的代码分配线线程
- observeOn： 给下面的代码分配线程

- 线程类型
  - Schedulers.io()：重用空闲的线程，比newThread效率更高。
  - Schedulers.newThread()：
  - Schedulers.single()
  - AndroidSchedulers.mainThread()：android主线程





# 4.IO

## 4.1 装饰模式 （功能增强----层层包裹增强）

```java
DataOutputStream out = new DataOutputStream( //基本类型数据的输出
    new BufferedOutputStream( //具备缓存功能
        new FileOutputStream( //向文件中写入数据
            new File(file)));
```



**Component**（抽象接口）

- ConcreateComponent：具体的构建对象，实现某个功能
- Decorator： 所有装饰器的抽象父类，继承并**持有抽象接口**
  - concreateDecoratorA ：实际的装饰对象，实现具体的功能 （可以传入 ConcreateComponent对象）
  - concreateDecoratorB ：实际的装饰对象，实现具体的功能



**InputStream** （抽象接口， read方法需要子类具体实现）

- ByteArrayInputStream （具体的组件对象）

- PipedInputStream （具体的组件对象）

- **FilterInputStream**    <====  装饰器的**抽象父类**    （仅仅增加构造函数，持有**InputStream**）

  - BufferedInputStream  <===实际的装饰器对象

    byte[1024]，一次性读取1024个字节（在调用磁盘一次读；否则一个字节读一次磁盘。）

  - DataInputStream          <===实际的装饰器对象

- FileInputStream

  File/ FileDescriptor

- ObjectInputStream

```java
File file = new File("/xx/xx.txt");
//嵌套流对象
DataInputStream dataInputStream = new DataInputStream(
  new BufferedInputStream(
    new FileInputStream(file)
  ));
boolean b = dataInputStream.readBoolean();
byte b1 = dataInputStream.readByte();
```



- 字节流：inputStream/outputStream           字节流中的**行**也就是一个**字节符号**

- 字符流：Reader/Writer.          **readLine()**



## 4.2 字符流：Reader/Writer

> 一个字符占用两个字节

```java
//字节流 转换为 字符流
BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(dataInputStream));
String str = "";
while ((str = bufferedReader.readLine()) != null){ <===============行的概念
  System.out.println(str);
}
```

FileWriter 继承 OutputStreamWriter 依赖 FileOutputStream 依赖 File

- 构造方法：OutputStreamWriter(OutputStream out); 
- 构造方法：OutputStreamWriter(OutputStream out,String CharSetName); 

```java
BufferedWriter out=new BufferedWriter(new OutputStreamWriter(System.out)); 
BufferedReader in= new BufferedReader(new InputStreamReader(System.in);
String line=in.readLine();
```



## 4.3 BufferedInputStream

- **缓冲区的输入流，默认缓冲区大小是8M**，**减少访问磁盘的次数**

- ```
  BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
  byte[] b = new byte[1024];
  while (bin.read(b, 0, b.length) != -1){
  	String s = new String(b);
  }
  ```



## 4.4 RandomAccessFile （随机位置文件访问）

-  从指定位置开始读，seek（起始位置）

- **网络数据下载**应用：断点续传（**分段** 进行进行读） 

  在**带宽不变**的情况下，多线程分段下载能提高性能

- 既可以读也可以写。

```java
RandomAccessFile rsfWriter = new RandomAccessFile(file, "rw");
//从10001开始存
rsfWriter.seek(10000);
```





## 4.5 FileChannel （NIO 管道）

- 配合ByteBuffer，操作速度更快

- 批量/缓存的方式read/write

- ```java
  FileInputStream inputStream;
  RandomAccessFile randomAccessFile;
  FileChannel channel1 = randomAccessFile.getChannel(); //获取FileChannel
  FileChannel channel = inputStream.getChannel();//获取FileChannel
  ```

- 效率 ≈  (Stream以byte数组方式)

```java
Instant begin = Instant.now(); 
RandomAccessFile randomAccessSourceFile = new RandomAccessFile(sourceFile, "r");
RandomAccessFile randomAccessTargetFile = new RandomAccessFile(targetFile, "rw"); 
FileChannel sourceFileChannel = randomAccessSourceFile.getChannel();
FileChannel targetFileChannel = randomAccessTargetFile.getChannel();
//ByteBuffer
ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
while(sourceFileChannel.read(byteBuffer) != -1) {
    byteBuffer.flip();
    targetFileChannel.write(byteBuffer);
    byteBuffer.clear();
}
System.out.println("use time: " + Duration.between(begin, Instant.now()).toMillis());
```



# 5.dex文件加密

apk（dex1）------> dex1 AES加密后的新dex + dex2（壳） ---->  apk(重新装入dex)------>签名成新apk



**dex文件**

- header： 文件头
- 索引区
  - string_ids：字符串的索引
  - type_ids：类型的索引
  - proto_ids：方法原型的索引
  - field_ids：域的索引
  - method_ids：方法的索引
- 数据区
  - class_defs：类的定义区
  - data：数据区
  - link_data：链接数据区



apk打包流程

hook：通过反射，截取Android本身运行流程，**插入执行自己逻辑后**，再继续运行系统流程。



# 6.Android虚拟机与类加载机制

- JVM：执行class文件 （一个文件，一个class） 

  基于栈的虚拟机

- Dalvik/ART：执行的dex文件（一个文件，多个class）

  基于寄存器的虚拟机：没有操作数栈，有很多虚拟寄存器（寄存器存放于运行时栈中，本质是数组） 。

  相比指令更少，移动次数减少。 

  

>  ASM插件等价于javap命令：查看class字节码 （build里面看到的class文件实际上是反编译过来的）



- Dalvik：执行dex字节码，解释执行。支持JIT**即时编译**（Just In Time），对热点代码进行编译或者优化。

- ART：5.0以上默认使用，执行的是本地机器码。

  ART引入了**预先编译机制**（Ahead Of Time），使用dex2oat工具编译应用。dex中字节码编译成本地机器码。

  

**Source Insight**：查看源码工具





- BootClassLoader
- PathClassLoader



**双亲委托机制**：类加载器在加载类时，先将加载任务委托给父类加载器，依次递归，如果父类（系统中的类）完成加载任务，则成功返回；否则自己（自己项目中所写的类）去加载。

- 避免重复加载：父类加载一次，子classLoader不会再次加载。
- 安全性考虑：防止核心api库被篡改。



android 热修复应用

- application中初始化加载 补丁包（xxx.dex）。加载了新修复类，就不会加载原apk中的bug 类。





**class打包dex**

- dx --dex --output=output.dex input.jar
- dx --dex --output=output.dex /com/xx/xx/A.class B.class





**github查看代码插件**：octotree













