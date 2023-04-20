Springboot集成urule开源版本
-------------------
## Urule
URule是一款基于RETE算法纯Java的开源规则引擎产品，提供了向导式规则集、脚本式规则集、决策表、决策树、评分卡及决策流共六种类型的规则定义方式，配合基于WEB的设计器，可快速实现规则的定义、维护与发布。
用来替换原有的drools规则引擎,有一部分原因是因为URule自带了配置规则的UI界面
本例中采用urule客户端与服务端分离的设计
## Urule pro
URule Prow文档:https://www.bstek.com/resources/doc/3.x/
## Urule Server
urule的Server端,用来配置规则(知识包),并暴露给客户端,本例中知识库存储在mysql数据库中

### 1.配置Urule Servlet
#### URuleServletRegistration.java
```java
@Component
public class URuleServletRegistration {
	@Bean
	public ServletRegistrationBean registerURuleServlet(){
		return new ServletRegistrationBean(new URuleServlet(),"/urule/*");
	}
} 
```
### 2.配置urule知识库数据源、导入配置文件
#### application.yml
```yml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/urule?useUnicode=true&characterEncoding=utf-8
    driver-class-name: com.mysql.jdbc.Driver
    username: root
    password: 1234
  jackson:
    default-property-inclusion: non_null

urule:
  repository:
    databasetype: mysql
    datasourcename: datasource

server:
  port: 8787
```
#### Config.java
```java
@Configuration
@ImportResource({"classpath:urule-console-context.xml"})
@PropertySource(value = {"classpath:urule-console-context.properties"})
public class Config {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourceLoader() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setOrder(1);
        return configurer;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource datasource() {
        return DataSourceBuilder.create().build();
    }
}
```
### 3.启动Application类
#### DemoApplication.java
```java
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

```
访问地址:http://localhost:8787/urule/frame 即可看到urule的规则配置页面

![](https://raw.githubusercontent.com/worstEzreal/mdpic/master/1.png)

## Urule Client
Urule的客户端,即调用规则的一方

### 1.配置urule知识库地址
#### application.yml
```yml
urule:
  resporityServerUrl: http://localhost:8787
  knowledgeUpdateCycle: 1

server:
  port: 7878
```

### 2.引入urule配置文件
#### RuleConfig.java
```java
@Configuration
@ImportResource({"classpath:urule-core-context.xml"})
public class RuleConfig {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourceLoader() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreUnresolvablePlaceholders(true);
        configurer.setOrder(1);
        return configurer;
    }
}
```

#### 3.配置KnowledgePackageReceiverServlet

此Servlet用于接收Urule服务端发布的知识包(不想用这个功能可以不配)

##### URuleServletRegistration.java
```java
@Component
public class URuleServletRegistration {
	@Bean
	public ServletRegistrationBean registerURuleServlet(){
		return new ServletRegistrationBean(new KnowledgePackageReceiverServlet(),"/knowledgepackagereceiver");
	}
}
```

#### 4.启动Application类
##### ClientApplication.java
```java
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}

```

到这里Urule的服务端和客户端就都配置完了。

## 配置规则


#### 1.添加规则&发布

##### 配置参数
![image](https://raw.githubusercontent.com/luogang7/file/main/Snipaste_2023-04-20_11-24-16.png?token=GHSAT0AAAAAACBUQ25OHJ7LBQXJ2CBY7VZMZCAW7DQ)
##### 配置规则
![image](https://raw.githubusercontent.com/luogang7/file/main/Snipaste_2023-04-20_11-24-44.png?token=GHSAT0AAAAAACBUQ25PVRRCAL24FYNT7S4YZCAXAPA)

##### 配置推送客户端
![image](https://raw.githubusercontent.com/luogang7/file/main/Snipaste_2023-04-20_11-25-58.png?token=GHSAT0AAAAAACBUQ25PEEFJJMN66NBUCGL6ZCAXCYA)

##### 发布知识包
![](https://raw.githubusercontent.com/luogang7/file/main/Snipaste_2023-04-20_11-26-22.png?token=GHSAT0AAAAAACBUQ25PW33D4FPZOXIBN7B6ZCAXDEQ)

#### 2.编写Controller测试
##### RuleController.java
```java
@RestController
@ResponseBody
public class RuleController {
    @GetMapping("rule")
    public String rule(@RequestParam BigDecimal threeDay,
                       @RequestParam BigDecimal sevenDay,
                       @RequestParam BigDecimal fifteenDay
    ) throws IOException {
        //创建一个KnowledgeSession对象
        KnowledgeService knowledgeService = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        KnowledgePackage knowledgePackage = knowledgeService.getKnowledge("aaa/bag");
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);

        Map<String, Object> param = new HashMap();
        param.put("threeDay", threeDay);
        param.put("sevenDay", sevenDay);
        param.put("fifteenDay", fifteenDay);
        session.fireRules(param);

        Boolean result = (Boolean) session.getParameter("result");
        System.out.println(result);
        return String.valueOf(result);
    }
}

```

#### 3.调用测试
http://localhost:7878/rule?threeDay=10&sevenDay=20&fifteenDay=30 返回false
http://localhost:7878/rule?threeDay=11&sevenDay=21&fifteenDay=31 返回true
-------------------

