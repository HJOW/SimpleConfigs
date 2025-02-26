package org.duckdns.hjow.util.simpleconfig;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/** 설정 정보를 담은 인스턴스들은 이 인터페이스의 구현체입니다. */
public interface ConfigInstance extends Serializable {
    /** 설정의 키들을 조회해 반환합니다. */
    public Set<String> keySet();
    
    /** 설정 항목 하나의 값을 조회해 반환합니다. */
    public String getConfig(String key);
    
    /** 설정 전체 내용을 조회해 반환합니다. */
    public Map<String, String> getConfigs();
    
    /** 설정 전체 내용을 조회해 반환합니다. (파일을 다시 읽습니다.) */
    public Map<String, String> readConfigs();
    
    /** 읽은 설정 컨텐츠의 만료시간을 반환합니다. (Milliseconds) 이 시간이 지나면 설정 파일을 다시 읽습니다. */
    public long getExpireGap();
    
    /** 설정 내용을 읽은 시간 정보를 반환합니다. (Milliseconds) */
    public long getReadDate();
}
