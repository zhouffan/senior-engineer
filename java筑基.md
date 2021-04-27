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





# 2 注解

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





