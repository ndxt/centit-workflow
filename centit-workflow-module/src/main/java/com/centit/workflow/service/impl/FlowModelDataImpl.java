package com.centit.workflow.service.impl;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.SysUserFilterEngine;
import com.centit.framework.model.basedata.IOptMethod;
import com.centit.workflow.dao.FlowInfoDao;
import com.centit.workflow.dao.FlowOptDefDao;
import com.centit.workflow.dao.FlowOptInfoDao;
import com.centit.workflow.dao.FlowRoleDao;
import com.centit.workflow.po.FlowInfo;
import com.centit.workflow.po.FlowOptDef;
import com.centit.workflow.po.FlowOptInfo;
import com.centit.workflow.po.FlowStage;
import com.centit.workflow.service.FlowModelData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;


/**
 * 实现一个最简单的元数据提供类
 * 实际业务应该从数据库中获取
 */
@Service
@Transactional
public class FlowModelDataImpl implements FlowModelData, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 例举所有节点任务分配机制
     * A:一般 B:抢先机制 C:多人操作
     */
    @Resource
    private FlowInfoDao flowDefineDao;

    @Resource
    private FlowOptDefDao wfOptDefDao;

    @Resource
    private FlowRoleDao flowRoleDao;

    @Resource
    private FlowOptInfoDao flowOptInfoDao;


    @Override
    public Map<String, String> listAllOptType() {

        Map<String, String> optType = new HashMap<String, String>();
        optType.put("A", "一般");
        optType.put("B", "抢先机制");
        optType.put("C", "多人操作");
        optType.put("D", "自动执行");
        optType.put("S", "子流程");
        optType.put("E", "哑元");

        return optType;
    }

    /**
     * 例举所有节点类别
     * A:开始 B:首节点 C:一般 D:分支 E:汇聚 F结束
     */
    @Override
    public Map<String, String> listAllNoteType() {
        Map<String, String> nodeType = new HashMap<String, String>();
        //nodeType.put("A", "开始");
        //nodeType.put("B", "首节点");
        nodeType.put("C", "业务");
        nodeType.put("R", "路由");
        //nodeType.put("E", "汇聚 ");
        //nodeType.put("F", "结束");
        return nodeType;
    }

    //单机版本获取业务操作方法
    public Map<String, String> listAllOptCode_old(String flowCode, long version) {
        FlowInfo flowDef = this.flowDefineDao.getFlowDefineByID(flowCode, version);
        List<? extends IOptMethod> optDefList = CodeRepositoryUtil.getOptMethodByOptID(flowDef.getOptId());
        Map<String, String> optmap = new HashMap<>();

        if (optDefList != null) {
            for (IOptMethod optdef : optDefList) {
                optmap.put(optdef.getOptCode(), optdef.getOptName());
            }
        }

        return optmap;
    }

    /**
     * 暂时写死的业务操作
     *
     * @param flowCode
     * @param version
     * @return
     */
    public Map<String, String> listAllOptCode_o(String flowCode, long version) {
        Map<String, String> optMap = new HashMap<>();

        optMap.put("approval/approval.html", "通用审批");
        optMap.put("approval/zwh.html", "置文号");

        return optMap;
    }

    /**
     * 读取工作定义的业务操作
     *
     * @param flowCode
     * @param version
     * @return
     */
    public Map<String, String> listAllOptCode(String flowCode, long version) {
        FlowInfo flowDef = this.flowDefineDao.getFlowDefineByID(flowCode, version);
        //FlowOptInfo flowOptInfo = flowOptInfoDao.getObjectById(flowDef.getOptId());
        List<FlowOptDef> wfOptDefs = this.wfOptDefDao.listObjectsByProperty("optId", flowDef.getOptId());
        Map<String, String> optMap = new HashMap<>();
        for (FlowOptDef f : wfOptDefs) {
            //optMap.put(flowOptInfo.getOptUrl() + f.getOptMethod(), f.getOptName());
            optMap.put(f.getOptCode(),f.getOptName());
        }
        return optMap;
    }


    /**
     * 列举流程定义对应的操作定义
     */
    public Map<String, String> listAllOptCode(String flowCode) {

        return listAllOptCode(flowCode, 0L);
    }

    @Override
    public Map<String, Map<String, String>> listAllRole() {

        Map<String, Map<String, String>> roleList = new HashMap<>();

        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("","请选择");
        map1.putAll(CodeRepositoryUtil.getLabelValueMap("StationType"));
        roleList.put(SysUserFilterEngine.ROLE_TYPE_GW.toLowerCase() /*"gw"*/, map1);

        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("","请选择");
        map2.putAll(CodeRepositoryUtil.getLabelValueMap("RankType"));
        roleList.put(SysUserFilterEngine.ROLE_TYPE_XZ.toLowerCase() /*"xz"*/, map2);

        //获取所有流程角色
        Map<String, String> map3 = new LinkedHashMap<>();
        map3.put("","请选择");
        map3.putAll(flowRoleDao.listAllRoleMsg());
        roleList.put("sp".toLowerCase(), map3);

        return roleList;
    }


    @Override
    public String getUserNameByCode(String userCode) {
        return CodeRepositoryUtil.getValue(CodeRepositoryUtil.USER_CODE, userCode);
    }

    @Override
    public String getUnitNameByCode(String unitCode) {
        return CodeRepositoryUtil.getValue(CodeRepositoryUtil.UNIT_CODE, unitCode);
    }

    @Override
    public String getRoleNameByCode(String roleCode) {
        return CodeRepositoryUtil.getValue(CodeRepositoryUtil.ROLE_CODE, roleCode);
    }


    @Override
    public Map<String, String> listAllSubFlow() {
        Map<String, String> subwf = new HashMap<String, String>();

        List<FlowInfo> listflow = flowDefineDao.getFlowsByState("B");
        for (FlowInfo wfFlowDefine : listflow) {
            subwf.put(wfFlowDefine.getFlowCode(), wfFlowDefine.getFlowName());
        }

        return subwf;
    }

    /**
     * 根据流程代码、流程版本获取流程阶段信息
     *
     * @param flowCode
     * @return
     */
    public Map<String, String> listFlowStages(String flowCode) {
        FlowInfo flowDef = flowDefineDao.getFlowDefineByID(flowCode, 0L);//流程0版本读取
        Set<FlowStage> stageSet = flowDef.getFlowStagesSet();

        Map<String, String> optmap = new HashMap<String, String>();

        if (stageSet != null && !stageSet.isEmpty()) {
            Iterator<? extends FlowStage> it = stageSet.iterator();
            while (it.hasNext()) {
                FlowStage stage = it.next();
                optmap.put(stage.getStageCode(), stage.getStageName());
            }
        }
        return optmap;
    }

}
