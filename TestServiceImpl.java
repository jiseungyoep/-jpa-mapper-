package test.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import test.dao.TestDao;
import test.domain.TestVO;

@Service("testService")
public class TestServiceImpl extends EgovAbstractServiceImpl {
	
	private DmailDao dd = new DmailDao();
	
	public List<TestVO> selectList(@Param("tablename")String tableName, HashMap<String, String> dbMap) throws Exception {
		return dd.selectDmailList(tableName, dbMap);

	}

}
