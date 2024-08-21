#   <img src="https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240522160140.jpg" width="36px">豆豆陪玩

一个基于 Spring Boot、MyBatis、MySQL、Redis、ElasticSearch、WebSocket 、RabbitMQ 等技术栈实现的陪玩平台，采用主流的互联网技术架构、全新的UI设计，拥有完整的大神发布/搜索/评论/福利秒杀等，代码完全开源，用户可以浏览首页推荐内容，搜索喜爱的陪玩大神进行陪玩业务👍 。

## 一、项目介绍

### 项目演示
#### 前台首页展示
![image](https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240521152513.jpg)

#### 大神详情页面展示
![image](https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240821155824.png)

### 架构图
![image](https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240601154249.png)

### 组织结构
```
doudoupeiwan
├── doudoupeiwan-api -- 定义一些通用的枚举、实体类，定义 DO\DTO\VO 等
├── doudoupeiwan-core -- 核心工具/组件相关模块，如工具包 util， 通用的组件都放在这个模块（以包路径对模块功能进行拆分，如搜索、缓存、推荐等）
├── doudoupeiwan-service -- 服务模块，业务相关的主要逻辑，DB 的操作都在这里
├── doudoupeiwan-ui -- HTML 前端资源（包括 JavaScript、CSS、Thymeleaf 等）
├── doudoupeiwang-web -- Web模块、HTTP入口、项目启动入口，包括权限身份校验、全局异常处理等
```

### 配置文件说明
resources
- application.yml: 主配置文件入口
- application-config.yml: 全局的站点信息配置文件
- logback-spring.xml: 日志打印相关配置文件
- liquibase: 由liquibase进行数据库表结构管理
  
resources-env
- xxx/application-dal.yml: 定义数据库相关的配置信息
- xxx/application-image.yml: 定义上传图片的相关配置信息
- xxx/application-web.yml: 定义web相关的配置信息
  

## 二、内容梗概
* 短信登录

这一块使用redis共享session来实现，加入JWT单点登录，保证用户的安全性

* 大神查询缓存

考虑多种并发安全问题，比如缓存击穿，缓存穿透，缓存雪崩，数据同步性问题，具体情况具体解决

* 简单通信

通过WebSocket实现用户和大神的双向通信，实现简单的交流功能

* 优惠卷秒杀

通过本章节，我们可以学会Redis的计数器功能， 结合Lua完成高性能的redis操作，同时学会Redis分布式锁的原理，包括Redis的三种消息队列

* UV统计

主要是使用Redis来完成统计功能

* 用户签到

使用Redis的BitMap数据统计功能

* 用户关注，点赞

基于Set集合的关注、点赞、取消关注、取消点赞，共同关注等等功能。


版本迭代优化
```
1)、加入消息队列 RabbitMQ，对订单，关注等接口进行优化。 - （削峰解耦，保证数据的最终一致性）
2)、通过自定义线程池+CompletableFuture并发提高导入数据库的能力，和用户搜索体验
3)、解决一人一单 - （分布式锁）
4)、解决超卖问题 - （乐观锁）
5)、使用ES全文检索功能，并通过优先级队列来减少TOPN运算过程中的内存占用，优化搜索体验
```
