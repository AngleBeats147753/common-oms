package com.eggcampus.oms.client.springboot.manager;

import com.eggcampus.oms.api.pojo.dto.UploadTokenDTO;
import com.eggcampus.oms.client.springboot.EnableOms;
import com.eggcampus.oms.client.springboot.OmsManager;
import com.eggcampus.oms.client.springboot.TestMain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 黄磊
 */
//@SpringBootApplication
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {OmsAutoConfiguration.class, AutoConfigurationImportSelector.class})
@EnableOms
@SpringBootTest(classes = TestMain.class)
public class OmsManagerTests {
    //    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private OmsFeignManager omsFeignManager;
    @Autowired
    private OmsManager omsManager;

    @Test
    public void test() {
        UploadTokenDTO test2 = omsManager.generateUploadToken("test4");
        System.out.println(test2);
//        UploadTokenGenerationQuery query = new UploadTokenGenerationQuery(new ApplicationDTO("hl", "dev"), "test");
//        System.out.println(omsFeignManager.generateUploadToken(query));
    }
}
