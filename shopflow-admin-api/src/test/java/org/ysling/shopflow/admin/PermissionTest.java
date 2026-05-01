package org.ysling.shopflow.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ysling.shopflow.admin.annotation.entity.Permission;
import org.ysling.shopflow.admin.annotation.handler.PermissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.ysling.shopflow.admin.annotation.entity.PermVo;

import java.util.List;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PermissionTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        final String basicPackage = "org.ysling.shopflow.admin";
        List<Permission> permissionList = PermissionHandler.listPermission(context, basicPackage);
        List<PermVo> permVoList = PermissionHandler.listPermVo(permissionList);
        permVoList.stream().forEach(System.out::println);
    }
}
