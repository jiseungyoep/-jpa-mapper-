package egovimj.util.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*
 * @Param baseDir : 환경 변수로 잡혀있는 baseDir IM_BASE_DIR 경로 가져오는 변수
 * @Param prop : 파일을 읽어서 자바에서 사용할려고 세팅
 * @Param folderPath : baseDir+/config 경로 지정 변수
 * @Param filename : 읽을 파일 이름 받아옴
 * @Param pathname : 파일경로랑 이름을 합쳐서 FileReader에게 던져줄려고 만든 변수
 * @Param FileReader : 외부 경로에 있는 파일 읽어오는 변수
 * @Method load : 파일을 읽어서 프로퍼티스로 prop으로 반환해줌
 * 사용법
 * 	ConfigReader configReader = new ConfigReader();
	Properties prop2 = configReader.load("system.config");
	String key = "tracer.server.port";
	String value = prop2.getProperty(key);
	System.out.println("tracer.server.port =========> "+" "+ value);
 
*/
public class ConfigReader {
	private static final Properties prop = new Properties();
	private static final String LogName = "ConfigReader";
	private static final String ClassName = "CONFIGREADER";
	private String baseDir = System.getenv("BaseDir");
	private String folderPath = baseDir+"\\config";
	
	
	public Properties load(String filename) {
		String pathname = folderPath+"\\"+filename;
			FileReader fr = new FileReader(pathname);
			prop.load(fr);
			fr.close();
			return prop;
	}
}
