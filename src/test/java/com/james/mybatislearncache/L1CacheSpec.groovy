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
class L1CacheSpec extends Specification {

    @Autowired
    TransactionTemplate transaction;

    @Autowired
    PersonMapper mapper;

    ExecutorService executor;

    def setup() {
        // RC 隔离级别
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED)
        executor = Executors.newFixedThreadPool(1);
    }

    def cleanup() {
        mapper.deleteAll()
    }

    def "证明 Mybatis 存在一级缓存, 且破坏了RC的事务隔离能力"() {
        given:
        mapper.insert(1, "james")
        def name1
        def name2
        transaction.execute {
            // 当前线程查询
            name1 = mapper.selectNameById(1)

            // 新开线程更新并提交
            executor.execute {
                transaction.execute {
                    mapper.updateNameById("kobe", 1)
                }
            }

            // 确保更新线程提交成功
            Thread.sleep(2000)

            // 当前线程命中缓存并返回, 忽略了更新的值"kobe"
            name2 = mapper.selectNameById(1)
        }
        Thread.sleep(3000)
        expect:
        name1 == "james"
        name2 == "james"
    }

    def "证明 Mybatis 存在一级缓存, 在xml上关闭缓存, 保留RC的事务隔离能力"() {
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

            // 当前线程命中缓存并返回, 忽略了更新的值"kobe"
            name2 = mapper.selectNameByIdWithoutL1Cache(1)
        }
        Thread.sleep(3000)
        expect:
        name1 == "james"
        name2 == "kobe"
    }
}
