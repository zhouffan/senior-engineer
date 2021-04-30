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
> **ThreadLocal:** 每个线程都有遍历的副本，线程隔离。   拥有一个线程独有的   TheadLocalMap （内含Entry[]）
>
> ​						threadLocal使用后， 不remove，会发生内存泄漏 。
>
> ​						没有使用好，也会线程不安全。







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



原子操作： 不可分，要么全部做，要么一个都不做

**CAS （compare and swap）** （无锁算法）

-  **比较再交换**，java.util.concurrent.*,其下面的类直接或者间接使用CAS算法实现，区别synchronouse同步锁的一种乐观锁。 

- CPU指令级的操作，只有一步原子操作，非常快。避免了请求操作系统来裁定锁的问题，直接在CPU执行。

- 原理： 利用现代处理器都支持的CAS的指令，  **循环**这个指令，直到成功为止。  （自旋：死循环。但性能高）。

- get变量值（旧值）--->计算后得到新值---> compare内存中变量值和旧值---->如果相等-----旧值swap为新值

  ​																														   ---->如果不想等，从头再来一次

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





**解决多线程并发问题：**

- 加synchronized 。锁机制
- threadLocal。副本，4个方法，get/set/remove/initialValue
- concurrent下的原子操作类。 concuuurentHashMap，AtomicBoolean...



















