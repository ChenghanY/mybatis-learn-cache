package com.james.mybatislearncache

import com.james.mybatislearncache.mapper.AnotherPersonMapper
import com.james.mybatislearncache.mapper.PersonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest(classes = MybatisLearnCacheApplication.class)
class RRL2CacheSpec extends Specification {

    @Autowired
    TransactionTemplate transaction;

    @Autowired
    PersonMapper mapper;

    @Autowired
    AnotherPersonMapper anotherMapper;

    ExecutorService executor;

    def setup() {
        // RC 隔离级别
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ)
        executor = Executors.newFixedThreadPool(1);
    }

    def cleanup() {
        mapper.deleteAll()
    }

    def "RR环境下, 缓存不影响结果"() {
        given:
        mapper.insert(1, "james")
        def name1
        def name2
        transaction.execute {
            // 当前线程查询
            name1 = mapper.selectNameByIdWithoutL1Cache(1)

            // 新开线程更新并提交
            executor.execute {
                transaction.execute {
                    mapper.updateNameById("kobe", 1)
                }
            }

            // 确保更新线程提交成功
            Thread.sleep(2000)

            // 使用不同的mapper保证不命中二级缓存
            name2 = anotherMapper.selectNameById(1)
        }
        Thread.sleep(3000)

        expect:
        name1 == "james"
        // RC 下这个结果未 "kobe"
        name2 == "james"
    }
}
