package com.bttf.queosk;

import com.bttf.queosk.config.baseEntity.JpaAuditingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(JpaAuditingConfiguration.class)
@SpringBootTest
class QueoskApplicationTests {

    @Test
    void contextLoads() {
    }

}
