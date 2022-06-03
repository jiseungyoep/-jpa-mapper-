package 사용하실 패키지;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.ibatis.common.resources.Resources;


/* 	
  @param  : dataTable - db 정보들 담아서 받아오는 변수
 	@method : envDBConn() - 변수에 저장된 값 가지고 새로운 dataSource를 만드는 메서드
 	@method : getInstance() - 해당 경로에 있는 mapper-config.xml 파일에 envDBConn에서 새롭게 만든
 							  DataSource가지고 새로운 session을 열어주는 메서드
 							  ※메서드안 자세한 변수 설명은 메서드위에 주석 참고
 */
@Lazy
@Configuration
public class DCI  {
	private ConfigReader confReader = new ConfigReader();
	private int maxActive;
	private Long maxWait;
	private HashMap<String, String> dataTable = new HashMap<String, String>();
	 
	public DCI() {}

	//config 파일 읽어올려고 세팅한것 
	public void loadConfig() {
		Properties prop = confReader.load("database.config");
		String value = "";
		value = prop.getProperty("db.extern.max.connection");
		try {
			maxActive = Integer.parseInt(value);
		}catch(Exception e) {
			maxActive = 10;
		}
		value = prop.getProperty("db.extern.max.idle.timeout");
		try {
			maxWait = Long.parseLong(value)*1000;
		}catch(Exception e) {
			maxWait = 180000L;
		}
	}
	
	//데이터 소스
	//bean에 dataSource같은 애라고 보면 됨.
	/*
	 * https://urakasumi.tistory.com/162 
	 * 커넥션 풀 관리 정리 해둔 사이트
	*/
	@Bean(name="secondDataSource", destroyMethod = "close")
	public BasicDataSource envDBConn() {
		BasicDataSource bds = new BasicDataSource();
		loadConfig();
		bds.setDriverClassName(dataTable.get("driver"));
		bds.setUrl(dataTable.get("url"));
		bds.setUsername(dataTable.get("user"));
		bds.setPassword(dataTable.get("password"));
		//동시에 사용할 수 있는 커넥션 수
		bds.setMaxActive(maxActive);
		//pool 고갈되었을때 기다리는 시간
		bds.setMaxWait(maxWait);
		return bds;
	}

	//mapper-config.xml에서 새로운 db가지고 커넥션 해주는 애
	/*
	  @param resource = mapper-config 경로 지정하는 하는 애
	  					dmail 커스텀마이징으로 다른 쿼리 날리거나 하고 싶으면 
	  					따로 경로를 외부로 빼서 지정받게 만들어도 좋을 것 같음
	  @param reader = 저 경로에 있는 resouces 파일 읽어 들이는 애
	  @param tran = JDBC 트랜지션 관리해주는 애 이 친구가 있어야 env 설정이 가능해서 따로 새로 생성해줌
	  @param env = mybatis-config에 새로운 db 관련 정보를 넣어주는 애 위에서 "사용할 id", 트랜지션, 
	  			   dataSource 순으로 받아씀
	  @param parser = reader로 읽어온 resouces 파일을 읽어서 XMLConfigBuilder로 대상을 지정
	  @param conf = mybatis에 settings에 해당하는 부분을 읽어오고 그 해당하는 부분에 설정값을 
	  			    추가할 수 있는 옵션을 넣어주면 됨
	  @param ssf = SqlSessionFactory 자바랑 db랑 연결해주는 변수(conf)를 넣어서 새로 추가되는 
	  			   db를 연결 시켜줌
	  @param session = 실직적인 db랑 매퍼랑 연결해주는 변수 SqlSessionFactory.openSession()
	  				   이라는 매서드로 연결 시켜줌				   
	  참고 사이트 : https://okky.kr/article/1048258?note=2520718
	*/
	public SqlSession getInstance() throws IOException  { 
		//이걸 외부 경로로 주시면 외부에서 세팅만 하시면 mapper 연결 
		String resource = "resource/sqlmap/dbconfig/env-mapper-config.xml";
		Reader reader = null;
		SqlSession session = null;
			reader = Resources.getResourceAsReader(resource);
			SqlSessionFactoryBuilder sfb = new SqlSessionFactoryBuilder();
			TransactionFactory tran = new JdbcTransactionFactory();
			Environment env = new Environment("env", tran, envDBConn());
			XMLConfigBuilder parser = new XMLConfigBuilder(reader);
			org.apache.ibatis.session.Configuration conf = parser.parse();
			conf.setEnvironment(env);
			
			SqlSessionFactory ssf = sfb.build(conf);
			session = ssf.openSession();
			reader.close();
			return session;	
	}
	/*
	  UI단 환경설정 database설정에서 연결로 체크할려고 만든 메서드
	*/
	public int connectionChecker() throws Exception {
		SqlSession session = getInstance();
		int check = 0;
		if(session != null) {
			check = 1;
		}else {
			check =0;
		}
		session.close();
		session = null;
		
		return check;
	}

	
	/*
	  env db 관련 jpa 세팅
	  EntityManagerFactroy
	  LocalContarinerEntityManagerFactroyBean ; : Entitymanager를 
	  Bean 에다가 등록해주는 애
	*/
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(hibernateJpaVendorAdapter);
		factory.setJpaProperties(jpapropProperties());
		factory.setPersistenceUnitName("test");
		factory.setPackagesToScan("base.*");
		factory.setDataSource(envDBConn());
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	/*
	 jpa 하이버에션 설정부분
	*/
	private Properties jpapropProperties() {
		Properties prop = new Properties();
		Properties dialect = confReader.load("dialect.config");
		//각 db별 dialect 값들 		
		String dialectKey = "";
		if(dataTable.get("vendor").equalsIgnoreCase("mysql")) {
			dialectKey = "mysql";
		}else if(dataTable.get("vendor").equalsIgnoreCase("mssql")) {
			dialectKey = "mssql";
		}else if(dataTable.get("vendor").equalsIgnoreCase("oracle")) {
			dialectKey = "oracle";
		}else if(dataTable.get("vendor").equalsIgnoreCase("cubrid")) {
			dialectKey = "cubrid";
		}else if(dataTable.get("vendor").equalsIgnoreCase("tibero")) {
			dialectKey = "tibero";
		}else if(dataTable.get("vendor").equalsIgnoreCase("postgre")) {
			dialectKey = "postgre";
		}else if(dataTable.get("vendor").equals("extDB1")) {
			dialectKey = "extdb1";
		}else if(dataTable.get("vendor").equals("extDB2")) {
			dialectKey = "extdb2";
		}else{
			System.out.println("해당 db에 관한 dialect가 없습니다");
		}
		//db 방언설정
		prop.put("hibernate.dialect", dialect.getProperty(dialectKey));
		//콘솔창 sql 설정
		prop.put("hibernate.show_sql", dialect.get("showSQL"));
		//데이터베이스 스키마 자동 생성
		prop.put("hibernate.hdm2ddl.auto","none");
		//하이버네이트 물리적 명명전략 ex) @table 변경할때 쓰는거
		prop.put("hibernate.physical_naming_strategy", "egovimj.util.db.ServerAwareNamingStrategy");
		return prop;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}
	
	public HashMap<String, String> getDataTable() {
		return dataTable;
	}

	public void setDataTable(HashMap<String, String> dataTable) {
		this.dataTable = dataTable;
	}
}
