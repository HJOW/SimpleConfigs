package org.duckdns.hjow.util.simpleconfig;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/** 단일 설정 그룹이 이 클래스의 인스턴스가 됩니다. */
public class ConfigInstance implements Serializable {
    private static final long serialVersionUID = -8942489641252500910L;
    protected Map<String, String> configs = new HashMap<String, String>();
    protected transient String fileName = "config";
    protected transient long   readDate = 0L;
    
    /** 설정의 키들을 조회해 반환합니다. */
    public Set<String> keySet() {
        return getConfigs().keySet();
    }
    
    /** 설정 항목 하나의 값을 조회해 반환합니다. */
    public String getConfig(String key) {
        return getConfigs().get(key);
    }
    
    /** 설정 파일 전체 내용을 조회해 반환합니다. */
    public synchronized Map<String, String> getConfigs() {
        if(configs == null) return readConfigs();
        if(getExpireGap() >= 0 && System.currentTimeMillis() - getReadDate() >= getExpireGap()) return readConfigs();
        return configs;
    }
    
    /** 설정 파일 조회 (파일을 다시 읽는다.) */
    public synchronized Map<String, String> readConfigs() {
        InputStream inp = null;
        Properties prop = new Properties();
        
        // 파일 읽기
        try {
            inp = ConfigManager.class.getClassLoader().getResourceAsStream("/" + getFileName() + ".properties");
            if(inp != null) { prop.load(inp); inp.close(); inp = null; }
            
            inp = ConfigManager.class.getClassLoader().getResourceAsStream("/" + getFileName() + ".xml");
            if(inp != null) { prop.loadFromXML(inp); inp.close(); inp = null; }
        } catch(Exception ex) {
            if(inp != null) { try { inp.close(); } catch(Exception closingErr) { throw new RuntimeException(closingErr.getMessage(), ex); } }
            inp = null;
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
        // Map 으로 변환
        Map<String, String> map = new HashMap<String, String>();
        Set<String>  keys = prop.stringPropertyNames();
        
        for(String k : keys) {
            map.put(k, prop.getProperty(k));
        }
        prop.clear();
        
        setReadDate(System.currentTimeMillis());
        configs = map;
        return map;
    }

    /** 설정 파일명을 반환합니다. */
    public String getFileName() {
        return fileName;
    }

    /** 설정 파일명을 변경합니다. 파일명이 달라지므로 설정 내용을 다시 읽습니다. (동일 파일명을 넣으면 아무 일도 하지 않습니다.) 양 측 공백이 제거되며, / 및 \\ 기호는 제거됩니다. */
    public synchronized void setFileName(String fileName) {
        if(fileName == null) throw new NullPointerException("This field cannot be null !");
        
        fileName = fileName.trim();
        if(fileName.equals("")) throw new NullPointerException("This field cannot be a empty string !");
        
        if(this.fileName.equals(fileName)) return;
        
        this.fileName = fileName.replace("/", "").replace("\\", "");
        readConfigs();
    }

    /** 설정 항목을 임시 세팅합니다. (직접 호출하지 마세요. 직렬화를 위해 구현한 메소드입니다.) */
    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }

    /** 설정 내용을 읽은 시간 정보를 반환합니다. (Milliseconds) */
    public long getReadDate() {
        return readDate;
    }

    /** 직접 호출하지 마세요. 이 라이브러리 내부 동작 및 직렬화를 위해 구현한 메소드입니다. */
    public void setReadDate(long readDate) {
        this.readDate = readDate;
    }
    
    /** 읽은 설정 컨텐츠의 만료시간을 반환합니다. (Milliseconds) 이 시간이 지나면 설정 파일을 다시 읽습니다. */
    public long getExpireGap() {
        return 60000L;
    }
}
