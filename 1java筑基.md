讲给不懂的人/写博客



# 1 深入泛型

 泛型： jdk 1.5 。  

- **作用**：编译期进行检测； 增加代码复用性

- **应用：**泛型类/ 泛型接口/ 泛型方法

  继承类或者接口（要么继续申明，要么写具体的类型）

```kotlin
class Person<T, V>{
  fun getNums(t: T, v: V): HashMap<T, V>{
    return HashMap<T, V>()
  }

  fun <N> getNums2(n: N): N{
    return n
  }
}
```

```java
//泛型方法    
class Test{
  //泛型方法  
  public <T> T test(T t){
    return t;
  }

  public void main() {
    Test test =new Test();
    //泛型方法  
    test.<Integer>test(1);
    test.test(1);
  }
}
```



受限泛型：  <T, S, U, V, N **extends** Long & Number>   (只能一个类的限定，其他可以接口)

不能有继承关系：Person<Long> --> Person<Number>

<? extends Number>  :  使用Int 型就会报错。因为它只知道**最大上限**是Number，可能是Int/Long/Short...

**类型擦除**：编译器会将泛型 变换成 对应具体的类型。  (**JVM 不支持泛型**)



**查看字节码**：

- 查看字节码工具：**ASM** Bytecode Viewer  (插件)

- javac   xxxx.java (编译后，用该命令查看class：javap -c xxxx.class)



类中不能使用    *静态变量泛型*，*静态方法泛型*

> 问？
>
> Set 和 Set<?>    : 后者会进行类型检查

```java
public void test1(Set set){
  set.add(1);
  for (Object o : set) {
    System.out.println(o.toString());
  }
}
public void test2(Set<?> set){
  set.add(1); //报错
  for (Object o : set) {
    System.out.println(o.toString());
  }
}
```



Java 泛型的 PESC原则

- List<? extends T> :  PE，生产者，拿数据，只读  （T是父， 只知道最大类型，肯定只能读，因为你知道写哪个子吗？）
- List<? super T>   ： SC，消费者，存数据，只写  （T是子， 只知道最小类型，肯定只能写）

```java
public <T> void test3(List<? extends T> list){
  T t = list.get(0);
  list.add(t); //报错
}
public  void test3_1(List<? extends Number> list){
  Number t = list.get(0);
  list.add(t); //报错
}
public <T> void test4(List<? super T> list){
  T t = list.get(0); //报错
  list.add(t); 
}
public <T> void test4_1(List<? super Number> list){
  Number t = list.get(0); //报错
  list.add(t);
}
```





# 2 注解（打标记）

元注解： 注解上的注解 （@Target- 哪些地方/  @Retention - 保留在哪个阶段）

>  @IntDef 也是元注解  （@Target({ANNOTATION_TYPE})），语法检查， 作用在自定义注解上
>
> @DrawableRes int id   检查参数是否是资源id

```java
@Target(ElementType.TYPE, ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)  //保留注解到什么时候
public @interface Name {
    int value(); //默认不写key
    int age();  //需要写key
}
 
```

```java
    final static int ONE = 0;
    final static int TWO = 1;

    //检查
    @CheckMyNum int num = ONE;
    //检查
    public void setNum(@CheckMyNum int num){
        this.num = num;
    }

    @IntDef({ONE, TWO})//元注解
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.SOURCE)  //源码级别，IDE实现的语法检查
    @interface CheckMyNum{

    }
```





Retention （级别小->大）

- source    ： 保留在原码                              APT：可以生产额外的辅助类
- class       ：  保留在 class 字节码               字节码增强
- runtime    ：保留在运行时。                     运行时通过  反射  处理一些逻辑，JVM保留



> APT： annotation  processor  tools  （注解处理器）

> Android studio创建普通的Java模块    ：  apply plugin: 'java-library'

> android studio 编译时： 会做成很多个task任务进行编译   Task:app:processDebugResources UP-TO-DATE

>  字节码增加： 在字节码中写代码。

> 运行：javac  -classpath  xxxx.jar   aaa.java  bbb.java



## 2.1 注解结合反射

>  查看某个项目进程占用： 任务管理器（性能）---> 打开资源监视器--->管理的句柄  （输入项目名称）

获取构造器：

- getConstructors() 
- ...

获取类的成员变量：

- getFileds()       - 获得类所有的公共字段
- getDeclaredFields()   - 获得类声明的所有字段
- ...

获取调用方法：

- getMethods()
- ...

```java
    //实例一：实现功能，注解获取资源id对应的view
    @InjectView(R.id.textview)
    TextView textView;
    
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface InjectView{
        @IdRes int value();
    }

    static class InjectUtils{
        public static void inject(Activity activity){
            Class<? extends Activity> activityClass = activity.getClass();
            Field[] declaredFields = activityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                //获取注解
                if (field.isAnnotationPresent(InjectView.class)) {
                    InjectView fieldAnnotation = field.getAnnotation(InjectView.class);
                    //获取资源id
                    int id = fieldAnnotation.value();
                    View view = activity.findViewById(id);
                    field.setAccessible(true);
                    try {
                        //设置给view
                        field.set(activity, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                
                
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注入activity       
        InjectUtils.inject(this);
    }
```

```java
	//实例一： 实现页面跳转，跳转后注解获取参数
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Autowired{
        String value();
    }
    static class FirstAct extends Activity{
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(this, SecondAct.class);
            intent.putExtra("key1", 1);
            intent.putExtra("key2", "2");
            intent.putExtra("key3", true);
            startActivity(intent);
        }
    }

    static class SecondAct extends Activity{
        @Autowired("key1")
        int v1;
        @Autowired("key2")
        String v2;
        @Autowired("key3")
        boolean v3;
        @Autowired("key4")
        UserParcelable[] v4;  //数组 并且 是 Parcelable（子类）数组

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //传统方式
//            v1 = getIntent().getStringExtra("key1");
            AutowiredUtils.init(this);
        }
    }

    static class AutowiredUtils {
        public static void init(Activity activity) {
            Intent intent = activity.getIntent();
            Bundle bundle = intent.getExtras();
            Class<? extends Activity> activityClass = activity.getClass();
            Field[] declaredFields = activityClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    //key
                    String key = annotation.value();
                    Class<?> componentType = field.getType().getComponentType();
                    Object value = bundle.get(key);
                    //当前属性是数组并且是 Parcelable（子类）数组 (Parcelable数组类型不能直接设置，其他的都可以)
                    if (field.getType().isArray() && Parcelable.class.isAssignableFrom(componentType)) {
                        Object[] objects = (Object[]) value;
                        //创建对应类型的数组并由objs拷贝
                        Arrays.copyOf(objects, objects.length, (Class<? extends Object[]>) field.getType());
                        value = objects;
                    }
                    field.setAccessible(true);
                    try {
                        field.set(activity, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
```



## 2.2 手写retrofit

代理模式：

- 静态代理

  网络请求--------**代理对象**（隔离层，统一抽象方法get/post...）-------封装层（volley）

  ​															                                             -------封装层（okhttp）

  缺点：多个代理对象

- 动态代理

  同一个代理对象，处理很多功能。

```java
public class TestProxy {
    interface Fruit{
        void eat(String name);
    }
    interface Fruit2{
        void eat2(String name);
    }
    static class Orange implements Fruit{
        @Override
        public void eat(String name) {
            System.out.println(name + "  eat  Orange");
        }
    }
    static class Apple implements Fruit{
        @Override
        public void eat(String name) {
            System.out.println(name + "  eat  Apple");
        }
    }

    static class FruitClient {
        public void printFruit(Fruit fruit){

        }
    }

    public static void testProxy(){
        final Orange orange = new Orange();
        //代理对象
        Object instance = Proxy.newProxyInstance(
                TestProxy.class.getClassLoader(),
                new Class[]{Fruit.class, Fruit2.class},  //多个接口
                new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                System.out.println("11111");
                Object invoke = method.invoke(orange, objects[0]);  //代理对象持有对象
                System.out.println("22222");
                return invoke;
            }
        });
        Fruit fruit = (Fruit) instance;
        fruit.eat("zdf");
        //
//        Apple apple = (Apple) instance;
//        apple.eat("zdf");
    }

    public static void main(String[] args) {
        TestProxy.testProxy();
    }
}
```



**动态代理+注解+反射**



```java
public class XRetrofit {
    final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap();
    Call.Factory callFactory;
    HttpUrl baseUrl;

    public XRetrofit(HttpUrl httpUrl, Call.Factory factory) {
        this.callFactory = factory;
        this.baseUrl = httpUrl;
    }

    /**
     * WeatherApi weatherApi = retrofit.create(WeatherApi.class);
     *
     */
    public <T> T create(Class<T> cls){
        /**
         * 生成一个代理类，代理接口对应的方法执行，
         * 如发出某个请求，对应的请求参数需要解析出来，最终发出请求。并返回
         */
        Object instance = Proxy.newProxyInstance(cls.getClassLoader(),
                new Class[]{cls}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        /**
                         * method 就是接口中的方法
                         * 解析方法上所有的注解信息
                         */
                        ServiceMethod serviceMethod = loadServiceMethod(method);
                        //执行被代理类的对应方法，及相应的参数
                        return serviceMethod.invoke(objects);
                    }
                });
        return (T) instance;
    }

    /**
     * 缓存，避免每次读取方法上的参数注解等
     * @param method
     */
    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod serviceMethod = serviceMethodCache.get(method);
        if (serviceMethod != null) {
            return serviceMethod;
        }
        synchronized (serviceMethodCache){
            serviceMethod = serviceMethodCache.get(method);
            if (serviceMethod == null) {
                serviceMethod = new ServiceMethod
                        .Builder()
                        .init(this, method)
                        .build();
                serviceMethodCache.put(method, serviceMethod);
            }
        }
        return serviceMethod;
    }

    /**
     * 构建者模式，将一个复杂对象构建分离。
     */
    public static final class Builder{
        HttpUrl httpUrl;
        Call.Factory factory;
        public Builder baseUrl(String url){
            httpUrl = HttpUrl.get(url);
            return this;
        }

        public Builder callFactory(Call.Factory factory){
            this.factory = factory;
            return this;
        }

        public XRetrofit build(){
            if(httpUrl == null){
                throw  new IllegalStateException("xxxx");
            }
            //构建默认请求
            if(this.factory == null){
                this.factory = new OkHttpClient();
            }
            return new XRetrofit(httpUrl, factory);
        }
    }
}
```





# 3 进程/线程(并发)

## 3.1 线程基础



>  线程存在于操作系统中，不仅仅是Java

- 进程：系统调用的最小单位

  分配 cpu/内存/磁盘io

- 线程：cpu调用的最小单位

  内核数与线程数： 一比一 （逻辑处理器，2倍）

  RR调度： cpu时间片轮转 机制



- 并行（各自执行）：同时运行的任务数
- 并发（交替执行）：单位**时间**内，处理的任务数

os限制： linux 分配1000个线程， windows 分配2000个    (线程池来控制线程数量)。

句柄： 系统分配了一段连续的内存空间， 指向这个内存空间的叫做句柄。



> jdk 线程是协作式， 不是强制式
>
> **Thread.currentThread()  : 获取当前线程**



创建线程：一种：Thread； 二种：Runnable

- stop() （过时）: 不建议使用，方式野蛮，直接停止线程操作。如写文件时，中途停止，文件未写完整。

- interrupt()（推荐）: 标识线程中断，标识位， 实际不一定是停止线程。
- isInterrupted(): 判断是否停止  while(isInterrupted()){...} ，  **不建议使用** 设置boolean 标识 while(cancel){...}
- interrupted(): 判断是否停止 （会修改标识为 true）



sleep捕获异常，需要interupt()， 外部不会让线程中断。

```java
static class T extends Thread{
        @Override
        public void run() {
            System.out.println("run...2-1 "+ isInterrupted());
            //没有打断
            while (!isInterrupted()){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //需要打断，否则while会一直执行下去
                    interrupt();
                    System.out.println("run...sleep   "+ isInterrupted());
                }
                System.out.println("run...1   "+ isInterrupted());
            }
            System.out.println("run...2-2   "+ isInterrupted());
        }
    }
```



- run()    : 主线程执行

- start()  : 启动一个子线程执行

- yield():  从cpu中让出执行权，转为就绪状态。    **不会让出锁**

- join():    获取执行权，  但是有顺序的执行（串行）   （如何让2个线程有顺序的执行？使用join）

- setPriority():  线程优先级

- setDaemon():  设置为守护线程。  **当前线程完毕后，守护线程停止**

  当前线程外， 设置了setDaemon的线程 都是守护线程。

  

  **线程状态交互：**

   ![img](https://github.com/zhouffan/senior-engineer/raw/master/image/%E7%BA%BF%E7%A8%8B%E4%BA%A4%E6%9B%BF%E6%89%A7%E8%A1%8C.png)

线程间的共享

 多个线程对同一个资源访问 ===> 加锁 (方法加锁，对象加锁)

静态方法上内加锁===>  

注： 相同锁， 多线程串行；  锁不同，多线程并行



**死锁：**

- 多个操作者（线程）
- 争夺多个资源



>  **volatile：** 最轻量的线程同步机制，可见性。（旧值**变成**新值时，马上被其他线程看到） ，不能保证线程安全。  **一写多读**的场景下使用。
>
>  ​	- 可见性：对一个volatile变量的读，能看到其他线程对这个变量最后的写入。（强制刷新值同步回主内存，强制其他读线程去读。**没有写的情况下，是各自工作线程从主内存中拷贝一个副本并进行各自操作**）
>
>  ​	- 只能保证可见性，不能保持**原子性**（复合操作（自增）。 要么一起成功，要么都不成功）
>
>  **synchronized**： 独占锁， 保证 **可见性+原子性**
>
>  **ThreadLocal:** 每个线程都有遍历的副本，线程隔离。   拥有一个线程独有的   TheadLocalMap （内含Entry[]）
>
>  ​						threadLocal使用后， 不remove，会发生内存泄漏 。
>
>  ​						没有使用好，也会线程不安全。



**可见性与原子性**

- **可见性：**指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。

  由于线程对变量的所有操作都必须在**工作内存中进行**，而**不能直接读写主内存中**的变量，那么对于共享变量V，它们首先是在自己的工作内存，之后再**同步到主内存**。可是并**不会及时**的刷到主存中，而是会有一定时间差。很明显，这个时候线程 A 对变量 V 的操作对于线程 B 而言就不具备可见性了 。

  要解决共享对象可见性这个问题，我们可以使用**volatile关键字**或者是**加锁**。

- **原子性：**即一个操作或者多个操作 要么全部执行并且执行的过程不会被任何因素打断，要么就都不执行。

  - **“时间片”**：CPU资源的分配都是**以线程为单位**的，并且是**分时调用**，操作系统允许某个进程执行一小段时间，例如 50 毫秒，过了 50 毫秒操作系统就会**重新选择一个进程**来执行（我们称为“任务切换”），这个 50 毫秒称为**“时间片”**。

  - **线程切换问题原理**：线程切换为什么会带来bug呢？因为操作系统做任务切换，可以发生在**任何一条CPU 指令执行完**！，CPU 指令，而不是高级语言里的一条语句。

    ```
    count++;  //一条java语句，对应多条 cpu指令
    ```



**volatile：**把对volatile变量的**单个**读/写，看成是使用同一个**锁**对这些**单个读/写**操作做了同步

- 可见性：对一个volatile变量的读，总是能看到（任意线程）对这个volatile变量最后的写入。

- 原子性：对任意**单个**volatile变量的读/写具有原子性。能保证执行完**及时把变量刷到主内存**中
- 但对于count++这种非原子性、多指令的情况。由于线程切换，线程A刚把count=0加载到工作内存，线程B也拿到0（执行++ =>1），这样就会导致线程A和B执行完的结果都是1，都写到主内存中，主内存的值**还是1**不是2

- **原理：**该修饰的变量进行写时，会使用cpu提供的Lock前缀指令，可以理解为CPU指令级的一种锁

  - 将当前处理器缓存行的数据写回系统内存
  - 这个写回操作会使其他cpu缓存了**该内存地址的数据无效**。重新来读取。

  



**引用分类**

- 强引用
- 软引用：softReference      gc时， 内存不足时， 才会回收。
- 弱引用：weakReference     gc时，不管内存是否充足，都会回收。
- 虚引用： 



线程之间的协作：

- wait()      : object的方法，  写在syn 同步块或者方法中。 //释放锁
- notify()/ **notifyAll()**    : object的方法， 写在syn 同步块或者方法中

```java
//等待
syn(对象){
	while(条件不满足){
		对象.wait()  //释放锁
	}
	//业务
}
//通知
syn(对象){
	//业务， 改变条件
	对象.notify()/notifyAll()
}
```



object o = new object()

o = null;   (代表栈指向空，但是对应的堆对象还存在，等待gc回收)



> fork/join:  分而治之 ， 大任务拆分(fork)成若干小任务， 再将小任务的直接结果汇总（join）





## 3.2 CAS （compare and swap）（无锁算法）



> 原子操作： 不可分，要么全部做，要么一个都不做

**AtomicBoolean使用**: https://zhuanlan.zhihu.com/p/87614101



- 目的：实现原子操作（synchronized也能实现原子操作）

  synchronized缺点：

  - 被阻塞的线程优先级很高很重要怎么办？
  - 获得锁的线程一直不释放锁怎么办？
  - 如果有大量的线程来竞争资源，那CPU将会花费大量的时间和资源来处理这些竞争。会出现如死锁等情况。
  - **锁机制**是一种比较粗糙，粒度比较大的机制，相对于像计数器这样的需求有点儿过于笨重。

-  释义1：**比较再交换**，java.util.concurrent.*,其下面的类直接或者间接使用CAS算法实现，区别synchronouse同步锁的一种乐观锁。 

- 释义2：CPU指令级的操作，只有一步原子操作，非常快。避免了请求操作系统来裁定锁的问题，直接在CPU执行。

- 原理： 利用现代处理器都支持的CAS的指令，  **循环**这个指令，直到成功为止。  （自旋：类似死循环，长时间不成功会带来cpu高开销）。

- get变量值（旧值）--->计算后得到新值---> compare**内存中变量值**和**旧值**---->如果相等-----旧值swap为新值

  ​																														   ---->如果不相等，从头再来一次
  
  ​																				    		(如果一个线程一直不相等，则最后一次也会相等)
  
-  **思想：**获取当前变量最新值A（预期值），然后进行CAS操作。此时如果内存中变量的值V（内存值V）等于预期值A，**说明没有被其他线程修改过**，我就把变量值改成B（更新值）；如果不是A，便不再修改。（被其他线程修改过，则重新循环）



> eg:  count++ 多个线程来操作  A：0-->1
>
> CAS接收三个参数（1. count的内存地址; 2. 期望的值(旧值，count 0); 3. 新值（1））
>
> 当执行时，**比较**和**交换**是不能被外部线程打断： 原子性



缺点：

- ABA问题： 中间已经改过了。
- 开销问题：不停重试。
- 只能保证一个共享变量的原子操作。  （如果内部有个变量修改，就需要syn）



JDK中相关原子操作类（java.util.concurrent.atomic，提供一种高效的CAS操作）：

- 更新基本类型类： AtomicBoolean, AtomicInteger, AtomicLong
- 更新数组类： AtomicIntegerArray， AtmoIntegerArray, AtomicReferenceArray
- 更新引用类型：AtomicReference, AtomicMarkableReference, AtomicStampedReference

syn： （一个线程操作，其他线程都得等待）



**ConcurrentHashMap** （线程安全，解决并发问题）

- jdk 1.8以前： Segment+ReentrantLock
- jdk 1.8以后： CAS+Synchronized

> 1.8 在 1.7 的数据结构上做了大的改动，采用红黑树之后可以保证查询效率（`O(logn)`），甚至取消了 ReentrantLock 改为了 synchronized，这样可以看出在新版的 JDK 中对 synchronized 优化是很到位的。



```java
private static Lock No13 = new ReentrantLock();//第一个锁    (独占锁,可重入)
private static Lock No14 = new ReentrantLock();//第二个锁

//先尝试拿No13 锁，再尝试拿No14锁，No14锁没拿到，连同No13 锁一起释放掉
private static void fisrtToSecond() throws InterruptedException {
    String threadName = Thread.currentThread().getName(); 
    while(true){
        if(No13.tryLock()){
            System.out.println(threadName +" get 13");
            try{
                if(No14.tryLock()){
                    try{
                        System.out.println(threadName +" get 14");
                        break;
                    }finally{
                        No14.unlock();
                    }
                }
            }finally {
                No13.unlock();
            }
        }
    }
}
```



**解决多线程并发问题：**

- 加synchronized 。锁机制
- threadLocal。副本，4个方法，get/set/remove/initialValue
- concurrent下的原子操作类。 concuuurentHashMap，AtomicBoolean...
- **volatile+CAS操作**：替换synchronized





**阻塞队列**

- 队列满了：再添加，会阻塞

- 队列空了：去取，会阻塞

**BlockingQueue**： 阻塞方法（put()/ take()），有阻塞方法也有非阻塞方法。

- 线程take()取值时，如果取不到值，会阻塞在那里。
- ArrayBlockingQueue : 由数组构成的有界阻塞
- LinkedBlockingQueue：由链表构成的有界阻塞
- PriorityBlockingQueue：支持优先级排序构成的无界阻塞
- DelayQueue：支持优先级排序构成的无界阻塞  (有效时间， 缓存时间控制)
- ...



生产者/消费者：

-  **中间建立一个容器**（阻塞队列），生产者和消费者各自执行各自（**解决**生产者和消费者的**强耦合**问题）
- 阻塞队列就相当于一个缓冲区，平衡生产者和消费者的处理能力。



|   方法   | 抛出异常 | 返回值 | 一直阻塞 | 超时退出           |
| :------: | -------- | ------ | -------- | ------------------ |
| 插入方法 | add      | offer  | **put**  | offer(e,time,unit) |
| 移除方法 | remove   | poll   | **take** | poll(time,unit)    |
| 获取方法 | element  | peek   | -        | -                  |

抛出异常：

- 当队列满时，如果再往队列里插入元素，会抛出IllegalStateException（"Queuefull"）异常。
- 当队列空时，从队列里获取元素会抛出NoSuchElementException异常。

## 3.3 线程池原理

**作用：**

- 降低资源消耗。重复利用已创建的线程  降低线程**创建**和**销毁**造成的消耗。
- 提高响应速度。只有执行时间（已创建线程的情况下）。
- 提高线程的可管理性。统一分配、调优和监控。



**一个线程所需要的资源时间**

- 创建时间
- 任务执行时间   （线程池就只包含了该时间，不需要重复创建/销毁）
- 销毁时间



Exceutor--ExecutorService--ThreadPoolExecutor 

```java
ThreadPoolExecutor(
  int corePoolSize,   //核心线程池数量
  int maximumPoolSize,  //线程池中允许的最大线程数。如果当前阻塞队列满了，且继续提交任务，则创建新的线程执行任务，前提是当前线程数小于maximumPoolSize （立即执行）
  long keepAliveTime,  //核心线程 保活时间
  TimeUnit unit,  //保活时间单位
  BlockingQueue<Runnable> workQueue, //阻塞队列， 超过核心数量后，加入到该队列中
  ThreadFactory threadFactory,  //设置，线程名、守护线程。Executors静态工厂里默认的threadFactory，线程的命名规则是“pool-数字-thread-数字”。
  RejectedExecutionHandler handler // 超过最大线程池数量，拒绝
)
  //eg: 核心线程数 3， 最大线程数 6， 阻塞队列 10
  //1、2、3（核心线程数），4--13（阻塞队列），14、15、16 （最大线程数 6-3=3）的执行顺序，1、2、3、14、15、16、4--13
    
    
//创建方式
ExecutorService executorService = Executors.newCachedThreadPool();
ExecutorService executorService = Executors.newFixedThreadPool(3);
ExecutorService executorService = Executors.newSingleThreadExecutor();
```

**线程池工作机制**

- 如果当前运行的线程少于corePoolSize，则**创建新线程**来执行任务（注意，执行这一步骤需要获取全局锁）
- 如果运行的线程等于或多于corePoolSize，则将任务加入BlockingQueue。
- 如果无法将任务加入BlockingQueue（队列已满），则**创建新的线程**来处理任务。
- 如果创建新线程将使当前运行的线程超出maximumPoolSize，任务将被拒绝，并调用RejectedExecutionHandler.rejectedExecution()方法。



> 怎么让线程一直进行？
>
> 当前线程执行run方法中，可以使用BlockingQueue阻塞队列进行 take()阻塞



![img](https://github.com/zhouffan/senior-engineer/raw/master/image/%E7%BA%BF%E7%A8%8B%E6%B1%A0.png)

**任务特性**

- cpu密集型：cpu纯计算， 从内存中取出来计算。 线程数：不要超过cpu核心数+1。   **速度快**  

  ```java
  Runtime.getRuntime().availableProcessors()
  ```

- io密集型：网络通信/ 读写磁盘。    线程数：机器的cpu核心数*2        **速度慢**

- 混合型：



**摘自《Jeff Dean在Google全体工程大会的报告》**

| 操作                         | 响应时间    |
| ---------------------------- | ----------- |
| 打开一个站点                 | 几秒        |
| 数据库查询一条记录（有索引） | *十几毫秒*  |
| 1.6G的CPU执行一条指令        | **0.6纳秒** |
| 从机械磁盘顺序读取1M数据     | *2-10毫秒*  |
| 从SSD磁盘顺序读取1M数据      | *0.3毫秒*   |
| 从内存连续读取1M数据         | 250微秒     |
| CPU读取一次内存              | **100纳秒** |
| 1G网卡，网络传输2Kb数据      | 20微秒      |

1秒=1000毫秒      1毫秒=1000微秒         1微秒=1000纳秒





**AQS：AbstractQueuedSynchronizer  抽象队列同步器**   （state锁状态值）

- **作用：**用来构建锁或者其他同步组件的**基础框架**。使用了一个int成员变量表示同步状态，通过内置的FIFO队列来完成资源获取线程的排队工作。
- **使用：**继承AQS，管理同步状态值state

- CLH队列锁（Craig, Landin, and Hagersten (CLH) locks）：基于链表的自旋锁。

- 内部一个state状态值。包含模版方法模式。     同步工具类的内部类来继承AQS

> 设计模式：模版方法模式
>
> doSomthing(){
>
> ​	method1();
>
> ​	method2();
>
> ​	...
>
> }
>
> abstract public void method1();

**compareAndSetState**(int expect,int update)：使用CAS设置当前状态，该方法能够保证状态设置的原子性。 



非公平锁：不排队 拿锁。  抢占锁

ReentrantLock:  可重入锁，线程每进入一次会进入累加，释放一次进行累减。 基于AQS实现



cpu---->高速缓存---->内存

基于cpu读取速度快， 内存读取相对慢的情况，java内存模型引入了主内存和工作内存。

java内存模型： JMM

java线程从主内存中拷贝一个副本到各自的工作内存中进行相应操作。



## 3.4 synchronized的实现原理

**描述：**基于进入和退出Monitor对象来实现方法同步和代码块同步，都可以通过成对的MonitorEnter和MonitorExit指令来实现。常量池中多了ACC_SYNCHRONIZED标示符。

- JVM根据**该标示符**来实现方法的同步的：当方法被调用时，调用指令将会检查方法的 ACC_SYNCHRONIZED 访问标志是否被设置。
- 如果设置了，执行线程将**先获取monitor**，获取成功之后才能执行方法体，方法执行完后**再释放monitor**。
- 在方法执行期间，其他任何线程都**无法获得同一个**monitor对象。



> javap -v xxxx.class  : 反编译class文件
>
> monitorenter/monitorexit 指令实现 syn同步代码块



**锁的状态 比较**  （会随着竞争情况逐渐升级）

- 无锁状态
- 偏向锁 ： 加锁/解锁不需要额外消耗，和非同步方法执行接近。适用于一个线程访问同步块（减少不必要的CAS操作）
- 轻量级锁：由偏向锁**升级**来，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁。得不到锁会使用自旋消耗cpu，不会阻塞。 追求响应时间
- 重量级锁：线程阻塞，响应时间慢。不实用自旋，不消耗cpu。  追求吞吐量



synchronized是加锁在某一个具体的对象上， 如对象/ class/ 变量

DCL：双重检查锁定（单例模式）

```java
//最终懒汉式 单例模式 ===类加载机制，保证线程安全
public class SingleObject {
    private SingleObject(){}
    
    static class Instance{
        private static SingleObject singleObject = new SingleObject();
    }
    
    public static SingleObject getInstance(){
        return Instance.singleObject;
    }
}
```



> 指令重排序



- sleep/yield ：依然拥有锁    （sleep会抛出异常，所有会中断）

- wait ： 让出锁的执行权
- 线程顺序执行（T1/T2/T3）：T3方法中调用t2.join，再调用t1.join。 依次从t1-->t2-->t3



## 3.5 并发编程

工具包：java.util.concurrent.*

```java
│  AbstractExecutorService.java
│  ArrayBlockingQueue.java    //<-----
│  BlockingDeque.java
│  BlockingQueue.java
│  ConcurrentHashMap.java    //<-----
│  ConcurrentLinkedDeque.java
│  ConcurrentMap.java
│  DelayQueue.java
│  Executor.java
│  ExecutorService.java    //<-----
│  ForkJoinPool.java
│  ForkJoinWorkerThread.java
│  Future.java
│  LinkedBlockingDeque.java
│  LinkedBlockingQueue.java
│  RejectedExecutionException.java
│  RejectedExecutionHandler.java
│  ScheduledExecutorService.java
│  ...
│
├─atomic
│      AtomicBoolean.java     //<-----
│      AtomicInteger.java    //<----- 
│      AtomicIntegerArray.java    //<-----
│      AtomicLong.java
│      AtomicLongArray.java
│      AtomicReference.java
│      AtomicReferenceArray.java
│      AtomicStampedReference.java
│
└─locks
        AbstractOwnableSynchronizer.java
        AbstractQueuedLongSynchronizer.java
        AbstractQueuedSynchronizer.java    //<-----
        Lock.java
        ReadWriteLock.java
        ReentrantLock.java
```





# 4 JVM

**Java Virtual Machine**： 翻译工作，可以跨平台。狭义上指的就 **HotSpot**（JVM有很多版本，但是使用最多的是HotSpot）

- jvm识别.class后缀文件（翻译，系统不能识别.class文件）
- 解析文件中指令 （翻译）
- 调用操作系统上的函数（windows、linux、macos）
- 注意：**JVM只识别字节码**，与语言无关，解耦。（Java、Groovy 、Kotlin、Jruby等等语言都可以编译成字节码）



**释义**

- **流程：**java程序(javac编译)-----java字节码-----JVM(Java类加载器->运行时数据区->执行引擎)-------操作系统函数（linux/windows/macos）
- **JRE**（Java Runtime Environment）：运行时环境，**包含JVM**，提供了其他类库（比如操作文件，连接网络，使用I/O等）。
- **JDK**：供了一些小工具，比如 javac（编译代码）、java、jar （打包代码）、javap（反编译<反汇编>）等



**运行时数据区**：自动内存管理机制

- 线程私有

  - **虚拟机栈**：存储当前线程运行方法所需的数据，**指令、返回地址**。虚拟机栈是基于线程，生命周期**跟随线程**。栈里的每条数据，就是栈帧。

    栈帧/大小限制(- Xss)

  - **本地方法栈**： 虚拟机栈用于管理 Java 函数的调用，而本地方法栈则用于管理本地方法的调用（由 C 语言实现）。HotSpot直接把**本地方法栈**和**虚拟机栈**合二为一

  - **程序计数器** ：较小的内存空间，JVM中唯一不会OOM(OutOfMemory)的内存区域。主要用来记录**各个线程**执行的**字节码的地址**，例如，分支、循环、跳转、异常、线程恢复等都依赖于计数器。（作用：时间片轮转应用。记录执行到哪一行标志）

- 线程共享

  - **方法区/永久代** ：存放已被虚拟机加载的类相关信息，包括类信息、**静态变量**、常量、运行时常量池、字符串常量池

  - **堆**   ：JVM 上**最大**的内存区域，申请的几乎所有对象，都是在这里存储。**垃圾回收，操作的对象就是堆**。需要不定期回收， GC（Garbage Collection

    

**java堆的大小参数设置**  （例如- Xmx256m）

- -Xms：堆的最小值；
- -Xmx：堆的最大值；
- -Xmn：新生代的大小；
- -XX:NewSize；新生代最小值；
- -XX:MaxNewSize：新生代最大值；



**Java平台，标准版工具参考**：https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html



**栈帧**：都包含四个区域

- **局部变量表**：存放局部变量的，Java的八大基础数据类型、Object对象（存放其**引用地址**）
- **操作数栈**：方法执行的操作数，操作的的元素可以是任意的java数据类型
- 动态连接：Java语言特性多态
- **返回地址**：正常返回（调用程序计数器中的地址作为返回）、异常的话（通过异常处理器表<非栈帧中的>来确定）



>class类加载：加载、验证、准备、解析、初始化



java方法开始执行：入栈

java方法执行完毕：出栈

**一个方法就是一个栈帧**

>  javap命令行： **javap -c** （堆代码进行反汇编）
>
> jps：查看进程  （JVMStack）
>
> HSDB：HotSpot Debugger



**内存划分**：

- 指针碰撞： 内存规律
- 空闲列表：空位不规则
- 并发 
  - CAS失败重试
  - TLAB 本地线程分配缓存，使用线程本地分配块



Object

- finalize() : 优先级低，需要等待； 只执行一次。    ===>try  finally



可达性分析算法



**对象引用**

- 强引用：
- 软引用（ SoftReference）：oom时，才会回收。
- 弱引用（WeakReference）：gc时，就会回收。   eg：threadLocal
- 虚引用：



栈跟随线程存在：线程结束，栈内存也结束（如方法栈，局部变量）



阈值

阀值













