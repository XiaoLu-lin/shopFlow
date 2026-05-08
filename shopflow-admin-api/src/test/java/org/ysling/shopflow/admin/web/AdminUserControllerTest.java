package org.ysling.shopflow.admin.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.ysling.shopflow.admin.model.user.result.UserBatchDeleteResult;
import org.ysling.shopflow.admin.service.AdminUserService;
import org.ysling.shopflow.core.utils.response.ResponseUtil;
import org.ysling.shopflow.db.domain.ShopflowUser;
import org.ysling.shopflow.db.entity.IdsBody;
import org.ysling.shopflow.db.enums.UserStatus;

public class AdminUserControllerTest {

    @Test
    @SuppressWarnings("unchecked")
    public void batchDeleteShouldAllowPartialSuccess() {
        FakeAdminUserService userService = new FakeAdminUserService();
        AdminUserController controller = new AdminUserController();
        ReflectionTestUtils.setField(controller, "userService", userService);

        ShopflowUser firstUser = new ShopflowUser();
        firstUser.setId("1001");
        firstUser.setStatus(UserStatus.STATUS_NORMAL.getStatus());

        ShopflowUser secondUser = new ShopflowUser();
        secondUser.setId("1002");
        secondUser.setStatus(UserStatus.STATUS_NORMAL.getStatus());

        ShopflowUser outUser = new ShopflowUser();
        outUser.setId("1003");
        outUser.setStatus(UserStatus.STATUS_OUT.getStatus());

        userService.putUser(firstUser, 0);
        userService.putUser(secondUser, 1);
        userService.putUser(outUser, 1);

        IdsBody body = new IdsBody();
        body.setIds(Arrays.asList("1001", "1002", "1003", "1004"));

        ResponseUtil<UserBatchDeleteResult> response =
                (ResponseUtil<UserBatchDeleteResult>) controller.batchDelete(body);

        Assertions.assertEquals("success", response.getErrno());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(Integer.valueOf(1), response.getData().getSuccessCount());
        Assertions.assertEquals(Integer.valueOf(3), response.getData().getFailCount());
        Assertions.assertEquals(UserStatus.STATUS_OUT.getStatus(), firstUser.getStatus());
        Assertions.assertEquals(UserStatus.STATUS_OUT.getStatus(), secondUser.getStatus());
        Assertions.assertEquals(Arrays.asList("1001", "1002"), userService.getUpdatedIds());
    }

    private static class FakeAdminUserService extends AdminUserService {

        private final Map<String, ShopflowUser> users = new HashMap<>();
        private final Map<String, Integer> updateResults = new HashMap<>();
        private final List<String> updatedIds = new ArrayList<>();

        void putUser(ShopflowUser user, int updateResult) {
            users.put(user.getId(), user);
            updateResults.put(user.getId(), updateResult);
        }

        List<String> getUpdatedIds() {
            return updatedIds;
        }

        @Override
        public ShopflowUser findById(Serializable id) {
            return users.get(String.valueOf(id));
        }

        @Override
        public int updateVersionSelective(ShopflowUser record) {
            updatedIds.add(record.getId());
            return updateResults.getOrDefault(record.getId(), 0);
        }
    }
}
