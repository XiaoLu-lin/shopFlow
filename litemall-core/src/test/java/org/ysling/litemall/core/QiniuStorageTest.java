package org.ysling.litemall.core;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.ysling.litemall.core.storage.storage.QiniuStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import java.io.IOException;



@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class QiniuStorageTest {

    @Autowired
    private QiniuStorage qiniuStorage;

    @Test
    public void test() throws IOException {

    }

}