package com.centit.workflow.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.centit.support.json.JSONOpt;
import com.centit.workflow.po.UserTask;

public class TestUserTask {
    public static void main(String[] args) {
        String jsonStr = "{\"flowInstId\":\"2c657c70ae3147fa9e45d2a4f8c0ecf9\",\"flowCode\":\"9GcBFl95RTqUxG0UTbG-5w\",\"version\":1,\"flowOptName\":\"test\",\"nodeInstId\":\"8fb95ab36ea74a888d105339560ff267\",\"unitCode\":\"Dv9dzwjen1ts\",\"userCode\":\"U93ije5mccnp\",\"roleType\":\"RO\",\"roleCode\":\"Rz5mspsk\",\"authDesc\":\"引擎分配\",\"nodeName\":\"人工节点\",\"nodeType\":\"C\",\"nodeOptType\":\"A\",\"createTime\":\"2023-07-19 11:54:16.000\",\"promiseTime\":0,\"timeLimit\":0,\"optCode\":\"NO\",\"lastUpdateUser\":\"U8d223e0\",\"instState\":\"N\",\"optId\":\"r-aeYCS6SDG6E9Rpst8HMQ\",\"osId\":\"ecgs\",\"creatorCode\":\"U8d223e0\"}";

        JSONOpt.fastjsonGlobalConfig();

        JSONObject object = JSON.parseObject(jsonStr);

        UserTask userTask = object.toJavaObject(UserTask.class);

        System.out.println(userTask.getCreateTime());
    }
}
