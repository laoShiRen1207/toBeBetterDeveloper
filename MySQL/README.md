# MySQL 学习

查看版本

```sql
select version();

-- 8.0.28
```

查看表数据硬盘情况

```sql
SELECT
	TABLE_NAME '表名',
	concat( TRUNCATE ( data_length / 1024 / 1024, 0 ), ' MB' ) AS '数据',
	concat( TRUNCATE ( index_length / 1024 / 1024, 2 ), ' MB' ) AS '索引' 
FROM
	information_schema.TABLES 
WHERE
	TABLE_SCHEMA = 'laoshiren' 
GROUP BY
	TABLE_NAME 
ORDER BY
	data_length DESC;
```

创建表

```sql
CREATE TABLE `user_operation_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NULL DEFAULT NULL,
  `ip` varchar(20)  NULL DEFAULT NULL,
  `op_data` varchar(255)  NULL DEFAULT NULL,
  `attr1` varchar(255)  NULL DEFAULT NULL,
  `attr2` varchar(255)  NULL DEFAULT NULL,
  `attr3` varchar(255)  NULL DEFAULT NULL,
  `attr4` varchar(255)  NULL DEFAULT NULL,
  `attr5` varchar(255)  NULL DEFAULT NULL,
  `attr6` varchar(255)  NULL DEFAULT NULL,
  `attr7` varchar(255)  NULL DEFAULT NULL,
  `attr8` varchar(255)  NULL DEFAULT NULL,
  `attr9` varchar(255)  NULL DEFAULT NULL,
  `attr10` varchar(255)  NULL DEFAULT NULL,
  `attr11` varchar(255)  NULL DEFAULT NULL,
  `attr12` varchar(255)  NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 

```

1000w 数据准备

```sql
CREATE PROCEDURE batch_insert_log()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE userId INT DEFAULT 10000000;
 set @execSql = 'INSERT INTO `user_operation_log`(`user_id`, `ip`, `op_data`, `attr1`, `attr2`, `attr3`, `attr4`, `attr5`, `attr6`, `attr7`, `attr8`, `attr9`, `attr10`, `attr11`, `attr12`) VALUES';
 set @execData = '';
  WHILE i<=10000000 DO
   set @attr = "'测试很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的属性'";
  set @execData = concat(@execData, "(", userId + i, ", '127.0.0.1', '用户登录操作'", ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ",", @attr, ")");
  if i % 1000 = 0
  then
     set @stmtSql = concat(@execSql, @execData,";");
    prepare stmt from @stmtSql;
    execute stmt;
    DEALLOCATE prepare stmt;
    commit;
    set @execData = "";
   else
     set @execData = concat(@execData, ",");
   end if;
  SET i=i+1;
  END WHILE;
END;;
```

```sql

create table emp
(
    id          int comment '编号',
    workno      varchar(10) comment '工号',
    name        varchar(10) comment '姓名',
    gender      char(1) comment '性别',
    age         tinyint unsigned comment '年龄',
    idcard      char(18) comment '身份证号',
    workaddress varchar(50) comment '工作地址',
    entrydate   date comment '入职时间'
) comment '员工表';


```

```sql
insert into emp(id, workno, name, gender, age, idcard, workaddress, entrydate)
VALUES (1, '1', '柳岩', '女', 20, '123456789012345678', '北京', '2000-01-01'),
       (2, '2', '张无忌', '男', 18, '123456789012345670', '北京', '2005-09-01'),
       (3, '3', '韦一笑', '女', 38, '123456789012345670', '上海', '2005-08-01'),
       (4, '4', '赵敏', '女', 18, '123456789012345670', '北京', '2009-12-01'),
       (5, '5', '小昭', '女', 16, '123456789012345678', '上海', '2007-07-01'),
       (6, '6', '杨逍', '男', 28, '12345678901234567X', '北京', '2006-01-01'),
       (7, '7', '范瑶', '男', 40, '123456789012345670', '北京', '2005-05-01'),
       (8, '8', '黛绮丝', '女', 38, '123456789012345670', '天津', '2015-05-01'),
       (9, '9', '范凉凉', '女', 45, '123456789012345678', '北京', '2010-04-01'),
       (10, '10', '陈友谅', '男', 53, '123456789012345670', '上海', '2011-01-01'),
       (11, '11', '张士诚', '男', 55, '123456789012345670', '江苏', '2015-05-01'),
       (12, '12', '常遇春', '男', 32, '123456789012345670', '北京', '2004-02-01'),
       (13, '13', '张三丰', '男', 88, '123456789012345678', '江苏', '2020-11-01'),
       (14, '14', '灭绝', '女', 65, '123456789012345670', '西安', '2019-05-01'),
       (15, '15', '胡青牛', '男', 70, '12345678901234567X', '西安', '2018-04-01'),
       (16, '16', '周芷若', '女', 18, null, '北京', '2012-06-01'); 
```

## 1.存储引擎

### 1.1 MySQL 体系结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/b15aad174b2c4eb09f2d3a287b381c2f.png)

连接层：主要完成客户端的连接处理，授权，检查连接数。

服务层：绝大部分的核心功能都是在服务层完成，sql接口，解析器，优化器，缓存。

引擎层：可插拔式的存储引擎。索引实在存储引擎层实现的。

存储层：日志，数据，索引等。

### 1.2 存储引擎简介

什么是存储引擎？

> 存储引擎就是存储数据、建立索引、更新/查询数据等技术的实现方式。存储引擎是基于表的，而不是基于库的，所以存储引擎也被称作表类型。

查询建表语句

```sql
show create table user_operation_log;

CREATE TABLE `user_operation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `op_data` varchar(255) DEFAULT NULL,
  `attr1` varchar(255) DEFAULT NULL,
  `attr2` varchar(255) DEFAULT NULL,
  `attr3` varchar(255) DEFAULT NULL,
  `attr4` varchar(255) DEFAULT NULL,
  `attr5` varchar(255) DEFAULT NULL,
  `attr6` varchar(255) DEFAULT NULL,
  `attr7` varchar(255) DEFAULT NULL,
  `attr8` varchar(255) DEFAULT NULL,
  `attr9` varchar(255) DEFAULT NULL,
  `attr10` varchar(255) DEFAULT NULL,
  `attr11` varchar(255) DEFAULT NULL,
  `attr12` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2184001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```

建表语句没有指定引擎，使用默认的存储引擎`InnoDB`(从MySQL5.5之后就是InnoDB)

查看当前数据库支持的存储引擎

```sql
show engines;
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/ac0db00c4f2f45bfbc2b82cd9cc2a541.png)

指定存储引擎 MyISAM

```sql
create table my_myisam(
id int,
    name varchar(10)
) engine = MyISAM;
```

### 1.3 存储引擎特点

#### InnoDB

介绍

> InnoDB 是一种兼顾高可靠性和高性能的通用存储引擎，在MySQL 5.5 之后，InnoDB 是默认的MySQL 引擎。

特点

> DML 操作遵循 ACID 模型，支持**事务**。
>
> **行级锁**，提高并发访问性能。
>
> 支持 **外键** FOREIGN KEY 约束，保证数据的完整性和正确性。

文件

> xxx.ibd：xxx表示表名，InnoDB 引擎的每张表都会对应一个这样的表空间文件，存储该表的表结构（frm（8.0之后表结构存储到了sdi）、sdi）、数据和索引。
>
> 参数：innodb_file_per_table  8.0版本这个参数是打开的，每一张表有自己的表空间

![在这里插入图片描述](https://img-blog.csdnimg.cn/9c74b3b534504f82b0008d3b828c97c7.png)



![在这里插入图片描述](https://img-blog.csdnimg.cn/1f1bab1f4d4c482c8ddae030497599e0.png)

在InnoDB 逻辑存储结构当中，Page 是磁盘操作的最小单元，一个Extent的大小是固定的（1M），一个Page （16k）

#### MyISAM

介绍

> MyISAM是MySQL早期的默认存储引擎

特点

> 不支持事物，不支持外键
>
> 只是表锁，不支持行锁
>
> 访问速度快

文件

> xxx.sdi: 存储表结构信息
>
> xxx.MYD: 存储表数据
>
> xxx.MYI: 存储索引

![在这里插入图片描述](https://img-blog.csdnimg.cn/d410ca54b3e34c5abe8a68fabfefbb58.png)

#### Memory

介绍

> Memory 引擎的表数据是存放在内存中的，由于受到硬件问题或断点问题的影响，只能将这些表作为临时表或者缓存使用。

特点

> 内存存放
>
> hash索引(默认)

文件

> xxx.sdi: 存储表结构信息



|     特点     |      InnoDB       | MyISAM | Memory |
| :----------: | :---------------: | :----: | :----: |
|   存储限制   |       64TB        |   有   |   有   |
|   事务安全   |       支持        |   -    |   -    |
|    锁机制    |       行锁        |  表锁  |  表锁  |
|  B+Tree索引  |       支持        |  支持  |  支持  |
|   Hash索引   |         -         |   -    |  支持  |
|   全文索引   | 支持(5.6版本之后) |  支持  |   -    |
|   空间使用   |        高         |   低   |  N/A   |
|   内存使用   |        高         |   低   |  中等  |
| 批量插入速度 |        低         |   高   |   高   |
|   支持外键   |       支持        |   -    |   -    |

### 1.4 存储引擎选择

在选择存储引擎时，应该根据应用系统的特点选择合适的存储引擎。对于复杂的应用系统，还可以更具实际情况选择多种引擎进行组合。

> InnoDB ：是MySQL 的默认引擎，支持事务外键，对事务完整性要求比较高，要求一定并发条件下数据一致性，除查询和插入外还包括更新删除操作，InnoDB 比较适合
>
> MyISAM ：以读操作插入操作为主少有更新和删除操作，并对事务的完整性，并发要求不是很高，MyISAM 比较适合
>
> Memory 访问速度快，对于大数据表无法缓存在内存中，而且无法保证数据安全性。

在实际业务开发过程中MyISAM 被MongoDB 取代了，Memory 被Redis 取代了。

## 2 索引

### 2.1 索引概述

> 索引（index）是帮助MySQL **高效获取数据的有序数据结构**。在数据之外，数据库系统还维护着满足特定查找算法的数据结构，这些数据结构以某种方式引用（指向）数据，这样就可以在这些数据结构上实现高级查找算法，这种数据结构就是索引。

![在这里插入图片描述](https://img-blog.csdnimg.cn/4edfb01bba244156910d57f13a2f9f77.png)

优缺点：

|                             优点                             |                             缺点                             |
| :----: | :----: |
|           提高数据库检索的效率，降低数据库的IO成本           |                   索引列也是需要占用空间的                   |
| 通过索引列对数据进行排序，降低数据排序的成本，降低CPU的消耗。 | 索引大大提高了查询效率，同时也降低更新表的速度，如果对表进行INSERT，UPDATE，DELETE时，效率降低 |

### 2.2 索引结构

MySQL 的索引实在存储引擎层实现的，不同的存储引擎有不同的结构，主要包含以下几种：

|      索引结构       |                             描述                             |
| :-----------------: | :----------------------------------------------------------: |
|     B+Tree索引      |          最常见的索引类型，大部分引擎都支持B+树索引          |
|      Hash索引       | 底层数据结构是hash表实现的，只有精确匹配索引列的查询才有效，不支持范围查询 |
|  R-Tree(空间索引)   | 空间索引是MyISAM 引擎的一个特殊索引类型，主要用于地理空间数据类型，通常使用较少 |
| Full-Text(全文索引) | 是一种通过建立倒排索引，快速匹配文档的方式。类似于Lucene，Solr，ES |

支持情况

|    索引     |     InnoDB      | MyISAM | Memory |
| :---------: | :-------------: | :----: | :----: |
| B+Tree 索引 |      支持       |  支持  |  支持  |
|  Hash 索引  |     不支持      | 不支持 |  支持  |
| R-Tree 索引 |     不支持      |  支持  | 不支持 |
|  Full-text  | 5.6版本之后支持 |  支持  | 不支持 |

平时所说的索引，没有特别指明，都是B+Tree结构的索引。

#### BTree

二叉树

![在这里插入图片描述](https://img-blog.csdnimg.cn/df6f2aa9e69749c4b0b61de45648fab2.png)

二叉树的缺点：顺序插入式，会形成一个链表，查询性能大大降低。大数量情况下，层级较深，索引速度慢。

红黑树

![在这里插入图片描述](https://img-blog.csdnimg.cn/0b4d34a7d2ee429ea78a50f1fd541865.png)

红黑树的缺点：大数量情况下，层级较深，检索速度慢。

B-Tree(多路平衡查找树)

以一颗最大度数（max-degree）为5的B-Tree为例（每个节点最多寸4个Key,5个指针）

> 树的度数指的是一个节点的子节点个数

![在这里插入图片描述](https://img-blog.csdnimg.cn/41209fd07cf0461bb2ab008c28d426eb.png)

[Data Structure Visualizations  链接: https://www.cs.usfca.edu/~galles/visualization/Algorithms.html ](https://www.cs.usfca.edu/~galles/visualization/Algorithms.html)

插入满Max-degree的节点会进行分裂，中间元素向上分裂

#### B+Tree

特点:

* 所有的元素都在叶子节点，非叶子节点只起到了索引的作用

* 在B+Tree数据结构中叶子节点，会形成一个单项链表

以一颗最大度数（max-degree）为4的B+Tree为例

![在这里插入图片描述](https://img-blog.csdnimg.cn/527368b16cec4a4492eec11ffae5f673.png)

插入满Max-degree的节点会进行分裂，中间元素向上分裂，在叶子节点也会存放，而且会形成一个单项列表。

在MySQL索引数据结构对经典B+Tree进行了优化。在原B+Tree的基础上，增加一个指向相邻叶子节点的链表指针，就形成了带有顺序指针的B+Tree，提高区间访问的性能。

![在这里插入图片描述](https://img-blog.csdnimg.cn/3a6eee5db434486aa68708bff76fbf4b.png)

#### Hash

哈希索引就是采用一定的hash 算法，将键值换算成新的hash 值，映射到对应的槽位上，然后存储在hash 表中。

如果两个（或多个）键值，映射到一个相同槽位上，他们就产生了hash 冲突（也称为hash 碰撞），可以通过链表来解决。

![在这里插入图片描述](https://img-blog.csdnimg.cn/9383f36985a947e1ae150856a61a21b9.png)

特点：

* hash索引只能用于对等比较（=，in）,不支持范围查询（between，>，<，......）
* 无法利用索引完成排序操作
* 查询效率高，通常只要一次索引就可以了，效率通常要高于B+Tree 索引

在MySQL 中，支持hash 索引的事Memory 引擎，而在InnoDB 中具有自适应hash 功能，hash索引是存储引擎根据B+Tree 索引在指定条件下自动构建的。



为什么InnoDB 存储引擎选择B+Tree 索引结构？

> 相对于二叉树，层级更少，搜索效率高
>
> 对于B-Tree ，无论是叶子节点还是非叶子节点，都会保存数据，这样导致一页存储的键值减少，指针耕者减少，同样保存大量数据，只能增加树的高度，导致性能降低
>
> 对于Hash 索引 B+Tree 支持范围匹配及排序操作

### 2.3 索引分类

|   分类   |                         含义                         |           特点           |  关键字  |
| :------: | :--------------------------------------------------: | :----------------------: | :------: |
| 主键索引 |                 针对于表中主键的索引                 | 默认自动创建，只能有一个 | PRIMARY  |
| 唯一索引 |           避免同一个表中某数据列中的值重复           |        可以有多个        |  UNIQUE  |
| 常规索引 |                   可以定位特定数据                   |        可以有多个        |          |
| 全文索引 | 全文索引查找的是文本中的关键字，而不是比较索引中的值 |        可以有多个        | FULLTEXT |

在InnoDB存储引擎中，根据索引的形式，又可以分为以下两种：

|            分类             |                           含义                           |         特点         |
| :-------------------------: | :------------------------------------------------------: | :------------------: |
| 聚集索引（Clustered Index） | 将数据存储与索引放到了一块，索引结构的子节点保存了行数据 | 必须有，而且只有一个 |
| 二级索引（Secondary Index） | 将数据与索引分开存储，索引结构的子节点关联的是对应的主键 |     可以存在多个     |

聚集索引选取规则：

如果存在主键，主键索引就是聚集索引。

如果不存在主键，将使用第一个唯一索引（UNIQUE）作为聚集索引。

如果没有主键，或者没有合适的唯一索引，则InnoDB 会自动生成一个rowid 作为隐藏的聚集索引。

![在这里插入图片描述](https://img-blog.csdnimg.cn/698651c6c2a94e5c84e877b84b97f570.png)

`select * from user where name = 'Arm';`

首先走二级索引，查找到Arm，然后拿到主键索引，然后通过聚集索引拿到行数据，这个操作也叫回表。

> InnoDB指针占用6个字节控件，主键为bigint，占用字节数为8

### 2.4 索引语法

**创建索引**

```sql
CREATE [UNIQUE|FULLTEXT] INDEX index_name ON table_name (col0,col1,...);
```

**查看索引**

```sql
SHOW INDEX FROM table_name;
```

**删除索引**

```sql
DROP INDEX index_name ON table_name;
```

### 2.5 SQL 性能分析

#### 查看执行频次

MySQL 客户端连接成功后，通过`show [session|global] status` 命令可以提供服务器状态信息，通过如下指令可以查看数据的INSERT，UPDATE，DELETE，SELECT 的访问频次   

```sql
SHOW GLOBAL STATUS LIKE 'Com_______';
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/e3fb80d22be34eb694edc78a1586e20a.png)

#### 慢查询日志

慢查询日志记录了所有执行时间超过指定参数（long_query_time，单位：秒，默认10秒）的所有SQL 语句的日志。

MySQL 的慢查询日志默认没有开启，需要在MySQL 的配置文件（/etc/my.cnf）中配置如下信息：

```cnf
# 开启慢查询日志开关
slow_query_log=1
# 设置慢查询日志时间为2秒
long_query_time=2
```

配置完毕之后，重启MySQL 服务器，查看慢日志文件中的记录信息 `/var/lib/mysql/localhost-slow.log`。

```shell
# Time 2022-03-05T15:45:39.688679Z
# User@Host: root[root] @ locahost [] Id: 8
# Query_time: 13.350650 Lock_time 0.000358 Rows_sent: 1 Rows_examined: 0

use laoshiren;
SET timestamp=1635435926;
select count(1) from user_operation_log;
```

#### show profiles

profile详情

`show profile`能够在做SQL优化是帮助我们了解时间都消耗到哪里去了，通过have_profiling 参数，能够看到当前MySQL 是否支持profile操作

```sql
select @@have_profiling;
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/edde7490ff8b45c984b7621de2eeb00e.png)

默认profiling 是关闭的 （`select @@profiling`），可以通过set 语句在session/global 级别开启profiling:

```sql
SET profiling = 1;
```

执行一系列的业务SQL的操作，然后通过如下指令查看SQL 的执行耗时

```sql
-- 查看每一条SQL的耗时情况
show profiles;

-- 查看指定Query_id的SQL语句各个阶段的耗时情况
show profile for query query_id;

-- 查看query_id的SQL cpu使用情况
show profile cpu for query query_id;
```

执行了3条SQL

```sql
SELECT * from user_operation_log where id = '456465';

select * from  user_operation_log where user_id = '15646';

select * from user_operation_log LIMIT 10000, 10;
```



![在这里插入图片描述](https://img-blog.csdnimg.cn/feb06621572b4498886df1922b8a2ddf.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/a13d01bcb8a842169e72682415691e07.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/55f858cf2aea4f8f87edbcea0ca883e3.png)

####  explain

**explain 执行计划**

EXPLAIN 或者 DESC 命令获取MySQL 如何语句的信息，包括在SELECT 语句执行过程中表如何连接和连接顺序

```sql
-- 在SQL语句前加上关键字 EXPLAIN/DESC
EXPLAIN SELECT col0,clo1,... from table_name WHERE condition;
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/54fb8b7b886749c6924fee874bcdc11c.png)

|      列       |       含义        |
| :----: | :-----: |
|      id       | select 查询的序号，表示查询中的执行select 子句或者操作表的顺序（id相同，顺序从上到下，id 不同值越大越先执行） |
|  select_type  | 表示 SELECT 的类型，常见的取值有 SIMPLE （简单表，不使用表连接和子查询），PRIMIAY（主查询，即外层的查询），UNION ，SUBQUERY （select/where 之后包含了子查询）等 |
|     table     |  |
|  partitions   |                   |
|     **type**      | 表示连接的类型，性能由好到差的连接类型为 NULL，system，const，eq_ref，ref，range，index，all |
| **possible_keys** | 可能用到的索引 |
|      **key**      | 实际用到的索引 |
|    **key_len**    | 表示索引使用的字节数 |
|      ref      |                   |
|     rows      | 查询预估值 |
|   filtered    | 返回结果占读取行数的百分比，越大越好 |
|     Extra     | 额外信息 |

### 2.6 索引使用

#### 验证索引效率

未建立索引的列`user_id`作为条件查询时，耗时143s。

```sql
select * from user_operation_log where user_id = '10000010';
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/0b2c8570fbcb4ce8a48ad1963ae9c8a4.png)

当存在索引时查询耗时0.26s

```sql
select * from user_operation_log where id = 13
```

对于字段创建索引

```
create index index_user_id on user_operation_log(user_id);
```

创建索引就相当于去构建B+Tree的数据结构，需要一定耗时。

![在这里插入图片描述](https://img-blog.csdnimg.cn/0ce695f62b8d4bb7b398bd82519ce5cd.png)



再次查询发现只要0.419s![在这里插入图片描述](https://img-blog.csdnimg.cn/331362f6d542482b80d991d9df19fc84.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/8fec22ea4e1c425396eb76617ec708f1.png)

#### 最左前缀法则

如果索引了多列（联合索引），需要村收最左前缀法则，最左前缀发法则指的是查询从索引的最左列开始，并不跳过索引中的列。如果跳跃某一列，索引将部分失效（后面的字段索引失效）。

![在这里插入图片描述](https://img-blog.csdnimg.cn/9bd9a7071e094050b31aaccc9b35f5b6.png)

首先对这3个字段创建索引，来测试最左前缀发展。

```sql
explain 
select * from emp where 1=1
and name = '韦一笑' 
and gender = '女' 
and  workaddress = '上海'
```

当按照顺序且不跳过字段查询是可以使用索引，即使是少了几个字段也是可以使用的（缺少后面字段）。

当跳过第一个字段（根据`gender`和`workaddress`）查询时，发现是无法使用索引的

![在这里插入图片描述](https://img-blog.csdnimg.cn/3683910830b84f859605b43712e16b82.png)

前文说了，跳跃了某一列，部分索引失效，观察索引长度，即只使用了跳跃列前面的索引长度。

```sql
select * from emp where 1=1
and gender = '女' 
and workaddress = '上海'
and name = '韦一笑' 
```

当查询为如上`sql`时也会走索引

##### 范围查询

在联合索引中，出现范围查询(><)，范围查询右侧的列索引失效。

![在这里插入图片描述](https://img-blog.csdnimg.cn/c93436050a6d4ea281ef2678c24cd463.png)

**当不得不出现范围查询，且业务允许的时候，大于可以替换为大于等于。**

#### 索引失效

##### 索引列运算

不要再索引列上进行运算操作，索引将会失效。

![在这里插入图片描述](https://img-blog.csdnimg.cn/5693929457cb4c94bf7ae5f69434fafe.png)

##### 字符串不加引号

字符串类型的字段使用时，不加引号，索引将失效

![在这里插入图片描述](https://img-blog.csdnimg.cn/22725425d90449af87427fd91e9b5c1f.png)

##### 模糊查询

如果仅仅是尾部模糊匹配，索引不会失效，如果是头部匹配，索引失效。

##### or链接的条件

用`or` 分割开的条件，如果`or` 前的条件有所用而后面列没有索引，那么涉及的索引都不会被用到。

![在这里插入图片描述](https://img-blog.csdnimg.cn/d33366cac76346fa852d93c4f4a31670.png)

##### 数据分布影响

如果MySQL 评估使用索引比全表更慢，则不使用索引。

![在这里插入图片描述](https://img-blog.csdnimg.cn/49a4cce585ab44458390031a2ebd00c6.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/dbfbc889637249bbaee17772cb33e5d0.png)

`is null` 和`is not null `是否使用索引取决于MySQL 表的数据分布情况。

#### SQL 提示

SQL提示是优化数据库的一个重要手段，简单来说，在SQL语句种加入一些人为的提示来达到优化操作的目的。比如： 一个联合索引（name,id_card,status）和普通索引（name）到底用哪个索引呢？

##### use index

```sql
-- 使用某个索引
explain select * from emp use index(idx_name_gen_add) where name = '韦一笑'
```

##### ignore index

```sql
-- 忽略某个索引
explain select * from emp ignore index(idx_name_gen_add) where name = '韦一笑'
```

##### force index

```sql
-- 强制使用某个索引
explain select * from emp force index(idx_name_gen_add) where name = '韦一笑'
```


#### 覆盖索引&回表查询

尽量使用覆盖索引（查询使用了索引，并且需要返回的列，在该索引已经全部能够找到），减少`select * `。

在`Explain SQL`时关注Extra 列出现`using index condition`或者`NULL` 说明查找使用了索引，但是需要回表查询数据。

`using where;using index`说明查找使用了索引，但是需要的数据都在索引列种能够找到，所以不需要回表查询。 

当查询的字段不在索引种能够找到，从而查询聚集索引拿到数据，这样的操作称作回表。

![在这里插入图片描述](https://img-blog.csdnimg.cn/deaa720aeddd4c09aafe329969f8a3e8.png)

#### 前缀索引

表结构当中经常会出现文本类型的字段，当字段类型为字符串（varchar,text）时，有时候需要索引很长的字符串，这会让索引变得很大，查询时浪费大量的磁盘IO，影响查询效率，此时可以将字符串的一部分前缀建立索引，这样可以大大节约索引空间，从而提高索引效率。

```sql
create index idx_xxx on table (column(N));
```

**前缀长度**

可以根据索引的选择性来决定，而选择性是指不重复的索引值（基数）和表记录的总数的比值，索引选择性越高，查询效率越高。唯一索引的选择性是1，这是做好的索引选择性，性能也是最好的。

```sql
-- 查询截取前N个字符的不重复的记录 / 所有记录树 为该字段的选择性
SELECT count(DISTINCT(substring(NAME, 1, N ))) / count(*) FROM TABLE
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/8876a85413514db9ad70f8e55ab7c25d.png)

#### 单列&联合索引

单列索引：即一个索引只包括一个字段。

联合索引：即一个索引包括多个字段。

在业务场景种，如果存在多个查询条件，考虑针对于查询字段建立索引是，建议建立联合索引，而非单列索引。多条件联合查询，MySQL 优化器会评估那个字段的所有效率更好，会选择该索引完成本次查询。

### 2.7 设计原则

1. 针对于数据量比较大，查询比较频繁的表
2. 针对于常作为查询条件，排序，分组操作的字段建立索引。
3. 尽量选择分区度高的列作为索引，尽量做唯一索引，区分度越高，效率越高。
4. 如果是字符串且字符串长度较长，可以针对字段的特点，建立前缀索引。
5. 尽量使用联合索引，减少单列索引，查询时联合索引喝多时候可以覆盖索引，节省存储空间避免回表。
6. 索引越多，维护表结构的代价越大，影响增删改的效率。
7. 如果所有列不能为空时，创建表时使用`NOT NULL`约束，当优化器知道每列包含`NULL` 值时，可以更好的确定哪个索引最有效地用于查询。

## 3 SQL优化

### 3.1 插入数据

#### 批量操作

2-1000条

```sql
insert into emp (1, '1', '柳岩', '女', 20, '123456789012345678', '北京', '2000-01-01'),
       (2, '2', '张无忌', '男', 18, '123456789012345670', '北京', '2005-09-01');
```

手动提交事务

```sql
start transaction;
insert ....
commit;
```

如果一次性涉及大批量数据，insert 语句性能较低，此时可以使用MySQL 的load 指令进行插入

```shell
# 客户端连接服务器时，加上参数，--local-infile
mysql --local-infile -uroot -p

# 设置全局参数local-infile 为1 开启从本地加载文件导入数据的开关
set global local_infile = 1;

# 执行load 指令将准备好的数据，加载到表结构当中
load data local file '/home/xxx.log' into table 'xxx' fields terminated by ',' lines terminated '\n';
```

### 3.2 主键优化

在InnoDB 存储引擎中，表数据都是根据主键顺序组织存放的，这种存储方式的表称为索引组织表（Index Organized Table IOT）。

![在这里插入图片描述](https://img-blog.csdnimg.cn/1f1bab1f4d4c482c8ddae030497599e0.png)

一个Page 默认是16K ，一个Extent 是固定的，是1M。一个Page 是InnoDB 磁盘管理的最小单元。

#### 页分裂

页可以为空，也可以填充一半，也可以填充满。每个页包含了2-N行数据（如果一行数据过大，会行溢出），根据主键排序。

![在这里插入图片描述](https://img-blog.csdnimg.cn/0121abb0031e4416aed791cf6eab0020.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/571d2b173d9d447c8c1ab65f6ccb9ad9.png)

首先会新获取一个page, 将page 页的50%之后的数据，迁移到新开辟的页面，就新数据50插入到新页面，然后重新设置页的表指针。这个操作成为页分裂。

#### 页合并

当删除一行记录时，实际上记录并没有被物理删除，而是被标记（flaged）为删除并且它的空间变得被其他记录声明使用。当页中删除的记录达到MERGE_THREADSHOLD(默认页的50%)，InnoDB 会开始寻找最靠近的页看看是否可以将2个页合并优化空间使用。

![在这里插入图片描述](https://img-blog.csdnimg.cn/358e489b152743578ef6e944277a2c5c.png)

MERGE_THREADSHOLD：合并页的阈值，可以自己设置，在创建表或者创建索引时指定。

#### 主键设计原则

* 满足业务需求的情况下，尽量降低主键的长度（二级索引存储聚集索引的值）。
* 插入数据时，尽量选择顺序插入，选择AUTO_INCREMENT 自增主键。
* 尽量不要使用UUID 做主键或者是其他自然主键。
* 业务操作时，避免对主键的修改。

### 3.3 order by 优化

* using filesort : 通过表的索引或者扫描权标，读取满足条件的数据行，然后在排序缓冲区sort buffer 中完成排序操作，所有不是通过索引直接返回排序结果的排序都是 FileSort 排序。
* using index : 通过有序索引顺序扫描直接返回有序数据，这种情况即为 using index，不需要额外排序，操作效率高。

将`emp`中的`idcard`索引删除。

```sql
-- 删除索引
DROP INDEX idx_id_card ON emp；

EXPLAIN select  *  FROM `emp` order by idcard；


explain select id, workno, age,idcard  from emp order by workno desc, age desc ,idcard desc;

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/24be6a4b98264ba28a4cdde6b15d5e77.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/5b61a7d2351e45439e3f8a33a56018e0.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/be783aeb6f4343588fd155125413e8b8.png)

然而根据最左原则，排序字段应该也要跟着索引指定的字段顺序进行排序

![在这里插入图片描述](https://img-blog.csdnimg.cn/67209c8abe554ef5b22a8388bd496cbd.png)

当select 字段不出现在索引中，需要回表操作，此时，缓冲区会用filesort 进行排序。

**小结**

* 根据排序字段建立合适的索引，多字段排序时，也遵循最左前缀法则。
* 尽量使用覆盖索引。
* 多字段排序，一个升序，一个降序，此时需要注意联合索引在创建时的规则（ASC/DESC ）。
* 如果不可避免的出现filesort，大数据量排序，可适当增大排序缓冲区大小 sort_buffer_size（默认256k）。

### 3.4 group by 优化

先删除表上其他索引

```sql
drop index xxx on TABLE;

explain select workaddress,count(1) from emp group by  workaddress
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2c16b1190de04b2ab33950ec5635765f.png)

`Using temporary`使用临时表性能非常低。

```sql
-- 创建一个联合索引
create index idx_add_gender on emp(workaddress,gender);

explain select workaddress,count(1) from emp group by  workaddress
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/e805ec86d63d411d89c2bec86aecd332.png)

当group by 不满足最左前缀法则，则用不到索引，先会使用临时表。

![在这里插入图片描述](https://img-blog.csdnimg.cn/9c1e1ca845a840609ece72553b5fbda4.png)

当查询涉及如上的查询，在查询条件添加where 条件且刚好满足最左原则则查询可以使用索引。

![在这里插入图片描述](https://img-blog.csdnimg.cn/5033bfc9073c47ae8bc44b3a6ce9fc3f.png)

分组操作时，索引的适合用也是尽可能满足最左前缀法则。

### 3.5 limit 优化

`user_operation_log`这张表有1000w的数据。

```sql
select * from user_operation_log limit 1000000,10
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/417ec01da5f74328a9926f28b7fce7c0.png)

时长显然是不可接受的。在对于limit 来说，大数据量的情况下，越往后效率越低，耗时越长。MySQL需要排序前N 条记录，仅仅返回N-(N+n)条记录，其他记录设计，查询排序的代价非常大。

官方推荐使用覆盖索引加子查询的方式。

![在这里插入图片描述](https://img-blog.csdnimg.cn/b9f8d65ce4664e3c80b235a135fac181.png)

MySQL 不支持此类语法 ~~`select * from user_operation_log where id in (select id from user_operation_log order by id limit 1000000,10)`~~，8.0的版本不支持在 in 条件的子查询里使用`limit`，所以要修改写法。

```sql
select * from user_operation_log u,(select id from user_operation_log order by id limit 1000000,10) a where u.id = a.id
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/7ffe0112af4046409abe8fc1942d78c5.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/62e5907050104fdf9cdbacfaa111da6b.png)

### 3.6 count 优化

MyISAM 引擎把一个表的总行数存在了磁盘上，因此执行`count(*)`的时候会直接返回这个数，效率很高。而在InnoDB 引擎就麻烦了，他执行`count(*)`的时候，需要把数据一行一行地从引擎里读，然后累积计数。

#### count

`count()`是一个聚合函数，对于返回的结果集，一行行地判断，如果count() 函数的参数不是`NULL `就累加1，否则不加，最后返回累计值。

InnoDB 在`count(主键)`时，会遍历整张表，把每一行的主键ID的值都取出来，返回给服务层。服务层拿到主键后，直接进行累加（主键不可能为`null`）。

count(1)，遍历整张表，不取值，对于返回的每一层，放一个数字1，进行累加。

count(*)，InnoDB引擎不会把全部字段取出来，而是专门做了优化，直接按行进行累加。

按照效率 count(字段)< count(pk)< count(1)≈count(*)

### 3.7 update 优化

update 时候尽量使用ID作为条件更新，不然update 会因为没有索引，从而将行锁升级为表锁。InnoDB 的行锁只针对索引加的锁，不是针对记录加的锁，并且索引不能失效，否则会从行锁升级为表锁。

## 4 锁

### 4.1 概述

锁是计算机协调多个进程或线程并发访问某一资源的机制。在数据库种，除传统的计算机资源（CPU，RAM，I/O）的争用以外，数据也是一种提供多用户共享的资源。如何保证数据并发访问的一致性，有效性是所有数据库必须解决的一个问题，锁冲突也是影响数据库并发访问性能的一个重要因素。从这个角度来说，锁对数据库而言显得尤其重要，也更复杂。

按照锁的粒度来分，MySQL中的锁分为以下三类

* 全局锁：锁定数据库中的所有表。
* 表级锁：每次操作锁住整张表
* 行级锁：每次操作锁住对于的行数据。

### 4.2 全局锁

对整个数据库实例枷锁，枷锁后整个实例就处于制度状态，后续的DML的写语句，DDL语句，以及更新操作的事务提交语句都将被阻塞。通常用于备份数据`mysqldump`。

### 4.3 表级锁

表级锁，每次操作锁住整张表。锁粒度打，发视锁冲突的概率高，并发度最低，应用在MyISAM，InnoDB等存储引擎中。

对于表级锁，主要分三类

1. 表锁
2. 元数据锁
3. 意向锁

#### 表锁

表锁分为2类

1. 表共享读锁（read lock）
2. 表独占写锁（write lock）

加锁：

```sql
lock tables TABLE read/write
```

释放锁：

```sql
unlock tables 
```

##### 读锁

![在这里插入图片描述](https://img-blog.csdnimg.cn/bb1a4b876e1d4ea08beb31f5296da858.png)

客户端1在表上加锁，客户端1和其他客户端都只能够查询，不能够写。当客户端1去写入数据会直接提示 `Table 'xxx' was locked with a READ lock and can't be updated`，当其他客户端写入数据时，会直接阻塞，直到客户端1解锁。

![在这里插入图片描述](https://img-blog.csdnimg.cn/13b790738eab4959848053e685c65176.png)

##### 写锁

![在这里插入图片描述](https://img-blog.csdnimg.cn/a575cbf6f7984b49a9e5e39c5b339942.png)

客户端1在表上加锁，只有客户端1能够读写。当其他客户端读写数据时，都会直接阻塞，直到客户端1解锁。

![在这里插入图片描述](https://img-blog.csdnimg.cn/08150db6677b46deb08f534a68897730.png)

读锁不会阻塞其他客户端的读，但是会阻塞写。写锁既会阻塞其他客户端的读，又会阻塞其他客户端的写。

#### 元数据锁

元数据锁（meta data lock ), MDL 加锁过程是系统自动控制，无需显示使用，在访问一张表的时候会自动加上。MDL锁主要作用是维护表元数据的数据一致性，在表上有活动事务的时候，不可以对元数据进行写入操作。**为了避免DML和DDL冲突，保证读写的正确性**。

在MySQL 5.5 中引用了MDL，当对一张表进行增删改查的时候，加MDL读锁（共享）；当表结构进行变更操作的时候，加上MDL写锁（排他）。

|                   对应SQL                    |                锁类型                 |                       说明                       |
| :------------------------------------------: | :-----------------------------------: | :----------------------------------------------: |
|          lock tables xxx read/write          | SHARED_READ_ONLY/SHARED_NO_READ_WRITE |                                                  |
|     select ,select... lock in share mode     |              SHARED_READ              | 与SHARED_READ，SHARED_WRITE兼容。与EXCLUSIVE互斥 |
| insert ,update, delete, select... for update |             SHARED_WRITE              | 与SHARED_READ，SHARED_WRITE兼容。与EXCLUSIVE互斥 |
|                alter table...                |               EXCLUSIVE               |                与其他的MDL都互斥                 |

```sql
select object_type,object_schema,object_name,lock_type,lock_duration from performace_schema.metadata_locks;
```

当开启一个事务的时候不会产生元数据锁，只有增删查改，修改表结构的时候才会看到自动加锁。

#### 意向锁

为了避免DML 在执行时，加的行锁与表锁的冲突，在InnoDB 中应如何了意向锁，使得表锁不用检查每行数据是否加锁，使用意向锁来减少表锁的检查。

![在这里插入图片描述](https://img-blog.csdnimg.cn/0a5b229250ba4a9b909ab534e320cec2.png)

不用逐行检查行锁，而是检查意向锁的情况，如果意向锁和当前加的锁是兼容的，如果兼容直接加锁，如果不兼容就会处于阻塞状态，知道A线程提交事务，解锁意向锁。

![在这里插入图片描述](https://img-blog.csdnimg.cn/e384c9d8172344e1abc19ebd4ebb7285.png)



|               意向共享锁（IS）               |                      意向拍打锁（IX）                      |
| :------------------------------------------: | :--------------------------------------------------------: |
|  由语句select ... lock in share mode添加。   |   由 insert，update，delete，select ... for update添加。   |
| 与表锁共享锁read 兼容，与表锁排他write锁互斥 | 与表锁共享锁read ，表锁排他write锁互斥。意向锁之间不会互斥 |
|                                              |                                                            |

通过以下SQL 查看意向所以及行锁的加锁情况。

```sql
select object_schema,object_name,index_name,lock_type,lock_mode,lock_data from performance_schema.data_locks;
```

意向锁主要解决的问题是在InnoDB 引擎在加表锁和行锁的冲突问题。

### 4.4 行级锁

行级锁，每次操作锁住对应的行数据。锁的力度小，发生锁冲突的概率低，并发度最高，应用在InnoDB 存储引擎中。

InnoDB 的数据是基于索引组织的。行锁是通过对表锁上的索引项加锁实现的，而不是对记录加的锁。对于行级锁，主要分一下三类：

* 行锁（Record Lock）：锁定单个记录的锁，防止其他事务对此进行update 和 delete。在RC、RR 隔离级别下都支持。
* 间隙锁（Gap Lock）：锁定索引记录间隙（不含该记录），确保索引间隙不变，防止其他事务在这个间隙进行insert ，产生幻读。在RR 隔离级别下都支持。
* 临间锁（Next-key Lock）：行锁和间隙锁的组合，同时锁住数据，并锁住数据前面的间隙Gap 。在RR 隔离级别下支持。
* ![在这里插入图片描述](https://img-blog.csdnimg.cn/140070ee86d7430ba3649fe1fc418926.png)

#### 行锁

InnoDB 实现了一下两种类型的行锁：

* 共享锁（S）：允许一个事务读取一行，阻止其他事物获得相同数据集的排他锁。
* 排他锁（X）：允许获取排他锁的事务更新数据，阻止其他事物相同数据集的共享锁和排他锁。

|          | S 共享锁 | X 排他锁 |
| :------: | :------: | :------: |
| S 共享锁 |   兼容   |   冲突   |
| X 排他锁 |   冲突   |   冲突   |

|            SQL            | 行锁类型 |             说明             |
| :-----------------------: | :------: | :--------------------------: |
|         INSERT...         |   排他   |           自动加锁           |
|         UPDATE...         |   排他   |           自动加锁           |
|         DELETE...         |   排他   |           自动加锁           |
|          SELECT           |  不加锁  |                              |
| SELECT LOCK IN SHARE MODE |   共享   | 需要手动加LOCK IN SHARE MODE |
|     SELECT FOR UPDATE     |   排他   |    需要手动加 FOR UPDATE     |

默认情况下，InnoDB 在RR 事物隔离级运行 InnoDB 使用 Next-key Lock 锁进行搜索和索引扫描防止幻读。

1.  针对唯一所有检索时，对已存在的记录进行等值匹配时，将会自动优化为行锁。
2. InnoDB 的行锁是针对于索引加的锁，不通过索引检索数据，那么InnoDB 将对表中所有的记录加锁，此时就会升级为表锁。

#### 间隙锁/临键锁

* 索引上的等值查询（唯一索引）,给不存在的记录加锁，优化为间隙锁。
* 索引上的等值查询（普通查询），向右遍历时最后一个值不满足查询需求时，next-key lock 退化为间隙锁。
* 索引上的范围查询（唯一索引）会访问到不满足条件的第一个值位置。

**注意：间隙锁唯一的目的是防止其他事物插入间隙，间隙锁可以共存，一个事务采用的间隙所不会阻塞另一个间隙上采用的间隙锁。**

## 5 InnoDB 引擎

InnoDB 是MySQL5.5 版本之后的默认的存储引擎。

### 5.1 逻辑存储结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/7cb0c00ecaca4043ac877bce74192f83.png)

 表空间（ibd文件），一个MySQL 实例可以对应多个表空间，用于存储记录、索引等数据。

段（Segment），分为数据段（Leaf node segment）、索引段（Non-leaf node segment）、回滚段（Rollbak segment）。InnoDB 是索引组织表，数据段就是B+tree的叶子节点，索引段即为B+Tree的非叶子节点。段用来管理多个Extent。

区（Extent）表空间的单元结构，每个区的大小为1M，默认情况下InnoDB 存储引擎页大小为16k，即一个区中一共有64个连续的页。

页（Page），是InnoDB 存储引擎磁盘管理的最小单元，每个页默认大小为16k。为了保证页的连续性，InnoDB 存储引擎每次从磁盘申请4-5个区。

行（Row），InnoDB 存储引擎数据是按行存放的。

> Trx_id: 每次对某条记录进行改动时，都会把对应的事务id 赋值给trx_id 隐藏列
>
> Rool_pointer：每次对某条记录进行改动时，都会把旧的版本写入到undo日志中，然后这个隐藏列相当于一个指针，可以通过它来找到该记录的修改前的信息。

### 5.2 架构

MySQL5.5 版本开始，默认使用InnoDB 存储引擎，它擅长事务处理，具有崩溃恢复特性，在日常开发中使用非常广泛，下面是InnoDB 架构图，左侧为内存结构，右侧为磁盘结构。 

![在这里插入图片描述\](https://img-blog.csdnimg.cn/7cb0c00ecaca4043ac877bce74192f83.png](https://img-blog.csdnimg.cn/2283e74ec8204095b88a9436281d2339.png)

#### 内存结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/f3108f3468e2427abf81e0cffee424f3.png)

##### Buffer Pool

缓冲池是主内存中的一个区域，里面可以缓存磁盘上进场操作的真实数据，在执行增删改查操作时，先操作缓冲池的数据（若缓冲池没有数据，则从磁盘加载并缓存），然后再以一定频率刷新到磁盘，从而减少磁盘IO，加快处理速度。

缓冲池有一个一个的块，叫做缓冲池。缓冲池以Page页为单位，底层采用链表数据结构管理Page。根据状态Page 被分为3类：

* free page：空闲page，未被使用。
* clean page：被使用page，数据没有被修改过。
* dirty page：脏页，被使用page，数据被修改过，数据与磁盘的数据产生了不一致

##### Change Buffer

更改缓冲区（针对于非一二级所以页），在执行DML 语句时，如果这些数据Page 没有在Buffer Pool 中，不会直接操作磁盘，而会将数据变更存在缓冲区Change Buffer 中，在未来数据被读取时，再将数据合并恢复到Buffer Pool 中，再讲合并后的数据刷新到磁盘中。

![在这里插入图片描述](https://img-blog.csdnimg.cn/646c6d7df7044d03a75eeecb89023bdd.png)

**Change Buffer的意义**

与聚集索引不同，二级索引通常是非唯一的，并且以相对随机的顺序插入二级索引。同样，删除和更新可能会影响数中不相邻的二级索引页。如果每一次都操作磁盘，会造成大量磁盘IO，有了Change Buffer 之后，我们可以在缓冲池进行合并处理，减少磁盘IO。

##### Adaptive Hash Index

hash 索引最大优势在于快，因为他只需要一次匹配就可以完成（前提是不存在hash冲突的情况下），B+Tree 往往需要2-3次。但是他的弊端是不能够支持范围查询，只能做等值匹配。所以InnoDB 引擎就做了这个自适应hash 。

自适应hash 索引，用于优化对Buffer Pool 数据查询。InnoDB 存储引擎会监控对表上各索引的查询，如果观察到hash 索引可以提高速度，则建立hash 索引，称之为自适应hash 索引。

**自适应hash 索引，无须人工干预，是系统根据情况自动完成。**

参数：adaptive_hash_index

##### Log Buffer 

日志缓冲区，用来保存要写入到磁盘中的log日志数据（redo log，undo log），默认大小是16MB，日志缓冲区的日志会定期刷新到磁盘中。如果需要更新、插入删除许多行的事务，增加日志缓冲区的大小可以节省磁盘I/O。

参数：innodb_log_buffer_size（缓冲区大小），innodb_flush_log_at_trx_commit(日志刷新到磁盘的时机)

刷新时机默认是1,1 日志在每次事务提交时写入并刷新到磁盘，0 每秒将日志写入并刷新到磁盘一次， 2 日志在每次事务提交后写入并每秒刷新到磁盘一次。

#### 磁盘结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/a512f74dbdf7476191ccfd031c1dc2a1.png)

##### System Tablespace

系统表空间是更新缓冲区存储的区域。如果表示在系统表空间而不是每个表文件或者通用表空间中创建的，它也可能包含表和索引的数据（在MySQL5.x版本中还包含InnoDB数据字典、undolog 等）

参数：innodb_data_file_path

##### File-Per-Table Tablespaces

每个表的文件表空间包含单个InnoDB 表的数据和索引，并存储在文件系统上的单个数据文件中。

参数：innodb_file_per_table

```sql
-- 创建表空间
create tablespace xxx add datafile 'xxx.ibd' engine = innodb;
```





#### 后台线程







### 5.3 事务原理

#### redolog



#### undolog





### 5.4 MVCC

#### 基本概念





#### 隐藏字段



#### undolog 版本链





#### readview 介绍







#### 原理分析（RC）







#### 原理分析（RR）