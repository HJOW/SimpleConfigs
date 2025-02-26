# SimpleConfigs
자바 기반 프로젝트에 간단히 설정 파일 기능을 적용합니다.  
설정 파일을 프로젝트 내에 배치할 수 있으며, 데이터는 키와 값 형태로 넣을 수 있습니다.  
자바 코드 내에서 이 파일 내용을 읽어 사용할 수 있습니다.

## 다음 파일을 읽습니다.
프로젝트의 자바 클래스패스 경로 최상단에 있는
```
config.properties
config.xml
```
Map 형태로 순서대로 읽으며, 키가 겹치는 항목은 나중에 읽은 것이 적용됩니다.

매번 디스크에 액세스하지 않습니다.  
파일을 읽어 메모리 상에 담아두고, 그로부터 10분이 지난 경우에만 다시 읽습니다.

## 자바 SE 6 (JRE / JDK 1.6)  이상을 지원합니다.

## Maven pom.xml dependency snippets
```
<dependency>
    <groupId>org.duckdns.hjow</groupId>
    <artifactId>simpleconfigs</artifactId>
    <version>0.0.1</version>
</dependency>
```

버전 정보는 [Maven Central - simpleconfigs](https://central.sonatype.com/artifact/org.duckdns.hjow/simpleconfigs/versions) 를 참고해 주세요.

## 사용 방법
1. 자바 코드 안에서, 설정을 읽어야 할 코드 위치에서 다음과 같이 사용합니다.
```
// import org.duckdns.hjow.util.simpleconfig.ConfigManager;
String option = ConfigManager.getConfig("option1");
```

2. 자바 클래스패스에 파일을 배치합니다.
클래스패스 경로 내 최상단에 설정 파일 (config.xml, config.properties, ...) 을 배치합니다.

이를테면,  
서블릿/jsp 웹 프로젝트의 경우, 서버에서는 [웹경로]/WEB-INF/classes/ 안에 설정 파일을 배치합니다.  
eclipse 툴로 만든 Maven Project 의 경우, [프로젝트경로]/src/main/resources/ 안에 설정 파일을 배치합니다.  
(전자정부프레임워크도 해당)  
eclipse 툴로 만든 기본 Java Project 의 경우, [프로젝트경로]/src/ 안에 설정 파일을 배치합니다.  

내용은 다음 형식으로 입력합니다. (xml 파일 기준)  
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
<comment/>
<entry key="키1">값1</entry>
<entry key="키2">값2</entry>
<entry key="키3">값3</entry>
</properties>
```
XML 문법에 영향을 주는 내용 기입을 위해, \<![CDATA[   이 곳에 내용을 기입   ]]> 문법 사용이 가능합니다.

## 참고사항
클래스패스 내 파일은, 프로그램이 구동 중일 때에는 외부에서 수정하더라도 프로그램에 영향을 주지 않으며,  
다음번 구동 시 적용됩니다.

## License

```
   Copyright 2025 HJOW

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
