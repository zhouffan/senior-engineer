[《后端架构师技术图谱》](https://github.com/xingshaocheng/architect-awesome)

# 1 configuration

## 1.0 linux

命令大全：https://man.linuxde.net/

linux目录结构：https://www.cnblogs.com/zhuchenglin/p/8686924.html

```java
//切换登录用户    /etc/group
su - 用户名
useradd xxx
userdel xxx
passwd xxx
  
groupadd ggg
groupmod ggg
  
ls /bin
//重启网络服务
service network restart
  //关闭防火墙     /etc/sysconfig/network-scripts/ifcfg-xxx
  systemctl stop firewalld
  systemctl disable firewalld //禁止开机启动
  
  //查看内容
  cat //最后一屏
  more //百分比， 回车：下一行；空格：下一页， q：退出
  less // pgUp /pgDn
  head -10 / tail -10 //文件前10行，  后10行   ctrl+c:结束
  tail -f //tail：后，  对某个文件动态监控， 如tomcat日志文件  tail -f catalina-222.log
  //tail -f logs/catalina.out
  
  man  
  
  find  /usr/local/ -name *.txt  //查找
  mv  //移动/改名
  cp -r  // -r 复制目录  recursion 循环
  touch xxx.txt
  
  chmod u=rwx,g=rw,o=r text.txt
  chmod 764 text.txt
  
  grep //搜索   grep a   xx.txt
  ps -ef/ ps aux //ps aux|grep redis        
  kill -9 pid //-9强制终止
  
  ping
  //netstat -tulnp|grep LISTEN    an: 所有连接端口；tulnp：正在TCP/UDP监听的端口；rn：网关  ===>可以各种组合
  netstat 
  ln //创建链接，  -s：软链接， 没有则是硬链接（copy）
  
  tar -zxvf jdkxxxx.tar.gz // 解压至/urs/local 
  vim /etc/profile 
  "
JAVA_HOME=/usr/local/jdk1.8.0
CLASSPATH=$JAVA_HOME/lib/
PATH=$PATH:$JAVA_HOME/bin  //:增量
export PATH JAVA_HOME CLASSPATH
  "
  source /etc/profile  
  java -version
  
  
 

scp  /etc/hosts root@mq2:/etc/   //复制当前hosts 到另一台虚拟机上去      s：sudo   cp：copy
```



**vim 编辑命令**

- 进入文件
- **命令模式**
- **按i编辑模式**
- 编辑文件
- esc进入低行模式
- :wq/ q! 退出

```java
:set nu   //设置行号

//移动光标
hjkl //上下左右
ctrl+b //后移动一页
ctrl+f //前移动一页
ctrl+u //后移动半页
ctrl+d //前移动半页
shift+g == G //移到文章最后
shift+4 == $ //移动到光标所在行的行尾
shift+6 == ^ //移动到光标所在行的行首
w //光标到下个字的开头
e //光标到下个字的结尾
b //光标回到上个字的开头
#1 //光标移到该行的第‘#’个位置   51  561
gg //进入文本的开始


//删除文字
dd  //删除光标行
#dd //6dd: 删除6行，光标所在行开始
x   //删除光标所在位置的一个字符
#x  //6x：删除光标所在位置后面6个字符， 包括自己
shift+x == X //删除光标位置的前一个字符
shift+#x == #X //20X：删除光标前20个字符

    
//复制
yw //光标所在位置到字尾，复制到缓冲区
#yw // 6yw:复制6个字符到缓冲区
yy  //复制光标所在行
#yy //光标行开始，复制多行
p   //粘贴到光标所在位置      与y有关的命令完成复制、粘贴
    
    
//替换
r //替换光标所在处的字符
R //替换光标所到之处的字符 ， Esc停止

//撤销上次操作
u //回到上一个操作，  多次u，多次恢复
    
//更改
cw //更改光标所在处的字到字尾处    c#w：更改3个字

//跳至指定行
ctrl+g  //列出光标所在行的行号
#G      //15G ：光标移到文章的15行 行首
```







1.按照文件名查找

　　　　(1)find / -name httpd.conf　　#在根目录下查找文件httpd.conf，表示在整个硬盘查找
　　　　(2)find /etc -name httpd.conf　　#在/etc目录下文件httpd.conf
　　　　(3)find /etc -name '*srm*'　　#使用通配符*(0或者任意多个)。表示在/etc目录下查找文件名中含有字符串‘srm’的文件
　　　　(4)find . -name 'srm*' 　　#表示当前目录下查找文件名开头是字符串‘srm’的文件

2.按照文件特征查找 　　　　

　　　　(1)find / -amin -10 　　# 查找在系统中最后10分钟访问的文件(access time)
　　　　(2)find / -atime -2　　 # 查找在系统中最后48小时访问的文件
　　　　(3)find / -empty 　　# 查找在系统中为空的文件或者文件夹
　　　　(4)find / -group cat 　　# 查找在系统中属于 group为cat的文件
　　　　(5)find / -mmin -5 　　# 查找在系统中最后5分钟里修改过的文件(modify time)
　　　　(6)find / -mtime -1 　　#查找在系统中最后24小时里修改过的文件
　　　　(7)find / -user fred 　　#查找在系统中属于fred这个用户的文件
　　　　(8)find / -size +10000c　　#查找出大于10000000字节的文件(c:字节，w:双字，k:KB，M:MB，G:GB)
　　　　(9)find / -size -1000k 　　#查找出小于1000KB的文件

二、grep命令

　　　  基本格式：find  expression

 　　　 1.主要参数

　　　　[options]主要参数：
　　　　－c：只输出匹配行的计数。
　　　　－i：不区分大小写
　　　　－h：查询多文件时不显示文件名。
　　　　－l：查询多文件时只输出包含匹配字符的文件名。
　　　　－n：显示匹配行及行号。
　　　　－s：不显示不存在或无匹配文本的错误信息。
　　　　－v：显示不包含匹配文本的所有行。



```
find / -name '*.txt' |grep zhou
```





## 1.1 docker

​                                        仓库（push/pull）

Sockerfile ==(build)==>镜像 <==(save/load)==>tar文件

​                                         容器（commit/run）

(**镜像**可以理解为一个Java类，而**容器**可以理解为Java类的实例。)

https://blog.csdn.net/sunnyzyq/article/details/101222410





**安装和常用CLI**：
添加阿里云镜像：sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
**安装命令**：sudo yum install -y  docker-ce docker-ce-cli containerd.io
**启动命令**：sudo systemctl start docker
**添加当前用户到docker用户组**：sudo usermod -aG docker $USER （需注销），newgrp docker （立即生效）
Helloworld：docker run hello-world  （本地没有镜像的话会自动从远端仓库pull）
**pull nginx 镜像**：docker pull nginx（等效于nginx:latest）
**运行**：docker run -【d】（后台运行不阻塞shell） 【-p 80:80】（指定容器端口映射，内部：外部） nginx

```
$ docker run --name nginx-test -p 8080:80 -d nginx
```

**查看正在运行**：docker ps
**删除容器**：docker rm -f container id(不用打全，前缀区分)
**进入bas**h：docker exec -it container id(不用打全，前缀区分) bash
**commit镜像**：docker commit container id(不用打全，前缀区分)  name
**查看镜像列表**：docker images （刚才commit的镜像）
**使用运行刚才commit的镜像**：docker run -d name
**使用Dockerfile构建镜像**：docker build -t name 存放Dockerfile的文件夹
**删除镜像**：docker rmi name
**保存为tar**：docker save name  tar name
**从tar加载**：docker load  tar name

```java
//使用 tomcat 镜像
//-d 后台运行
//-p 指定访问主机的8081端口映射到8080端口。  内部端口号:外部端口号
//-v 指定我们容器的/usr/local/tomcat/webapps/目录为/root/tomcat/主机目录，后续我们要对tomcat进行操作直接在主机这个目录操作即可。
//指定映像版本：name:ver
docker run -d -p 8081:8080 -v /root/tomcat/:/usr/local/tomcat/webapps/ tomcat

//列出所有容器 ID 
docker ps -aq
//停止所有容器 
docker stop $(docker ps -aq)
//停止单个容器 
docker stop 要停止的容器名
//删除所有容器
docker rm $(docker ps -aq)
//删除单个容器
docker rm 要删除的容器名
//删除所有的镜像
docker rmi $(docker images -q)
  
//进入目录
docker exec -it xxxxxx容器 /bin/bash
  
  
//vim 新建Dockerfile文件
FROM java:8
MAINTAINER bingo
ADD demo-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]
  
//创建镜像   注意最后的 .  表示 Dockerfile 文件在当前目录下
docker build -t my/demo .
//这个表示docker容器在停止或服务器开机之后会自动重新启动 --restart=always
//已经运行的docker容器怎么设置自动重启？ docker update –-restart=always demo 
docker run -d --name demo -p 8080:8080 my/demo
//查看启动日志
docker logs --tail  300 -f  demo   
//打包镜像
docker save -o xxxx.tar my/demo
//解包
docker load -i xxxx.tar
```





## 1.2 kubernetes









## 1.3 nginx

http://www.nginx.cn/doc/index.html

定义： http服务器/反向代理服务器/电子邮件代理服务器，支持5万并发。

翻墙： 正向代理

nginx： 反向代理，不知道实际地址



```
nginx -V/-v 
nginx -T/-t
nginx -q
nginx -s reopen
nginx -s stop
nginx -s quit   //推荐
nginx -s reload 
nginx -g

./nginx/sbin/nginx  //启动
sz nginx.conf  //拉取文件
```



```
yum install -y pcre-devel openssl-devel gcc curl
cd /usr/local/
wget https://openresty.org/download/openresty-1.17.8.2.tar.gz
cd /usr/local/
tar -zxvf openresty-1.17.8.2.tar.gz
cd /usr/local/
mv openresty-1.17.8.2 openresty
cd /usr/local/openresty/
./configure --with-luajit \
--without-http_redis2_module \
--with-http_iconv_module
cd /usr/local/openresty/
make&& make install
```

chmod +x openresty.sh

config配置文件   (复制**多份tomcat**，不同主页，进行访问测试)

```java
worker_processes 8; //cpu核心数*2
enents{
	#工作模型，日志中看到
	use epoll;
}
http{
	//限制访问速率
	limit_req_zone $binary_remote_addr zone=tuling:10m rate=2r/s;

	server{
		listen  80;
		
		//服务组
		upstream tuling{
			//轮询，权重，iphash:每个访客固定一个后端服务，
			server 127.0.0.1:8080 weight=1;   
			server localhost:8081 weight=2; 
		}
		upstream tuling2{
			ip_hash；    //iphash:每个访客固定一个后端服务
			least_conn; //请求分配到 最少连接的服务上     防止有些一直保持连接
			fair;
			server 127.0.0.1:8082;
			server 127.0.0.1:8083 backup; //backup 实现热备
		}
		
		location / {  //   ‘/’代表所有
			proxy_pass http://tuling;  //请求到 tuling
			...
		}
		location /test.html {  //请求定位： 匹配规则
			proxy_pass http://tuling2;  //请求到 tuling2
			
			
			//调用速率控制
			limit_req zone=tuling burst=5 nodelay;  //缓存5个请求   nodelay 不延迟
			
			...
		}
	}
}



ab -n100 -c10 http://127.0.0.1/    //模拟请求  100次请求， 
```

**限流：**单位时间内，限制用户访问服务器次数。 （超出服务器承载的流量，qps）

**限流算法：**

- **漏桶** （直接丢弃）    nginx使用方式

  请求从上方倒入水桶，从水桶下发流出（处理）；固定速率流出；水桶满时，谁溢出（丢弃）；

- **令牌桶** （含有一个队列）

  令牌固定速度产生，缓存到令牌桶中； 令牌桶满时，多余令牌丢弃；请求消耗令牌；令牌不够时，请求缓存

- **计数器**

- **滑动窗口**



**nginx实现动静分离**

- 静态页面由nginx处理
- 动态页面交给服务器或者apache处理

```
	
	server{
		location ~*\.(jpg|gif)${
			proxy_pass http://tuling   //指定到服务器来提供
			#root html;   //nginx内部提供
		}
	}
	
```



**nginx热备部署**：双主模式

- 利用keepalived解决单点风险，一旦nginx宕机，快速切换到备份服务器。

- nginx+keepalived  （多台nginx，多台实际服务器）

```
yum install nginx keepalived pcre-devel -y

//keepalived.conf
vrrp_instance VI_1{
	state MASTER  //备用机 修改为BACKUP
	...
	priority 100 //备用机设置低
}

service keepalived start/stop
cd /etc/keepalived/

```

用户 ----- 访问虚拟ip ---- keepalived（虚拟出来的ip，检查nginx存活状态，决定访问哪个）--------nginx （选择master/backup）









# 2 devops 

Development和Operations：过程、方法与系统的统称

是一组过程、方法与系统的统称，用于促进开发（[应用程序](https://baike.baidu.com/item/应用程序/5985445)/软件工程）、技术运营和质量保障（QA）部门之间的沟通、协作与整合。



## 2.1 Jenkins

https://www.bilibili.com/video/BV17y4y1v7Qy?p=9&spm_id_from=pageDriver

持续集成-cl： 多次将代码集成到主干，构建/单元测试/结果     build-test-result

持续交付-cd：频繁交给质量团队评审，如果通过，则进入生产阶段。 是在持续集成的基础上。 test-staging-production（手动）

持续部署-cd： 生产环境自动部署

**流程：** 提交代码commit----测试（第一轮：单元测试）----构建（将代码转为可允许实际代码, jenkins）-----测试（第二轮，全面）----部署（artifact版本）-------回滚（做法：修改符号链接）



```java
docker pull jenkins/jenkins:lts    //lts:长期演进版，长期支持
mkdir -p /mydata/jenkins_home
//运行容器
docker run -di --name=jenkins -p 8080:8080 -v /mydata/jenkins_home:/var/jenkins_home jenkins/jenkins:lts
//docker run -d -p 8080:8080 -p 50000:50000 -v /mydata/jenkins_home/:/var/jenkins_home jenkins/jenkins:lts

docker ps -a
chown -R 1000 /mydata/jenkins_home/   //加权限
docker rm jenkins  //删除容器，重新运行

192.168.10.101:8080 //浏览器运行

docker logs jenkins  //查看日志

/mydata/jenkins_home/secrets/initialAdminPassword   //查看密码
  
 x
//优先 安装推荐插件

//插件： SSH 
  
全局工具配置：系统配置--全局工具配置
//jdk： 新增jdk   8u221
 账号密码： 2696671285@qq.com ---- Oracle123
//maven： 3.6.2
  
  
  
系统配置：  （凭据-系统-全局凭据-添加凭据）
//SSH - 新增
//192.168.10.101/ 22/ 选择凭据
//check connection 检查连接

  
mkdir -p /usr/local/jenkins
cd /usr/local/jenkins
vim jenkins.sh
  
#!/usr/bin/env bash
app_name='jenkinsdemo'
docker stop ${app_name}
echo '----top container ---'
docker rm ${app_name}
echo '----rm container ---'
docker run -di --name=${app_name} -p 7070:7070 test/${app_name}:1.0-SNAPSHOT
echo '----start container---'
  
chmod +x ./jenkins.sh    //可执行权限 

  
新建任务--构建自由... - 源码管理-git url - 密码  （码云）
==> 构建-调用顶层maven目标---目标：clean package - POM:pom.xml
==> 增加构建步骤: execute shell script on remote host using ssh   --- Command: /usr/local/jenkins/jenkins.sh
  
  
cd /mydata/jenkins_home/tools/xxxxmaven/conf
vim settings...
拷贝淘宝镜像
 
docker restart jenkins  //重启jenkins
  
docker images
docker ps   //查看容器
//访问项目
```











## 2.2 drone





## 2.2 gitlab





# 3 database

## 3.1 mysql

CREATE TABLE excmd_in_tmp_210618 AS SELECT * FROM excmd_in WHERE main_code in ('99008800000000108PITEM0011035948','99008800000000108PITEM0011141297','99008800000000108PITEM0011036251','99008800000000108PITEM0011170509','99008800000000108PITEM0010941621','99008800000000108PITEM0011171909','99008800000000108PITEM0011110482','99008800000000108PITEM0011141245');

UPDATE excmd_in_tmp_210618 SET status = 0 where create_time > '2021-06-01 00:00:00';



### 3.1.1 索引

​	缺点：维护需要耗费数据库资源；占用磁盘空间；对数据增删改时，需要维护索引

- 主键索引：唯一，不为空

  create teable t_user(id varcher(20) **primary key**, name varcher(20));

  show **index** from t_user;       //查看索引

- 单值索引：单个字段的索引

  create table t_user2(id varcher(20) primary key, name varchar(20), **key(name)**);

  create **index** name_index on t_user(name);

  **drop** index 索引名 on t_user2;   //删除索引

- 唯一索引：唯一，可空

  create table t_user3(id varcher(20) primary key, name varchar(20), **unique(name)**);

  create **unique index** name_index on t_user3(name);

- 复合索引：多个字段的索引

  create table t_user3(id varcher(20) primary key, name varchar(20), age int, **key(name, age)**);

  create index name_age_index on t_user(name, age);

  ```markdown
  # 经典面试
  - name age bir //1.最左前缀原则；2.mysql引擎在查询过程中动态调整查询字段优化索引
    以下哪些能利用索引？
    name bir age   // 可以，调整成name age bir
    name age bir   // 可以
    age bir        // 不可以
    bir age name   // 可以，调整成name age bir
    age bir        // 不可以
    
    
    
  1. mysql底层为主键自动创建索引，并进行排序，便于排序。
  2. mysql索引进一步优化：基于 页 的形式管理； 提出页目录，先找页，再找数据。 （16kb）
  ```

  

**B+Tree 和 B-Tree**：

- B树：每个节点包含：key/data。  即存储data，会导致16kb存储的数量降低，导致树的深度增加，增大磁盘I/O次数。

- B+树（B树基础上优化）：1.非叶子节点只存储键值信息；2.叶子节点之间都有一个链指针；3.所有数据都记录在叶子节点中。 一般在2--4层。 **顶层页常驻内存**

 

**聚簇索引和非聚簇索引**：

- 聚簇索引：数据存储与索引放在一起，索引结构的叶子节点保存了行数据。

- 非聚簇索引：数据和索引分开存储，索引结构的叶子节点指向了数据对应的位置。

  都是辅助索引：复合索引，前缀索引，唯一索引；辅助索引的叶子节点存储的不是行物理位置，而是主键值。需要再进行二次主键索引查找。



不建议uuid作为主键，使用bigint 自增作为主键。







## 3.2 postgresql

- MySQL：目前最受欢迎的开源数据库。**多线程**

- PostgreSQL：最先进的开源数据库。**多进程**





## 3.3 mongodb(nosql)

**三元素**：

- 数据库
- 集合：对应关系数据库中的“表”
- 文档：对应“行”

**mongodb:字典格式，支持分组、索引、主从备份、集群**

**端口号：**

mysql: 3306

redis: 6379

mongodb: 27017



MongoDB的使用 https://www.cnblogs.com/for-easy-fast/p/12914718.html#autoid-1-0-0

Mongodb基本使用方法 https://blog.csdn.net/qq_41856814/article/details/89714627





**笔记：** https://blog.csdn.net/unique_perfect/article/details/107777853

> 集群：一个业务jar部署多套。
>
> 分布式：业务拆分成多个，并进行部署。（分布式一定在集群之上）

mongodb：分布式存储数据库（数据拆分成多份进行存储。）     **没有事务的概念**（不能替换mysql） 

bson/json： 数据容易进行扩展。



```java
//连接mongo： ./mongo --port 27017

show database; /show dbs;  //查看mongo中所有库
use test;  //选中库（没有的话则会创建并选中， 默认不显示空集合的库）
db;  //查询当前所在库
db.createCollection("t_user"); //创建集合
db.help;
db.dropDatabase();

//集合
db.createCollection("集合名称");
db.集合名称.insert({"name":"value"})  //隐式创建
db.t_user.drop();  //删除
db.集合名称.help(); 
show collections; /show tables;  //当前库中有哪些集合

//文档，行
db.集合名称.insert({"name":"value"})  //隐式创建，自动创建 "_id"的key
db.集合名称.insert([{},{},{},{} ])   //插入多条数据
for(var i=0; i<100;i++){     //插入多条数据
  db.集合名称.insert({...});
}
db.集合名称.remove({}) //删除所有
db.集合名称.remove({name:"张三"}) //条件删除
db.集合名称.update({更新条件},{更新内容}) //查询，删除之前，新增内容
db.集合名称.update({更新条件},{$set:{更新内容}})  //查询，新增。 默认只修改符合条件的第一条
db.集合名称.update({更新条件},{$set:{更新内容}},{multi:true}) //开启多条匹配
db.集合名称.update({更新条件},{$set:{更新内容}},{multi:true,upsert:true})//没有符合条件时，插入
db.集合名称.update({更新条件},{$inc:{age:10,id:5}})//指定字段自增步长

//查询
db.集合名称.find();  //查找所有
db.集合名称.findOne({条件});
db.集合名称.find({条件},{显示字段}); // 1显示，0不显示  {name:1,age:1}
db.集合名称.find().sort({age:1,name:-1}) //1 升序  -1 降序
//分页： skip((pageNow-1)*pageSize).limit(pageSize)
db.集合名称.find().sort({...}).skip(起始条数).limit(每页数) 
db.集合名称.count();
db.集合名称.find({}).count();
db.集合名称.find({name:/xxx/})   //模糊查询，  错误写法：find({name:"/xxx/"})
//高级查询（$eq, $and, $or, $gt(>), $lt(<), $gte(>=), $lte(<=), $nor(既不是，也不是) ）
db.集合名称.find({name:{$eq:"xxx"}})
db.集合名称.find({$and[{xx:xx,yy:yy}, {vv:vv}]})
db.集合名称.find({$or[{xx:xx,yy:yy}, {vv:vv}]})
db.集合名称.find({$nor[{xx:xx,yy:yy}, {vv:vv}]})
db.集合名称.find({age:{$gt:6, $lt:10})

```



```ABAP
主从搭建（从节点只负责数据同步/冗余备份）：   （4.0废弃）
1. 启动一个主节点： ./mongod --port --dbpath --master
2. 启动多个从节点： 
./mongod --port --dbpath --slave --source ip   //复制所有库
./mongod --port --dbpath --slave --source ip --only 库名 --slavedelay //复制指定库 (延迟同步)




rs.slaveOk(); :执行后，开启从节点查询
rs.status;  :查看节点状态   




副本集群搭建（Replica Set）：
解决问题： 1.保证系统高可用，自动故障转移； 2.无法解决集群中服务器单点压力问题。
步骤：
1. 配置主机名： ip与别名映射
  vi /etc/hosts        
  192.168.2.2  xxx     
  ping xxx
2. 创建3个副本数据目录
	mkdir  repl1 repl2 repl3
3. 启动3个副本集机器
	./mongod --port 27017 --dbpath /root/repl1 --bind_ip 0.0.0.0 --replSet myreplace/xxx:27018
	./mongod --port 27018 --dbpath /root/repl2 --bind_ip 0.0.0.0 --replSet myreplace/xxx:27019
	./mongod --port 27019 --dbpath /root/repl3 --bind_ip 0.0.0.0 --replSet myreplace/xxx:27017
	
4. 配置副本集 （配置必须在mongo中默认的admin库中左集群的配置）
 - 通过client登录到任意一个节点
 		./mongo --port 27017
 - 使用当前连接的mongo服务中的admin库
 		use admin
 - 定义配置信息
 		var config ={
 			_id:"myreplace",
 			members:[
 				{_id:0, host:"xxx:27017"},
 				{_id:1, host:"xxx:27018"},
 				{_id:2, host:"xxx:27019"}
 			]
 		}
 		rs.initiate(config);
5. 副本集节点添加（自动同步原有数据）
	- 启动节点： mkdir -p repl4
		./mongod --port 27020 --dbpath /root/repl4 --bind_ip 0.0.0.0 --replSet myreplace/xxx:27017
	- 在现有主节点中admin库中添加副本节点
		rs.add("xxx:27020");
6. 副本集 删除节点（admin中执行）
	rs.remove("xxx:27017")   //不能删除主节点
	
	
	
	
	
	
分片：数据拆分到多个机器上
应用情况： 1. 单机机器磁盘不够； 2. 单个mongod不能满足写数据的性能； 3.将大量数据放在内存中提高性能






- ./mongo --port 27022
- show dbs; //查看
- use ems;
- show collections;//查看
- db.emps.count();
```





## 3.4 redis(nosql)



## 3.5 elasticsearch



## 3.6 hbase



## 3.7 cassandra



## 3.8 分库分表

问题

- 数据库连接/io负载  （解决：读写分离）
- 单点故障

写：主库

读：从库        一主多从    1:10

mysql复制  ：   双主复制，多级复制 

如何自动切换数据源？ 

---->**aop**：主程序流程中，插入其他抽取功能。切面编程

```
//从库---读
@pointcut("execution(* com.xx.xx.service..*.query*(..)) ||com.xx.xx.service..*.find*(..))  ")
//主库---写
@pointcut("execution(* com.xx.xx.service..*.insert*(..)) ||com.xx.xx.service..*.add*(..))  ")
```

AbstractRoutingDataSource： 逻辑数据源

重写determineCurrentLookupKey方法 ，切换数据源



# 4 rpc 

## 4.1 corba



## 4.2 restful



## 4.3 apache thrift



## 4.4 google protobuf









# 5 message queue



## 5.1 zeromq



## 5.2 kafka



## 5.3 mqtt



## 5.4 rabbitmq

官网---Docs---Get Started

https://blog.csdn.net/unique_perfect/article/details/109380996



生产者（写消息）----- 消息队列 ----- 消费者（消费消息）  ：解耦

基于AMQP协议：advanced message queuing protocol

erlang语言开发。

find / -name rabbit.config.example

virtual host: 虚拟主机 ，添加对应用户

10.15.0.9:5672



**第一种模型：直连**

创建连接工厂----创建连接对象----获取连接通道----通道绑定消息队列----发布消息----关闭 

> ctrl + H 

**队列持久化/ 消息持久化**： 分别设置参数.

**exclusive**: 是否独占队列 （false：多个连接共用一个队列）



**第二种模型：work queue**   

多个消费者消费不同消息。



**第三种模型：publish/subscribe**  ，fanout广播， 多个消费者消费同一条消息

创建连接工厂----创建连接对象----获取连接通道----**绑定交换机**----创建临时队列----临时队列绑定到交换机----处理消息



**第四种模型：Routing **  路由        （在fanout广播模式下，不同的消息，被不同的队列消费），**定向发送**

订阅模型： direct 直连

生产者发送消息给交换机（携带RoutingKey）----- 交换机绑定队列（携带RoutingKey）---队列对应的消费者



**第五种模型：topic**（动态路由）      **通配符** ： 多个单词组成，‘.’分割

- *：匹配一个单词。    eg:  \*.orange.\*
- #：匹配一个或多个单词





### **应用场景**

- **异步处理**

  用户------注册信息写入数据库（返回给用户）-----写入消息队列   -----发送注册邮件

  ​																											 -----发送注册短信

  注册邮件和短信不是必须流程。  

- **应用解耦**

  订单系统-------消息队列------库存系统  （库存系统异常，不影响订单系统下单）

- **流量削峰**

  秒杀活动： 用户请求-----消息队列------秒杀业务处理系统

  超过队列最大值，抛弃请求或者跳转错误页面。



### RabbitMQ的集群

多个mq

#### 普通集群（副本集群）

- 解决问题：当master节点宕机，可以对queue中信息进行备份（slave）

- 主从mq， 分散消费压力（**提供并发**）

- 仅仅只能同步exchange， 不能同步队列  （**队列中才存放消息**）
- 如果**主节点宕机**，**从节点**都不能提供能力



#### 镜像集群

- 消息100%不丢失，

- 各节点（主节点，从节点）之间**进行消息自动同步**（可同步exchange及队列）

 





```java
@SpringBootTest(classes = RabbitmqSpringbootApplication.class)
@RunWith(SpringRunner.class)
public class TestTabbitMq{
  @Autoried
  private RabbitTemplate rt;
  @Test
  public void test(){
    
  }
}
```









# 6 file system



## 6.1 hdfs



## 6.2 ceph



## 6.3 minio



# 7 stream processing



## 7.1 apache spark



## 7.2 apache flink



## 7.3 apache storm







# 8 web



## 8.1 babeljs (toolset)



## 8.2 webpack (toolset)



## 8.3 nodejs (toolset)



## 8.4 vue/react/angular





## 8.5 electronjs(desktop)







# 9 tool

## 9.1 maven

- 一键迁移 （myeclipse，idea...）
- 支持远程（热）部署  （远程服务器地址  账号/密码）
- 支持持续集成（CI）

```java
src/main/java  //src/main/webapp   相当于以前web的webRoot
src/main/resources   //运行时的配置文件
src/test/java
src/test/resources
  
pom.xml  //maven项目配置文件
```



## 9.2 gradle

- 项目构建工具：  编辑/编译/测试/打包/部署...  （ant/maven/gradle）

- 使用groovy语言编写

底层转换为Java语言，  也是基于jvm运行。

idea(Tools --> Groovy Console...)

```groovy
int a = 222
def a2 = 22   //弱类型   groovy中最终都是对象类型
println a.class

def play(def num){
  println num
}
play("hhhh")

def list = ['1','2']
for(a in list){
  println a
}

def map = ['a':1, 'b':2]
println map.a
for(m in map){
  println m.key + '---' + m.value
}

//闭包
def m1 = {
  println 'hello'
}
def play(Closure c){   //Closure不需要导包
  c()
}
play(m1)
//带参闭包
def m2={
  v-> println 'hell0'+v
}
def play(Closure c){
  c("zhangsan")
}
play(m2)
```





## 9.3 git

**ssh 免密码登录**：

1. ssh-keygen -t rsa -C "zhouffan@qq.com"        //*#-t表示类型选项，这里采用rsa加密算法*

2. 用户名/.ssh/id_rsa.pub    中的内容添加到远程 “SSH公钥”   //或者 ssh-copy-id ldz@192.168.0.1。ssh-copy-id会将公钥写到远程主机的 ~/ .ssh/authorized_key 文件中。















# 其他

## 1.0 局域网下，Windows通过SSH连接Linux

```
Linux安装SSH
Ubuntu默认并没有安装ssh服务，如果通过ssh链接ubuntu，需要自己手动安装ssh-server。
Ubuntu终端输入：sudo apt-get install openssh-server

安装完成后，启动服务。
Ubuntu终端输入：sudo /etc/init.d/ssh start

查看Ubuntu IP地址
终端输入：ifconfig

xshell连接Ubuntu
新建->host填入Ubuntu的IP地址，端口默认22，protocol默认ssh->连接填入用户名密码即可 
```

## 2.0 ====>工具

FastStone ：画图工具





# 问题归纳

## 1.0 apt-get install E: 无法定位软件包问题

https://blog.csdn.net/beizhengren/article/details/77678603?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-1&spm=1001.2101.3001.4242

添加镜像源



## 1.2 docker 下载的tomcat 访问404

```
使用命令: docker exec -it 运行的tomcat容器ID /bin/bash 进入到tomcat的目录
进入webapps文件夹,发现里面是空的(tomcat默认的欢迎页面实际上放在的路径应该是:webapps/ROOT/index.jsp或者index.html)
发现旁边还有个webapps.dist的文件,进入才发现原本应该在webapps文件中的文件都在webapps.dist文件中,现在也不知道为什么！！！
```

cp -r ./webapps.dist/* ./webapps/