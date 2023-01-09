package com.james.mybatislearncache

import com.james.mybatislearncache.mapper.PersonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

@SpringBootTest(classes = MybatisLearnCacheApplication.class)
class L2CacheSpec extends Specification {

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    PersonMapper mapper;

    def setup() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ)
        mapper.insert(1, "james")
        mapper.insert(2, "kobe")
    }

    def cleanup() {
        mapper.deleteAll()
    }

    def "证明存在一级缓存" () {
       given: def id = mapper.selectIdByName("james")
       expect: id == 1
    }
}
