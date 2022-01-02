package com.yulin.biz;

import com.google.common.hash.Hashing;
import com.yulin.LinkApplication;
import com.yulin.component.ShortLinkComponent;
import com.yulin.manager.DomainManager;
import com.yulin.manager.ShortLinkManager;
import com.yulin.model.DomainDO;
import com.yulin.model.ShortLinkDO;
import com.yulin.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;

/**
 * @Auther:LinuxTYL
 * @Date:2021/12/25
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class DomainTest {

    @Autowired
    private DomainManager domainManager;

    @Test
    public void testListDomain(){
        List<DomainDO> officialDomain = domainManager.listOfficialDomain();
        log.info(officialDomain.toString());
    }


}
