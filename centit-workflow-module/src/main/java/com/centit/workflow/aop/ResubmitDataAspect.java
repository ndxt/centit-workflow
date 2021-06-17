package com.centit.workflow.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.workflow.commons.WorkflowException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @ClassName RequestDataAspect
 * @Description 工作流重复提交校验
 **/
@Aspect
@Component
public class ResubmitDataAspect {
    protected Logger logger = LoggerFactory.getLogger(ResubmitDataAspect.class);

    private final static String DATA = "data";
    private final static Object PRESENT = new Object();

    @Around("@annotation(com.centit.workflow.aop.NoRepeatCommit)")
    public Object handleResubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("spring AOP 提交校验submit 开始");
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息
        NoRepeatCommit annotation = method.getAnnotation(NoRepeatCommit.class);
        int delaySeconds = annotation.delaySeconds();
        Object[] pointArgs = joinPoint.getArgs();
        String key = "";
        //获取第一个参数
        Object firstParam = pointArgs[0];
        //解析参数
        JSONObject data = (JSONObject) JSON.toJSON(firstParam);

        if (data != null) {
            StringBuffer sb = new StringBuffer();
            data.forEach((k, v) -> {
                if (v != null) {
                    sb.append(v);
                }
            });
            //生成加密参数 使用了content_MD5的加密方式
            key = ResubmitLock.handleKey(sb.toString());
        }
        //执行锁
        boolean lock = false;
        try {
            //设置解锁key
            logger.info("spring AOP key: {}", key);
            lock = ResubmitLock.getInstance().lock(key, PRESENT);
            if (lock) {
                logger.info("spring AOP 提交校验submit -> 放行");
                //放行
                return joinPoint.proceed();
            } else {
                logger.info("spring AOP 提交校验submit -> 重复提交");
                //响应重复提交异常
                throw new WorkflowException("spring AOP 提交校验submit：重复提交");
            }
        } finally {
            //设置解锁key和解锁时间
            ResubmitLock.getInstance().unLock(lock, key, delaySeconds);
            logger.info("spring AOP 提交校验submit -> 解锁");
        }
    }
}
