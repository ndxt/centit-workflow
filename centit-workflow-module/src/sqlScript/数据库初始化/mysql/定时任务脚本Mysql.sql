--mysql5.6


CREATE EVENT WF_CONSUME_LIFE
ON SCHEDULE EVERY 5 MINUTE  -- ÿ�����ִ��һ��
DO call consumeLifeTime() ;  


SHOW VARIABLES LIKE 'event_scheduler'; -- �鿴��ʱ�������Ƿ���
set GLOBAL event_scheduler = ON; -- ������ʱ������

drop event if exists WF_CONSUME_LIFE --ɾ����ʱ��
select * from db.migration.mysql.event ; -�鿴��ʱ������
��ALTER EVENT WF_CONSUME_LIFE enABLE;--��ͣ��ʱ��
��ALTER EVENT WF_CONSUME_LIFE DISABLE; -��ʼ��ʱ��

