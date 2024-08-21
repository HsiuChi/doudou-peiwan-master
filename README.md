# 豆豆陪玩  <img src="https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240522160140.jpg" width="36px">

一个基于 Spring Boot、MyBatis、MySQL、Redis、ElasticSearch、WebSocket 、RabbitMQ 等技术栈实现的陪玩平台，采用主流的互联网技术架构、全新的UI设计，拥有完整的大神发布/搜索/评论/福利秒杀等，代码完全开源，用户可以浏览首页推荐内容，搜索喜爱的陪玩大神进行陪玩业务👍 。

## 一、项目介绍

### 项目演示
#### 前台首页展示
![image](https://github.com/HsiuChi/doudou-peiwan-master/blob/master/msg/20240521152513.jpg)

#### 前台消费页面展示
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
