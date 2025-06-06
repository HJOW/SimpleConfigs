package org.duckdns.hjow.util.simpleconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/** 단일 설정 그룹이 이 클래스의 인스턴스가 됩니다. */
public class CommonConfigInstance implements SaveableInstance {
    private static final long serialVersionUID = -8942489641252500910L;
    protected Map<String, String> configs = new HashMap<String, String>();
    protected transient Class<?> classBasic = ConfigManager.class;
    protected transient List<String> additionals = new ArrayList<String>();   
    protected transient String  fileName  = "config";
    protected transient String  comments  = "Configs";
    protected transient String  importKey = "__import";
    protected transient long    readDate  = 0L;
    protected transient boolean useYaml   = true;
    protected transient boolean firsts    = true;
    
    /** 설정의 키들을 조회해 반환합니다. */
    public Set<String> keySet() {
        return getConfigs().keySet();
    }
    
    /** 설정 항목 하나의 값을 조회해 반환합니다. */
    public String getConfig(String key) {
        return getConfigs().get(key);
    }
    
    /** 설정 파일 전체 내용을 조회해 반환합니다. 캐시를 사용합니다. */
    public synchronized Map<String, String> getConfigs() {
        if(configs == null) return readConfigs();
        if(getExpireGap() >= 0 && System.currentTimeMillis() - getReadDate() >= getExpireGap()) return readConfigs();
        return configs;
    }
    
    /** 설정 파일 전체 내용을 조회해 반환합니다. (파일을 다시 읽습니다.) */
    public synchronized Map<String, String> readConfigs() {
    	if(firsts) processAtFirst();
    	
        InputStream inp = null;
        Properties propFirst = new Properties();
        
        // 파일 읽기
        try {
            inp = classBasic.getClassLoader().getResourceAsStream("/" + getFileName() + ".properties");
            if(inp != null) { propFirst.load(inp); inp.close(); inp = null; }
            
            inp = classBasic.getClassLoader().getResourceAsStream("/" + getFileName() + ".xml");
            if(inp != null) { propFirst.loadFromXML(inp); inp.close(); inp = null; }
            
            if(useYaml()) {
            	inp = classBasic.getClassLoader().getResourceAsStream("/" + getFileName() + ".yaml");
                if(inp != null) { 
                	Map<String, ?> reads = YamlReflexionUtil.fromYaml(inp, "UTF-8"); inp.close(); inp = null;
                	Set<String> keyReads = reads.keySet();
                	for(String kx : keyReads) {
                		propFirst.setProperty(kx, reads.get(kx) == null ? "" : reads.get(kx).toString());
                	}
                }
            }
        } catch(Exception ex) {
            if(inp != null) { try { inp.close(); } catch(Exception closingErr) { throw new RuntimeException(closingErr.getMessage(), ex); } }
            inp = null;
            throw new RuntimeException(ex.getMessage(), ex);
        }
        
        Properties propAfter = new Properties();
        readMores(propAfter);
        
        propAfter.putAll(propFirst);
        propFirst.clear();
        
        propFirst = propAfter;
        propAfter = null;
        
        // Map 으로 변환
        Map<String, String> map = new HashMap<String, String>();
        Set<String>  keys = propFirst.stringPropertyNames();
        
        for(String k : keys) {
            map.put(k, propFirst.getProperty(k));
        }
        propFirst.clear();
        
        setReadDate(System.currentTimeMillis());
        configs = map;
        return map;
    }
    
    /** 스트림으로부터 Properties 정보를 읽습니다. 스트림을 닫지 않습니다. */
    protected Properties readPropertyStream(InputStream inp, String ext) {
    	Properties prop = new Properties();
    	if(ext == null) ext = "properties";
    	ext = ext.trim().toLowerCase();
    	
    	try {
    		if(ext.equals("properties")) prop.load(inp);
    		if(ext.equals("xml")) prop.loadFromXML(inp);
    		if(ext.equals("yaml")) YamlReflexionUtil.fromYaml(inp, "UTF-8");
            
            return prop;
        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 추가 파일을 읽습니다. */
    protected void readMores(Properties prop) throws RuntimeException {
        // importKey 읽기
        if(importKey != null) {
        	StringTokenizer spaceTokenizer = new StringTokenizer(importKey.trim(), "\n");
        	while(spaceTokenizer.hasMoreTokens()) {
        		String importValueOne = spaceTokenizer.nextToken().trim();
        		if(importValueOne.equals("")) continue;
        		readMores(prop, importValueOne);
        	}
        }
        
        // 추가 파일로 지정된 파일 읽기
        try {
            for(String pathOne : getAdditionals()) {
                if(pathOne == null) continue;
                readMores(prop, pathOne);
            }
        } catch(Exception ex) {
        	if(ex instanceof RuntimeException) throw (RuntimeException) ex;
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    /** 추가 파일을 읽습니다. */
    protected void readMores(Properties prop, String pathOne) throws RuntimeException {
    	InputStream inp = null;
    	try {
    		if(pathOne == null) return;
    		if(pathOne.startsWith("file:")) {
                File filePath = new File(pathOne.substring(5));
                if(! filePath.exists()) return;
                if(filePath.isDirectory()) return;
                
                String fName = filePath.getName().toLowerCase().trim();
                inp = new FileInputStream(filePath);
                
                if(fName.endsWith(".xml"))       prop.putAll(readPropertyStream(inp, "xml"));
                else if(fName.endsWith(".yaml")) prop.putAll(readPropertyStream(inp, "yaml"));
                else                             prop.putAll(readPropertyStream(inp, "properties"));       
                inp.close(); inp = null;
            } else {
                inp = classBasic.getClassLoader().getResourceAsStream(pathOne);
                if(inp != null) {
                	String pathLower = pathOne.toLowerCase();
                    if(pathLower.endsWith(".xml"))       prop.putAll(readPropertyStream(inp, "xml"));
                    else if(pathLower.endsWith(".yaml")) prop.putAll(readPropertyStream(inp, "yaml"));
                    else                                 prop.putAll(readPropertyStream(inp, "properties"));
                    
                    inp.close(); inp = null;
                }
            }
    	} catch(Exception ex) {
    		if(inp != null) { try { inp.close(); } catch(Exception closingErr) { throw new RuntimeException(closingErr.getMessage(), ex); } }
            inp = null;
            throw new RuntimeException(ex.getMessage(), ex);
        }
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
    
    /** 런타임 시간 내에서 이 객체 최초 사용 시 호출됩니다. */
    protected void processAtFirst() {
    	firsts = false;
    	setUseYaml(YamlReflexionUtil.isYamlSupport());
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

    /** 설정 조회 시 추가로 읽을 파일 경로 목록을 반환합니다. */
    public List<String> getAdditionals() {
        return additionals;
    }

    /** 설정 조회 시 추가로 읽을 파일 경로 목록을 세팅합니다. */
    public void setAdditionals(List<String> additionals) {
        this.additionals = additionals;
        readConfigs();
    }
    
    /** 설정 파일 경로를 추가합니다. 자바 클래스패스 풀네임 (확장자도 포함) 으로 지정해야 합니다. 순서가 영향이 있으며, 이미 키가 존재하는 경우 값을 덮어 쓰게 됩니다. */
    public void addAdditionalConfigPath(String classpath) {
        this.additionals.add(classpath);
        readConfigs();
    }
    
    /** 설정 파일 경로를 추가합니다. 실제 파일 정보를 지정합니다. 이미 존재하는 파일이어야 하고, 디렉토리가 아니어야 합니다. */
    public void addAdditionalConfigFile(File file) throws IOException {
        this.additionals.add("file:" + file.getCanonicalPath());
        readConfigs();
    }
    
    /** 직접 호출하지 마세요. 이 라이브러리 내부 동작 및 직렬화를 위해 구현한 메소드입니다. */
    public final boolean isUseYaml() {
		return useYaml();
	}
    
    /** yaml 사용 여부를 반환합니다. */
    public boolean useYaml() {
    	return useYaml;
    }

    /** yaml 파일 사용 여부를 지정합니다. 이 메소드를 호출하지 않으면 true 로 설정된 상태입니다. (단, 런타임 처음 SnakeYaml 라이브러리 존재여부 확인해 미존재 시 false 로 세팅됩니다.) */
	public void setUseYaml(boolean useYaml) {
		this.useYaml = useYaml;
	}

	/** 직접 호출하지 마세요. 이 라이브러리 내부 동작 및 직렬화를 위해 구현한 메소드입니다. */
	public final boolean isFirsts() {
		return firsts;
	}

	/** 직접 호출하지 마세요. 이 라이브러리 내부 동작 및 직렬화를 위해 구현한 메소드입니다. */
	public final void setFirsts(boolean firsts) {
		this.firsts = firsts;
	}

	/** 캐시 데이터를 비웁니다. */
    public void clear() {
        configs.clear();
        readDate = 0L;
    }
    
    /** 이 객체를 초기 상태로 되돌립니다. clear() 메소드 동작을 포함합니다. */
    public synchronized void reset() {
        additionals.clear();
        clear();
        fileName = "config";
    }
    
    /** 데이터들을 java.util.Properties 객체에 담아 반환합니다. */
    protected Properties toProp() {
        Properties prop = new Properties();
        Set<String> keys = keySet();
        for(String k : keys) {
            prop.setProperty(k, getConfig(k));
        }
        return prop;
    }
    
    /** 데이터들을 파일, URL 스트림 등으로 저장/전송합니다. 완료 후, 받은 스트림 객체를 닫지 않습니다. */
    public void save(OutputStream output, boolean xml) throws IOException { 
        Properties prop = toProp();
        
        if(xml) {
            prop.storeToXML(output, "Configs");
        } else {
            prop.store(output, "Configs");
        }
    }
    
    /** 데이터들을 파일로 저장합니다. */
    public void save(File file) {
        FileOutputStream fileOut = null;
        Exception exc = null;
        String name = file.getName().toLowerCase().trim();
        
        Properties prop = toProp();
        
        try {
            fileOut = new FileOutputStream(file);
            if(name.endsWith(".xml")) prop.storeToXML(fileOut, comments);
            else                      prop.store(fileOut, comments);
        } catch(Exception ex) {
            exc = ex;
        } finally {
            if(fileOut != null) { try { fileOut.close(); } catch(Exception closingErr) { throw new RuntimeException(closingErr.getMessage(), exc); } }
        }
        if(exc != null) throw new RuntimeException(exc.getMessage(), exc);
    }

    /** 지정되어 있는 xml 코멘트 값을 반환 (xml 사용 시에만 영향) */
	public String getComments() {
		return comments;
	}

	/** xml 코멘트 값을 지정 (xml로 저장 시에만 영향) */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/** import 키를 변경 (이 키로 설정 값을 넣으면, 해당 값을 추가 설정파일 경로로 보고, 해당 파일로부터 설정을 더 불러옵니다.) */
	public void setImportKey(String importKey) {
		this.importKey = importKey;
	}
	
	/** 리소스를 불러오기 위한 기준 클래스를 변경합니다. */
	public void setBaseClass(Class<?> classObject) {
		this.classBasic = classObject;
	}
}
