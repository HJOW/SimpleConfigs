package org.duckdns.hjow.util.simpleconfig;

import java.util.Map;
import java.util.Set;


/**
 * <pre>
 * 프로젝트의 설정을 파일로 관리할 때 사용할 유틸리티 클래스입니다.
 * 
 * 클래스패스 최상단에서 config.properties, config.xml 파일을 읽습니다.
 *     jsp / 서블릿 웹 프로젝트를 예로 들면, WEB-INF/classes/ 디렉토리에 해당합니다.
 *     
 * 파일은 둘 다 읽으며, 파일이 없으면 건너 뜁니다. 두 파일 모두 없으면 비어있는 상태가 될 뿐 예외가 발생하지 않습니다.
 *     config.properties 가 먼저 읽히고
 *     config.xml        내용을 읽습니다. 이 때, 중복되는 키의 값들은 덮어 씌워집니다.
 * 즉 config.xml 파일 내용이 더 우선권을 갖습니다.
 *  
 * </pre>
 */
public class ConfigManager {
    protected static ConfigInstance instances;
    
    /** 설정의 키 목록을 조회 */
    public static Set<String> keySet() {
        insureInstanceExists();
        return instances.keySet();
    }
    
    /** 설정에서 항목 하나의 값을 조회 */
    public static String getConfig(String key) {
        insureInstanceExists();
        return instances.getConfig(key);
    }
    
    /** 설정 파일 내용을 조회 */
    public static Map<String, String> getConfigs() {
        insureInstanceExists();
        return instances.getConfigs();
    }
    
    /** 인스턴스 객체가 존재함을 보장 */
    protected static synchronized void insureInstanceExists() {
        if(instances == null) instances = new CommonConfigInstance();
    }
    
    /** 어떤 문자열이 '/'로 끝나는지 확인, 이미 '/'로 끝나면 그대로 반환, 아니면 '/'를 붙여서 반환. 단 null 은 그대로 반환 */
    public static String insureEndSlash(String mayBeUrl) {
        if(mayBeUrl == null) return null;
        if(! mayBeUrl.endsWith("/")) return mayBeUrl + "/";
        return mayBeUrl;
    }
}
