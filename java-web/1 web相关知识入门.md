# 1 configuration

## 1.1 docker

​                                        仓库（push/pull）

Sockerfile ==(build)==>镜像 <==(save/load)==>tar文件

​                                         容器（commit/run）



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

docker ps -a
chown -R 1000 /mydata/jenkins_home/   //加权限
docker rm jenkins  //删除容器，重新运行

192.168.10.101:8080 //浏览器运行

docker logs jenkins  //查看日志

/mydata/jenkins_home/secrets/initialAdminPassword   //查看密码
  
 
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











# 其他

## 1.局域网下，Windows通过SSH连接Linux

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

