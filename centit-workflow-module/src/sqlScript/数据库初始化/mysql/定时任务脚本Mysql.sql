--mysql5.6


CREATE EVENT WF_CONSUME_LIFE
ON SCHEDULE EVERY 5 MINUTE  -- 每五分钟执行一次
DO call consumeLifeTime() ;  


SHOW VARIABLES LIKE 'event_scheduler'; -- 查看定时器功能是否开启
set GLOBAL event_scheduler = ON; -- 开启定时器功能

drop event if exists WF_CONSUME_LIFE --删除定时器
select * from mysql.event ; -查看定时器详情
　ALTER EVENT WF_CONSUME_LIFE enABLE;--暂停定时器
　ALTER EVENT WF_CONSUME_LIFE DISABLE; -开始定时器

