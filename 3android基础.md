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





























