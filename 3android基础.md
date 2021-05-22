

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



 





























