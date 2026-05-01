package org.ysling.shopflow.core;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.ysling.shopflow.core.storage.storage.AliyunStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import java.io.IOException;


@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class AliyunStorageTest {


    @Autowired
    private AliyunStorage aliyunStorage;

    @Test
    public void test() throws IOException {

    }

}