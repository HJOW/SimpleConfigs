package org.duckdns.hjow.util.simpleconfig;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/** 설정 정보를 담은 인스턴스들 중 정보를 파일 등으로 출력이 가능한 인스턴스들은 이 인터페이스의 구현체입니다. */
public interface SaveableInstance extends ConfigInstance {
    /** 데이터들을 파일, URL 스트림 등으로 저장/전송합니다. 완료 후, 받은 스트림 객체를 닫지 않습니다. */
    public void save(OutputStream output, boolean xml) throws IOException;
    
    /** 데이터들을 파일로 저장합니다. */
    public void save(File file);
}
