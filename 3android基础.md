

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























