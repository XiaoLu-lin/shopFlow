package org.ysling.litemall.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisTest {

    private final Log logger = LogFactory.getLog(RedisTest.class);

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    private static final String SESSION_PREFIX = "TENANT_DECODE:";

    @Test
    public void test() {
//        Set<Object> keys = redisTemplate.keys(SESSION_PREFIX + "*");
//        System.out.println(keys);
//
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(SESSION_PREFIX + "*");
//        System.out.println(entries);

//        Set<Object> keys = redisTemplate.keys(SESSION_PREFIX + "*");
//        HashSet<Session> sessions = new HashSet<>();
//        if (keys != null){
//            List<Object> list = redisTemplate.opsForValue().multiGet(keys);
//            if (list != null) {
//                for (Object key :list) {
//                    sessions.add((Session) key);
//                }
//            }
//        }
//
//        System.out.println(sessions);

        Object o1 = redisTemplate.opsForHash().get(SESSION_PREFIX, "6e7be58c649ebbe5");
//        Object o = redisTemplate.opsForValue().get(SESSION_PREFIX + "6e7be58c649ebbe5");
        System.out.println(o1);
    }
}
