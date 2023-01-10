package com.james.mybatislearncache

import com.james.mybatislearncache.mapper.PersonMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest(classes = MybatisLearnCacheApplication.class)
class RRL1CacheSpec extends Specification {

    @Autowired
    TransactionTemplate transaction;

    @Autowired
    PersonMapper mapper;

    ExecutorService executor;

    def setup() {
        // RR 隔离级别
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ)
        executor = Executors.newFixedThreadPool(1);
    }

    def cleanup() {
        mapper.deleteAll()
    }

    def "RR隔离级别下, 不需要借助缓存，两次读取是同样的值, RR级别下可以不关闭缓存"() {
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

            // 不需要缓存, 可重复读隔离级别两次读的是一样的
            name2 = mapper.selectNameByIdWithoutL1Cache(1)
        }
        Thread.sleep(3000)
        expect:
        name1 == "james"
        name2 == "james"
    }
}
