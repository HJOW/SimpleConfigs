package org.duckdns.hjow.util.simpleconfig;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Properties;

/** org.yaml.snakeyaml.Yaml 을 Reflexion 으로 접근해 사용 */
public class YamlReflexionUtil {
	/** Map 객체를 읽어 yaml 형식 문자열을 만들어 반환 */
    public static String toYaml(Map<String, Object> map) {
    	if(map == null) throw new NullPointerException();
    	try {
    		Class<?> classYaml = Class.forName("org.yaml.snakeyaml.Yaml");
        	Object   instances = classYaml.newInstance();
        	Method   methods   = classYaml.getMethod("dump", Object.class);
        	return (String) methods.invoke(instances, map);
    	} catch(ClassNotFoundException e) {
    		throw new RuntimeException(e.getMessage(), e);
    	} catch (InstantiationException e) {
    		throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (SecurityException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }
    
    /** yaml 형식 문자열을 읽어 Map 을 만듦 */
    @SuppressWarnings("unchecked")
	public static Map<String, ?> fromYaml(String yaml) {
    	if(yaml == null) throw new NullPointerException();
    	try {
    		Class<?> classYaml = Class.forName("org.yaml.snakeyaml.Yaml");
    		Class<?> classCons = Class.forName("org.yaml.snakeyaml.constructor.Constructor");
    		
    		Constructor<?> consCons = classCons.getConstructor(Object.class);
    		Object instCons = consCons.newInstance(HashMap.class);
    		
    		Constructor<?> consYaml = classYaml.getConstructor(classCons);
    		Object instYaml = consYaml.newInstance(instCons);
    		
    		Method methodLoad = classYaml.getMethod("load", String.class);
    		return (Map<String, ?>) methodLoad.invoke(instYaml, yaml);
    	} catch(ClassNotFoundException e) {
    		throw new RuntimeException(e.getMessage(), e);
    	} catch (SecurityException e) {
    		throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
    }
    
    /** yaml 사용 가능 여부를 반환 */
	public static boolean isYamlSupport() {
		try {
		    fromYaml("{s2: '456', s1: '123'}");
		    return true;
		} catch(Exception ex) {
			return false;
		}
	}
    
    /** yaml 형식 문자열을 스트림으로부터 읽어 Map 을 만듦 */
	public static Map<String, ?> fromYaml(InputStream inp, String charset) {
    	return fromYaml(readString(inp, charset));
    }
    
    /** 문자열로부터 yaml 내용을 읽어 Properties 객체에 내용 입력 */
    public static void load(Properties prop, String inp) {
    	Map<String, ?> map = fromYaml(inp);
    	Set<String> keys = map.keySet();
    	
    	for(String k : keys) {
    		Object obj = map.get(k);
    		if(obj == null) obj = "";
    		prop.put(k, obj.toString());
    	}
    }
    
    /** InputStream 으로부터 문자열을 읽어 반환. 완료 후 스트림이 닫힐 수도 있음. */
    public static String readString(InputStream inp, String charset) {
    	InputStreamReader inp2 = null;
    	BufferedReader    inp3 = null;
    	Exception causes = null;
    	
    	StringBuilder res = new StringBuilder("");
    	try {
    		inp2 = new InputStreamReader(inp, charset);
    		inp3 = new BufferedReader(inp2);
    		
    		String r;
    		while(true) {
    			r = inp3.readLine();
    			if(r == null) break;
    			res = res.append("\n").append(r);
    		}
    		
    		inp3.close(); inp3 = null;
    		inp2.close(); inp2 = null;
    	} catch(Exception ex) {
    		causes = ex;
    	} finally {
    		if(inp3 != null) { try { inp3.close(); inp3 = null; } catch(IOException ignores) {} }
    		if(inp2 != null) { try { inp2.close(); inp2 = null; } catch(IOException ignores) {} }
    	}
    	if(causes != null) throw new RuntimeException(causes.getMessage(), causes);
    	return res.toString().trim();
    }
}
