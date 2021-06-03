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
  
  
  
```



**vim 编辑命令**

- 进入文件
- **命令模式**
- **按i编辑模式**
- 编辑文件
- esc进入低行模式
- :wq/ q! 退出

```java
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



https://www.cnblogs.com/for-easy-fast/p/12914718.html#autoid-1-0-0

https://blog.csdn.net/qq_41856814/article/details/89714627









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