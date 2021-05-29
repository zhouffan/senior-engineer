[《后端架构师技术图谱》](https://github.com/xingshaocheng/architect-awesome)

# 1 configuration

## 1.0 linux

https://www.cnblogs.com/zhuchenglin/p/8686924.html

linux目录结构



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

一些启动参数：
后台运行容器：-d
容器内外端口映射：-p 内部端口号:外部端口号
目录映射：-v dir name : dir
指定映像版本：name:ver





## 1.2 kubernetes







# 2 devops

## 2.1 Jenkins





## 2.2 drone





## 2.2 gitlab





# 3 database

## 3.1 mysql



## 3.2 postgresql



## 3.3 mongodb(nosql)



## 3.4 redis(nosql)



## 3.5 elasticsearch



## 3.6 hbase



## 3.7 cassandra



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









