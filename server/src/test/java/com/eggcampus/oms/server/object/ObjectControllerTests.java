package com.eggcampus.oms.server.object;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author 黄磊
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ObjectControllerTests {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("生成上传凭证")
    void test_generateUploadToken() {
    }

    @Test
    @DisplayName("使用资源对象")
    void test_useObject() {

    }

    @Test
    @DisplayName("取消使用资源对象")
    void test_cancelUseObject() {

    }


    @Test
    @DisplayName("删除资源对象")
    void test_deleteObject() {

    }

    @Test
    @DisplayName("取消删除资源对象")
    void test_cancelDeleteObject() {

    }

    @Test
    @DisplayName("更改审核状态")
    void test_changeCheckObject() {

    }
}
