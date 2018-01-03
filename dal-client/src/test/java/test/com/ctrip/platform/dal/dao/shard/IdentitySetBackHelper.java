package test.com.ctrip.platform.dal.dao.shard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;

public class IdentitySetBackHelper {
    public static void clearId(List<ClientTestModel> entities) {
        int i = 0;
        for(ClientTestModel model: entities) {
            model.setId(-1);
            model.setAddress("CTRIP" + i++);
        }
    }
    
    public static void assertIdentity(DalTableDao<ClientTestModel> dao, List<ClientTestModel> entities, int shardId) throws SQLException {
        for(ClientTestModel model: entities) {
            assertEquals(dao.queryByPk(model, new DalHints().inShard(shardId)).getAddress(), model.getAddress());    
        }        
    }
    
    public static void assertIdentity(DalTableDao<ClientTestModel> dao, List<ClientTestModel> entities) throws SQLException {
        for(ClientTestModel model: entities) {
            assertEquals(dao.queryByPk(model, new DalHints()).getAddress(), model.getAddress());    
        }        
    }
    
    public static void assertIdentity(DalTableDao<ClientTestModel> dao, DalHints hints, List<ClientTestModel> entities) throws SQLException {
        if(hints.is(DalHintEnum.resultCallback))
            for(ClientTestModel model: entities) {
                assertTrue(model.getId() > 0);    
            }        
        else
            if(hints.isAsyncExecution()){
                for(ClientTestModel model: entities) {
                    dao.queryByPk(model, hints);
                    ClientTestModel p2;
                    try {
                        p2 = hints.getResult();
                        assertEquals(p2.getAddress(), model.getAddress());    
                    } catch (Exception e) {
                        fail();
                    }
                }        
            }else
                for(ClientTestModel model: entities) {
                    assertEquals(dao.queryByPk(model, hints).getAddress(), model.getAddress());    
                }
    }
}
