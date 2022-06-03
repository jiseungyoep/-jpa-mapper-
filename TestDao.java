package test.dao;

import java.util.HashMap;
import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovimj.domain.schedule.DmailVO;
import egovimj.domain.schedule.QDmailVO;
import egovimj.util.db.DCI;
import egovimj.util.db.ServerAwareNamingStrategy;


/*
  테스트용으로 만든 dao
*/
@Repository("dmailDao")
@Lazy
public class DmailDao extends EgovAbstractMapper {
	

	SqlSession session;
	DCI dci = new DCI();
	
	@Transactional(readOnly = true)
	public List<DmailVO> selectDmailList(@Param("tableName")String tableName, HashMap<String, String>dbMap ) throws Exception{
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("tableName", tableName);
		dci.setDataTable(dbMap);
		//jpa, querydsl
		ServerAwareNamingStrategy sa = new ServerAwareNamingStrategy();
		sa.setTableName(tableName);
		EntityManagerFactory emf1 = dci.entityManagerFactory();
		EntityManager em = emf1.createEntityManager();
		QTestVO qvo = QTestVO.TestVO;
		JPQLQueryFactory qf = new JPAQueryFactory(em);
		List<DmailVO> list = qf.selectFrom(qvo)
							   .fetch();
		
		System.out.println(list);
		em.close();
		emf1.close();
		//jpa, querydsl

    //mybatis 테스트 부분
		session = dci.getInstance();
		System.out.println(session);
		List<DmailVO> selectDmailList = session.selectList("selectDmailList", map);
		session.close();
    //mybaits 테스트 부분
		return selectDmailList;
	}

}
