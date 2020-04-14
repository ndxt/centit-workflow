package com.centit.workflow.test;

import com.alibaba.fastjson.JSON;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.service.impl.FlowVariableTranslate;

import java.util.Map;

public class TestFormula {
    public static void main(String[] args) {
        FlowVariableTranslate varTrans = new FlowVariableTranslate(
            null, new FlowInstance());
        Map<String, Object> retMap = varTrans.calcScript("a:if(isEmpty(a),1,a+1)");
        System.out.println(JSON.toJSONString(retMap));
        retMap = varTrans.calcScript(" a, _b, c, , d : 1,a+2, _b+a, toString(today())");
        System.out.println(JSON.toJSONString(retMap));
    }
}
