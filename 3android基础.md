

# 1.dex文件加密

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



# 2.Android虚拟机与类加载机制

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





- **BootClassLoader**：用于加载Android Framework层class文件。
- **PathClassLoader**：用于Android应用程序类加载器。可以加载指定的dex，以及jar、zip、apk中的classes.dex 。
- **DexClassLoader** ：用于加载指定的dex，以及jar、zip、apk中的classes.dex。



**双亲委托机制**：类加载器在加载类时，先将加载任务委托给父类加载器，依次递归，如果父类（系统中的类）完成加载任务，则成功返回；否则自己（自己项目中所写的类）去加载。

- 避免重复加载：父类加载一次，子classLoader不会再次加载。
- 安全性考虑：防止核心api库被篡改。

```java
public class PathClassLoader extends BaseDexClassLoader {
                public PathClassLoader(String dexPath, ClassLoader parent) {
                    super(dexPath, null, null, parent);
                }

                public PathClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
                    super(dexPath, null, librarySearchPath, parent);
                }
}

//使用
PathClassLoader pathClassLoader = new PathClassLoader("/sdcard/xx.dex", getClassLoader());
```





android 热修复应用

- application中初始化加载 补丁包（xxx.dex）。加载了新修复类，就不会加载原apk中的bug 类。





**class打包dex**

- dx --dex --output=output.dex input.jar
- dx --dex --output=output.dex /com/xx/xx/A.class B.class





**github查看代码插件**：octotree





# 3.Kotlin

- Test.java -------Test.class-------JVM

- Test.kt ----------Test.class-------JVM   (编译再编译，编译较慢)

静态语言： 编译期已确定。（js动态解释语言）



>  函数：一级公民。类外面也可以写



for循环自定义标签， 嵌套循环停止  ：break@xxxx  (@xxxx for(...))



- kotlin全部变量都**没有**默认值；

- java中成员有默认值，方法内部没有默认值



abstract  有open的特征



黄色---object----单例

- 嵌套类：
- 内部类：inner   可以拿到外层的变量值



@JvmStatic ：真静态方法



> tools--->kotlin--->show kotlin byteCode   : 查看kotlin对应的Java代码

```kotlin
//形参里面 java类
fun showClass(clazz: Class<JavaStudent>){}
//全部都是kotlin
fun showClass2(clazz:KClass<KtStudent>){}

//调用
showClass(JavaStudent::class.java) //kotlin 和java交互 （JavaStudent是java类）
showClass2(KtStudent::class) //没有.java
```



方法隔离（java不能调用kotlin）： ``

```kotlin
fun `test`(){
  printf()
}
```



高阶函数：省略接口

```kotlin
fun show(num: Int, back:(Boolean)->Unit){
  num++;
  if(num == 2){
    back(true)
  }else{
    back(false)
  }
}
//调用
show(1){
  println(it)
}

//show中直接调用方法  ::  (变成函数类型的对象，进行参数传递)
fun myprintln(b: Boolean){
  println(b)
}
//调用
show(1, ::myprintln)
```



let/also/run...

```kotlin
//两个R  释义
fun <R> sum(n1: Int, n2:Int, mm:(Int, Int)->R):R{
  return mm(n1, n2)   //下面mm的返回值 true，  这里又返回给 sum函数
}
//调用
var s = sum(10, 20){i1,i2->
   println("$i1  $i2")
   "xxx"
   true    //最后一个返回的值，就是mm的返回值
}
println(s) //打印的是mm的返回值，  层层返回到最外层。
```



```kotlin
//泛型 扩展函数
fun <T> T.test(){
  //...
}
fun main(){
  val name = "a"
  val age = 10
  //调用
  name.test()
  age.test()
}


//'T.(String)->T'    给T增加匿名函数，返回是this    ===>对象本身，可以直接调用方法:xxx()
fun <T, R> T.mylet2(mm: T.(String)->R):R{
  return mm("")
}
//'(String)->T'      匿名函数，返回是it    ===>对象it，调用方法:it.xxx()
fun <T, R> T.mylet(mm: (String)->R):R{
  return mm("")
}
```



**out**/in  (**? extend**/? supper : 1.上限下限；2. **能否获取/能否修改**)

>  out/in：与上下限无关。

- out =====>  不能修改，只能获取 （? extend）

- in    =====>...



**示例一：**基础

```kotlin
fun main() {
    val stu2 =  Student2(4545, "Derry", 'M')
    val (v1, v2, v3) = stu2.copy()
    //自定义解构赋值
    val stu =  Student(4545, "Derry", 'M')
    val(n1, n2, n3 ,n4) = stu

    stu.show("")

    `123`()
    showClass(KtStudent::class)
    showClass2(Thread::class.java)

    val instance = SingleInstance.getInstance()
    specialStr()
    lenMethod("222","333","444")
}

//扩展函数
fun Student.show(string: String){
    println(string)
}

//自定义解构赋值
data class Student2(var id: Int, var name: String ,var sex: Char)
class Student(var id: Int, var name: String ,var sex: Char){
    // component 不能写错
    operator fun component1(): Int = id
    operator fun component2(): String = name
    operator fun component3(): Char = sex
    operator fun component4(): String = "KT Study OK"

    //运算符重载   student + student
    operator fun plus(num: Int): Int{
        return id + num
    }

}

//特殊名称方法
fun `123`(){
    print("xx")
}

//kotlin类 KtStudent
fun showClass(clazz: KClass<KtStudent>){}
//java类  thread
fun showClass2(clazz: Class<Thread>){}
class KtStudent{}

/**
 * 可变参数
 * @param names Array<out String>
 */
fun lenMethod(vararg names: String){
    names.forEach {
        println(it)
    }
}

fun specialStr(){
    // --- 自己不用关系 \n 换行 ，不用自己调整
    val infoMesage = """
        AAAAAAAAAAA
        BBBBBBBBBBB 
    """  // 前置空格
    println(infoMesage)

    val infoMesage2 = """
        AAAAAAAAAAA
        BBBBBBBBBBB 
    """.trimIndent()  // 没空格
    println(infoMesage2)

    val infoMesage3 = """
        ?AAAAAAAAAAA
        ?BBBBBBBBBBB 
    """.trimMargin("?")  // 没空格 控制|
    println(infoMesage3)

    // 需求：显示 $99999.99
    val price = """
        ${'$'}99999.99
    """.trimIndent()
    println(price)
}

fun forMethod(){
    for(i in 1..10){
        println(i)
    }
    for (i in 9 downTo 1){
        println(i)
    }
    for (i in 1 until 30 step 2 ){
        println(i)
    }
}

fun forTag(){
    //自定义标签
    aaa@for ( i in 1..10){
        for (j in 1..10){
            if(j == 3){
//                break//停止最近循环
                break@aaa
            }
        }
    }
}

fun otherMethod(){
    // ---  比较对象地址
    val test1:Int? =  10000
    val test2:Int? =  10000
    println(test1 === test2) // false

    val num = if (test1 == 1) {
        false
    } else {
        true
    }

    when (test1) {
        1000 -> println("sss")
        in 1..20 -> println("222")
    }
}


class Person(id: Int){//主构造
    //次构造
    constructor():this(-1){}
    //次构造
    constructor(id: Int, name:String):this(id){}
}

class Sub{
    val num: Int = 1
    //嵌套类
    class Sub1{
//        val s = num
    }
    //内部类
    inner class Sub2{
        val s = num
    }

}

class SingleInstance{
    //object 单例
    object Holder{
        val instance = SingleInstance()
    }
    companion object{
        //对外提供方法
        fun getInstance() = Holder.instance
    }
}
```



**示例二：** 函数高级

```kotlin
fun main() {
    val name: String? = null
    testMethod().myRunOk {
        //回调方法是泛型的
        true
    }
    //方法体返回的是 this:String， 因为 '万能泛型.'
    name?.myRunOk {
            it-> print(it)
        print("$length")
        false
    }
    //方法体里面没有对象
    name.myRunOk2 {
        it-> print(it)
        true
    }

    //`::` 拿到函数类型的对象
    val m1 = ::testMethod
    test01(m1)

    //无限链式调用
    name.myAlso {

    }.myAlso {

    }.myAlso {

    }

    ktrun() {
        println("执行了一次 x")
    }
}


fun testMethod(){
    print("xxx")
}

fun test01(mm: ()->Unit){
    mm()
}

// 自定义线程封装
fun ktrun(
    start: Boolean = true,
    name: String ? = null,
    myRunAction: () -> Unit) : Thread {
    val thread = object : Thread() {
        override fun run() {
            super.run()
            myRunAction()
        }
    }
    name ?: "DerryThread"
    if (start)
        thread.start()
    return thread
}

/**
 * 高阶函数：
 * @receiver 万能泛型   随意定义
 * @param mm [@kotlin.ExtensionFunctionType] Function1<万能泛型, Boolean>
 *     ‘万能泛型.()->Boolean’  ：回调方法是泛型的
 */
fun <万能泛型> 万能泛型.myRunOk(mm: 万能泛型.(Int)->Boolean){
    mm(10)
}
fun <万能泛型> 万能泛型.myRunOk2(mm: (Int)->Boolean){
    mm(10)
}
//R：返回泛型
fun <T, R> T.myRun(mm: ()->R):R{
    return mm()
}
fun <T, R> T.myLet(mm: T.(T) -> R): R {
    // T == this   () -> R
    // mm(this) == this     vs    T.(T)  -> R
    return mm(this)
}

//返回this， 可以无限链式调用
fun <T> T.myAlso(mm: (T) -> Unit) : T{
    // T == this
    // it == T == this == name
    mm(this)
    return this
}
```



**示例三**：(out 只能取，不能修改。in 只能修改（存）， 不能获取)

```kotlin
fun main() {
    println("111")
    //out 只能取，不能修改   == ? extends FuClass
    val list: MutableList<out FuClass> = ArrayList<ZiClass>()
    //in 只能修改（存）， 不能获取   === ？ super ZiClass
    val list2:MutableList<in ZiClass> = ArrayList<FuClass>()
}

open class FuClass{}
class ZiClass: FuClass(){}

/**
 * 控制读写模式
 * @param out T 只能获取，不能修改
 */
class TestClass <out T>{
    fun getData(): T? = null
//    fun setData(data: T){}   //报错
}

class TestClass2 <in T>{
//    fun getData(): T? = null //报错
    fun setData(data: T){}
}



===============================
public class Simple03ExtendSupper {

    public static void main(String[] args) {
        System.out.println("xxx");
        FClass fClass = new FClass();
        ZClass zClass = new ZClass();

        //1
        ArrayList<FClass> list = new ArrayList<FClass>();
        list.add(fClass);
        list.add(zClass);
        list.get(0);
        //2  不能修改， 只能获取
        ArrayList<? extends FClass> list2 = new ArrayList<ZClass>();
//        list2.add(fClass);//报错   不知道FClass的 子是谁
//        list2.add(zClass);//报错   不知道FClass的 子是谁
        FClass c = list2.get(0);
        //3  不能获取， 只能修改
        ArrayList<? super ZClass> list3 = new ArrayList<FClass>();
        list3.add(zClass);
//        FClass c2 = list3.get(0);//报错   不知道 ZClass 的父是谁
//        ZClass c3 = list3.get(0);//报错   不知道 ZClass 的父是谁， 只能强转

    }
    static class FClass{}
    static class ZClass extends FClass{}
}
```











# 4.自定义view

- 布局： onLayout/ onMeasure   (layout:viewgroup)
- 显示：onDraw               (view: canvas, paint, matrix, clip, rect, animation, path, line)
- 交互：onTouchEvent   (组合的viewGroup)



自定义控件分类

- 自定义view： 重写onMeasure() 和 onDraw()
- 自定义viewgroup：重写onMeasure() 和 onLayout()

```java
public class CustomView extends View {
    /**
     * 代码直接调用
     * @param context
     */
    public CustomView(Context context) {
        super(context);
    }
    /**
     * xml调用
     * @param context
     * @param attrs  自定义属性
     *               通过<declare-styleable> 为自定义view添加属性
     */
    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
      	// 解析布局属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
        boolean aBoolean = typedArray.getBoolean(R.styleable.CustomView_layout_simple_attr, false);
        int gravity = typedArray.getInteger(R.styleable.CustomView_android_layout_gravity, 0);
       	typedArray.recycle();
    }
    /**
     * 不会自动调用，一般第二个函数调用
     * @param context
     * @param attrs
     * @param defStyleAttr  view style
     */
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * api21之后才使用，不会自动调用，一般第二个函数调用
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}

<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CustomView">
        <!-- 自定义属性 -->
        <attr name="layout_simple_attr" format="boolean"/>
        <!-- 使用系统预置的属性 -->
        <attr name="android:layout_gravity"/>
    </declare-styleable>
</resources>
  


CustomView view = null;
int left = view.getLeft();  //当前view的左上角与父view的左侧距离
int right = view.getRight();//当前view的右上角与父view的左侧距离

MotionEvent event = null;
float x = event.getX();      //触摸点相对于 该view 的坐标系 坐标
float rawX = event.getRawX();//触摸点相对于 屏幕默认 的坐标系 坐标
```



**view视图结构**

- **PhoneWindow**：android系统中最基本的窗口系统，继承windows类，管理界面显示和事件响应。activity和view系统交互的接口
- **DecorView**（framlayout）：PhoneWindow的起始节点view，设置窗口属性。
- **ViewRoot**：activity启动时创建，负责管理/布局/渲染UI等。

**view树的绘制流程**：通过ViewRoot去负责绘制，是view树的管理者，负责将DecorView和PhoneWindow“组合”起来；每个DecorView都有一个ViewRoot与之关联，由WindowManager进行管理。



> 无论measure/layout/draw，都是从view的根节点开始测量（DecorView），层层递归，最终计算整个view树中的各个view，最终确定相关属性。



**绘制流程**

- 构造函数（view初始化）
- onMeasure() （测量view大小）
- onSizeChanged() （确定view大小）
- onLayout()  （确定子view布局，包含子view时用）
- onDraw()  （实际绘制内容）
- 视图状态改变 (用户操作或者自身变化引起)   =====>invalidate()=====>onDraw()
- 结束  





- **LayoutParams**：布局参数，父容器控制子view的布局，都是对应**ViewGroup子类（LinearLayout）**的内部类。如LinearLayout.LayoutParams。

- **MarginLayoutParams**：增加了对上下左右的外间距支持。大部分LayoutParams的实现类都继承MarginLayoutParams，因为基本所有父容器都支持子view**设置外间距**



**MeasureSpec**：测量规格。（SpecMode/SpecSize）

- unspecified：不对view大小限制，系统使用
- exactly：确切大小，100dp
- at_most：大小不可超过某个值，  如：matchParent，不能超过父



> **子view**的MeasureSpec值：根据**子view的布局参数**（LayoutParams）和**父容器的MeasureSpec**值计算得来，封装在getChildMeasureSpec()
>
> ```
> 对于不同的父容器和view本身不同的LayoutParams，view就可以有多种MeasureSpec。 
> 1. 当view采用固定宽高的时候，不管父容器的MeasureSpec是什么，view的MeasureSpec都是精确模式并且其大小遵循Layoutparams中的大小； 
> 2. 当view的宽高是match_parent时，
>  2.1 父容器的模式是精准模式，那么view也是精准模式并且其大小是父容器的剩余空间，
>  2.2 父容器是最大模式，那么view也是最大模式并且其大小不会超过父容器的剩余空间； 
> 3. 当view的宽高是wrap_content时，不管父容器的模式是精准还是最大化，view的模式总是最大化并且大小不能超过父容器的剩余空间。 
> 4. Unspecified模式，这个模式主要用于系统内部多次measure的情况下，一般来说，我们不需要关注此模式(这里注意自定义View放到ScrollView的情况需要处理。
> ```



- view.getWidth() ：执行 onlayout 布局后获取
- view.getMeasuredWidth()： 执行onMeasure 测量后获取





# 5. xml解析-换肤



 



# 6.京东淘宝首页二级联动

> tools--->Layout  Inspector查看view的层级



嵌套滑动： 包含父+ 子

NestedScrollView：可以充当父嵌套和子嵌套 



**单点手指**：

- ACTION_DOWN ：手指 **初次接触到屏幕** 时触发。 

- ACTION_MOVE ：手指 **在屏幕上滑动** 时触发，会会多次触发。 

- ACTION_UP ：手指 **离开屏幕** 时触发。 

- ACTION_CANCEL ：事件 **被上层拦截** 时触发。

**多点手指**：

- ACTION_DOWN ：**第一个** 手指 **初次接触到屏幕** 时触发。 

- ACTION_MOVE 手指 **在屏幕上滑动** 时触发，====>会多次触发。 (多个手指)

- ACTION_UP **最后一个** 手指 **离开屏幕** 时触发。 

- **ACTION_POINTER_DOWN** 有非主要的手指按下(**即按下之前已经有手指在屏幕上**)。 

- **ACTION_POINTER_UP** 有非主要的手指抬起(**即抬起之后仍然有手指在屏幕上**)。 



**事件处理函数**

- dispatchTouchEvent：事件分发   （activity / viewgroup / view）
- onInterceptTouchEvent：事件拦截 （**viewgroup**）
- onTouchEvent：事件消费 （activity / viewgroup / view）

>  MotionEvent-->父向子分发事件dispatchTouchEvent---->若子允许父拦截--->且父进行onInterceptTouchEvent---->父进行事件消费onTouchEvent



**事件分发大流程**

//TODO 图片

```java
//ViewGroup 是根据 onInterceptTouchEvent 的返回值来 确定 是调用子View的 dispatchTouchEvent 还是 自身的 onTouchEvent， 并没有将调用交 给onInterceptTouchEvent.
public dispatchTouchEvent(MotionEvent ev) { 
  // 默认状态为没有消费过 
	boolean result = false; 
  // 如果没有拦截交给子View 
  if (!onInterceptTouchEvent(ev)) { 
    result = child.dispatchTouchEvent(ev); 
  }
  // 如果事件没有被消费,询问自身onTouchEvent
  if (!result) {  
    result = onTouchEvent(ev); 
  }
  return result; 
}



public boolean dispatchTouchEvent(MotionEvent ev) { 
  // 默认状态为没有消费过 
  boolean result = false; 
  //决定是否拦截 
  final boolean intercepted = false; 
  if (!requestDisallowInterceptTouchEvent()) {   <===============
    intercepted = onInterceptTouchEvent(ev); 
  }
  //找出最适合接收的孩子 
  if (!intercepted && (DOWN || POINTER_DOWN || HOVER_MOVE)) { 
    // 如果没有拦截交给子View 
    for (int i = childrenCount - 1; i >= 0; i--) { 
      mFirstTouchTarget = child.dispatchTouchEvent(ev); 
    } 
  }
  //分发事件 
  if (mFirstTouchTarget == null) { 
    // 如果事件没有被消费,询问自身onTouchEvent 
    result = onTouchEvent(ev); 
  } else { 
    for(TouchTarget touchTarget : mFirstTouchTarget) { 
      result = touchTarget.child.dispatchTouchEvent(ev); 
    } 
  }
  return result;
}
```



**事件监听器调用先后顺序**

- onTouchListener：ACTION_DOWN 和 ACTION_UP。最前面
- onTouchEvent：提供了一种默认的处理方式。 **view自身**
- onLongClickListener：不需要ACTION_UP
- onClickListener：需要两个事件(ACTION_DOWN 和 ACTION_UP )才能触发，如果先分配给onClick判断，等它判断完，用户手指已经离开屏幕。



**TouchTarget**：手势设置的，跨越事件保留

1、mFirstTouchTarget是TouchTarget，是一个链表； 

2、mFirstTouchTarget记录的是该view的第一个接收该手势的子view； 

3、mFirstTouchTarget的next的TouchTarget记录的是这个手势的其他手指或者鼠标所在的子view； 

4、mFirstTouchTarget是一个view group是否有孩子处理该事件的一个标志，为null表明没有孩子处理它， 否则表明action_down已经分发给他的孩子了； 

5、mFirstTouchTarget是View group的成员变量，标志着每个view group都有一个这样的变量，如果一个手势被一个view处理，那么他的父亲和祖父们的mFirstTouchTarget都不会为null；



**外部拦截**

- 当ViewPager接收到DOWN事件，ViewPager默认不拦截DOWN事件，DOWN事件交由ListView处理，由于ListView可以滚动，即可以消费事件，则ViewPager的mFirstTouchTarget会被赋值，即找到处理事件的子View。然后ViewPager接收到MOVE事件， 

- 若此事件是ViewPager不需要，则同样会将事件交由ListView去处理，然后ListView处理事件； 

- 若此事件ViewGroup需要，因为DOWN事件被ListView处理，mFirstTouchEventTarget会被赋值，也就会调用onInterceptedTouchEvent,此时由于ViewPager对此事件感兴趣 ，则onInterceptedTouchEvent方法会返回true，表示ViewPager会拦截事件，此时当前的MOVE事件会消失，变为CANCEL事件，往下传递或者自己处理，同时mFirstTouchTarget被重置为null。 

- 当MOVE事件再次来到时，由于mFristTouchTarget为null，所以接下来的事件都交给了ViewPager。

**内部拦截**

- requestDisallowInterceptTouchEvent(false)





















