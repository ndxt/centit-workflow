package com.centit.workflow.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.json.JSONOpt;
import com.centit.support.json.JsonPropertyUtils;
import com.centit.workflow.commons.ExtraFlowManager;
import com.centit.workflow.po.*;
import com.centit.workflow.service.FlowDefine;
import com.centit.workflow.service.FlowEngine;
import com.centit.workflow.service.FlowManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;



@Controller
@RequestMapping("/flow/manager")
public class FlowManagerController extends BaseController {
    //public static final Logger logger = LoggerFactory.getLogger(SampleFlowManagerController.class);

    @Resource
    private FlowManager flowManager;
    @Resource
    private FlowEngine flowEng;
    @Resource
    private FlowDefine flowDef;

    private ExtraFlowManager extraFlowManager;
    private ResponseData resData=new ResponseData();
    private  Map<Class<?>, String[]> excludes;
    /**
     * 流程实例检索查询
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc,
        HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        List<FlowInstance> listObjects = flowManager.listFlowInstance(searchColumn,pageDesc);
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);
        JsonResultUtils.writeResponseDataAsJson(resData, response,JsonPropertyUtils.getIncludePropPreFilter(FlowInstance.class, field));
    }
    
    /**
     * 根据id获取流程实例对象
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/{flowInstId}", method = RequestMethod.GET)
    public void getFlowInstance(@PathVariable Long flowInstId,  HttpServletResponse response) {
        FlowInstance flowInst = flowManager.getFlowInstance(flowInstId);
        Map<String ,Object> result=new HashMap<String,Object>();
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(FlowInstance.class,new String[]{"flowDefine","wfStageInstances"});
        excludes.put(NodeInstance.class,new String[]{"wfActionLogs"});
        excludes.put(NodeInfo.class,new String[]{"flowDefine"});
        List<StageInstance>stageList= flowManager.listStageInstByFlowInstId(flowInstId);
        String viewFlowInst= flowManager.viewFlowInstance(flowInstId);
        result.put("flowInst", flowInst);
        result.put("viewFlowInst", viewFlowInst);
        result.put("stageList",stageList);
        JsonResultUtils.writeSingleDataJson(result, response,JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }
    
    /**
     * 查看流程图
     * @param response
     */
    @RequestMapping(value = "/viewxml/{flowInstId}", method = RequestMethod.GET)
    public void viewRuntimeXml(@PathVariable Long flowInstId,HttpServletRequest request, HttpServletResponse response) {
        FlowInstance flowInst = flowManager.getFlowInstance(flowInstId);
        String  flowCode=flowInst.getFlowCode();
        Long version=flowInst.getVersion();
        if(StringUtils.isNotBlank(flowCode))
        { 
            FlowInfo obj = flowDef.getFlowDefObject(flowCode,version);
            String xml=obj.getFlowXmlDesc();
            String viewXml= flowManager.viewFlowInstance(flowInstId);
            HashMap<String ,String > result=new HashMap<String ,String >();
            result.put("xml", xml);
            result.put("viewXml",viewXml);
        JsonResultUtils.writeSingleDataJson(result, response);}
    }
    
    /**
     * 查看流程图
     * @param response
     */
    @RequestMapping(value = "/nodesxml/{flowInstId}", method = RequestMethod.GET)
    public void viewNodeInstancesXml(@PathVariable Long flowInstId,HttpServletRequest request, HttpServletResponse response) {
        String nodesxml= flowManager.viewFlowNodeInstance(flowInstId);
        HashMap<String ,String > result=new HashMap<String ,String >();
        result.put("nodesxml",nodesxml);
        JsonResultUtils.writeSingleDataJson(result, response);
    }
    
    /**
     * 获取组织机构列表
     * TODO:flowEng.viewFlowOrganize(flowInstId)要是直接返回unitInfo更方便
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/getorglist/{flowInstId}", method = RequestMethod.GET)
    public void getOrganizeList(@PathVariable Long flowInstId,  HttpServletResponse response){
        Map<String,List<String>> organizeMap=flowEng.viewFlowOrganize(flowInstId);
        List<Map<String,String>>organizeList=new ArrayList<Map<String,String>>();
        for (Map.Entry<String,List<String>> entry : organizeMap.entrySet()) {
            for(String unitCode:entry.getValue()){
                HashMap <String,String> unitTempMap=new HashMap<String,String>();
                unitTempMap.put("roleCode",entry.getKey());
                unitTempMap.put("unitCode", unitCode);
                unitTempMap.put("unitName", CodeRepositoryUtil.getValue("unitcode",unitCode));
                organizeList.add(unitTempMap);
            }
        }
        resData.addResponseData(OBJLIST, organizeList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    /**
     * 删除指定的流程组织机构
     * @return
     */
    @RequestMapping(value="/deleteorg/{flowInstId}/{roleCode}/{unitCode}",method=RequestMethod.GET)
    public void deleteOrg(@PathVariable Long flowInstId,@PathVariable String roleCode,@PathVariable String unitCode,HttpServletResponse response){
        flowEng.deleteFlowOrganize(flowInstId, roleCode, unitCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /**
     * 删除指定roleCode下的所有流程工作机构
     * @return
     */
    @RequestMapping(value="/deleteorg/{flowInstId}/{roleCode}",method=RequestMethod.GET)
    public void deleteOrgAll(@PathVariable Long flowInstId,@PathVariable String roleCode,HttpServletResponse response){
        flowEng.deleteFlowOrganize(flowInstId, roleCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /**
     * 添加流程工作机构
     * @return
     */
    @RequestMapping(value="/saveorg/{flowInstId}/{roleCode}/{unitCode}/{authDesc}",method=RequestMethod.POST)
    public void assignOrganize(@PathVariable Long flowInstId,@PathVariable String roleCode,
                               @PathVariable String unitCode,@PathVariable String authDesc, HttpServletRequest request,HttpServletResponse response){
        flowEng.assignFlowOrganize(flowInstId,roleCode,unitCode,authDesc);
        JsonResultUtils.writeSingleDataJson("", response);
        
    }
    
    /**
     * 给一个节点指定任务、用这个代替系统自动分配任务
     */
    @RequestMapping(value="/assign/{nodeInstId}",method=RequestMethod.POST)
    public void assign(@PathVariable Long nodeInstId, ActionTask actionTask, HttpServletRequest request, HttpServletResponse response) {
        flowManager.assignTask(nodeInstId,
                actionTask.getUserCode(), getLoginUser(request).getUserCode(),
                actionTask.getExpireTime(), actionTask.getAuthDesc());
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /**
     * 收回一个分配的任务
     * @return
     */
    @RequestMapping(value="/disableTask/{taskId}",method=RequestMethod.POST)
    public void disableTask(@PathVariable Long taskId,HttpServletRequest request,HttpServletResponse response) {
        flowManager.disableTask(taskId, getLoginUser(request).getUserCode());
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /**
     * 删除任务
     * @return
     */
    @RequestMapping(value="/deleteTask/{taskId}",method=RequestMethod.POST)
    public void deleteTask(@PathVariable Long taskId,HttpServletRequest request,HttpServletResponse response) {
        flowManager.deleteTask(taskId, getLoginUser(request).getUserCode());
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /*tab：办件角色管理*/
    
    /**
     * 查询办件角色列表
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/getteamlist/{flowInstId}", method = RequestMethod.GET)
    public void getTeamList(@PathVariable Long flowInstId,  HttpServletResponse response){
        Map<String,List<String>> teamMap=flowEng.viewFlowWorkTeam(flowInstId);
        List<Map<String,String>>teamList=new ArrayList<Map<String,String>>();
        for (Map.Entry<String,List<String>> entry : teamMap.entrySet()) {
            Set <HashMap<String,String>> userMap=new HashSet<HashMap<String,String>>();
            for(String userCode:entry.getValue()){
                Map <String,String>  teamTempMap=new HashMap<String,String>();
                teamTempMap.put("roleCode",entry.getKey());
                teamTempMap.put("userCode", userCode);
                teamTempMap.put("userName", CodeRepositoryUtil.getValue("userCode",userCode));
                teamList.add(teamTempMap);
            }
        }
        resData.addResponseData(OBJLIST, teamList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    
    /**
     * 删除指定roleCode下的所有流程工作小组
     * @return
     */
    @RequestMapping(value="/deleteteam/{flowInstId}/{roleCode}",method=RequestMethod.GET)
    public void deleteWorkTeam(@PathVariable Long flowInstId,@PathVariable String roleCode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        flowEng.deleteFlowWorkTeam(flowInstId, roleCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /**
     * 删除指定的流程工作小组
     * @return
     */
    @RequestMapping(value="/deleteteam/{flowInstId}/{roleCode}/{userCode}",method=RequestMethod.GET)
    public void deleteWorkTeamUser(@PathVariable Long flowInstId,@PathVariable String roleCode,@PathVariable String userCode,PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        flowEng.deleteFlowWorkTeam(flowInstId, roleCode, userCode);
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /*tab：流程变量管理*/
    
    /**
     * 查询变量列表
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/getvariablelist/{flowInstId}", method = RequestMethod.GET)
    public void getVariableList(@PathVariable Long flowInstId,  HttpServletResponse response){
        List<FlowVariable> variableList=flowEng.listFlowVariables(flowInstId);
        JsonResultUtils.writeSingleDataJson(variableList, response);
    }
    
    
    /**
     * 保存流程变量
     * @return
     */
    @RequestMapping(value="/savevariable/{flowInstId}/{varName}/{varValue}",method=RequestMethod.GET)
    public void saveVariable(@PathVariable Long flowInstId, @PathVariable String varName, @PathVariable String varValue, HttpServletRequest request , HttpServletResponse response){
       String runToken=request.getParameter("runToken");
       flowEng.saveFlowNodeVariable(flowInstId,runToken, varName, StringUtils.isBlank(varValue) ? null : varValue);
       JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /**
     * 新增变量是需要的令牌选择项
     * @param flowInstId
     * @param request
     * @param response
     */
    @RequestMapping(value="/tokens/{flowInstId}",method=RequestMethod.GET)
    public void editVariable(@PathVariable Long flowInstId,HttpServletRequest request ,HttpServletResponse response) {
        List<FlowVariable> flowVariableList = flowEng.listFlowVariables(flowInstId);
        Set<String> existTokenSet = new HashSet<String>();
        for (FlowVariable flowVariable : flowVariableList) {
            existTokenSet.add(flowVariable.getRunToken());
        }
            List<NodeInstance> nodeInstList = flowManager.listFlowInstNodes(flowInstId);
            Set<String> tokenSet = new HashSet<String>();
            Map<String,String> tokenLvbList = new HashMap<String,String>();
            tokenLvbList.put(" ", "------请选择------");
            for (NodeInstance nodeInst : nodeInstList) {
                if (!existTokenSet.contains(nodeInst.getRunToken()) && !tokenSet.contains(nodeInst.getRunToken())) {
                    tokenSet.add(nodeInst.getRunToken());
                    tokenLvbList.put(nodeInst.getRunToken(), nodeInst.getRunToken()); // 获取没有使用过的令牌
                }
            }
            resData.addResponseData(OBJLIST, tokenLvbList);
            JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    
    
    /**
     * 获取管理日志列表
     * @return
     */
    @RequestMapping(value="/getmgrloglist/{flowInstId}",method=RequestMethod.GET)
    public void getMgrLogList(@PathVariable Long flowInstId,PageDesc pageDesc,HttpServletResponse response){
       List<ManageActionLog> logList= flowManager.listManageActionLog(flowInstId,pageDesc);
       resData.addResponseData(OBJLIST, logList);
       resData.addResponseData(PAGE_DESC, pageDesc);
       JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 流程操作日志
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/getOptLogList/{flowInstId}",method = RequestMethod.GET)
    public void getOptLogList(@PathVariable Long flowInstId,HttpServletResponse response){
        List<ActionLog> actionLogList = flowManager.listFlowActionLogs(flowInstId);
        JsonResultUtils.writeSingleDataJson(actionLogList,response);
    }
    
   
    
    
    
    /*流程实例管理接口*/
    
    
    /**
     * 暂挂一个流程实例
     * @param wfinstid
     * @param request
     * @param response
     */
    @RequestMapping(value="/suspendinst/{wfinstid}",method = RequestMethod.GET)
    public void suspendInstance(@PathVariable Long wfinstid, HttpServletRequest request, HttpServletResponse response) {
        String mangerUserCode = this.getLoginUser(request).getUserCode();
        String  admindesc =  request.getParameter("stopDesc");   
        flowManager.suspendInstance(wfinstid,mangerUserCode, admindesc);
        if(extraFlowManager !=null){
            extraFlowManager.suspendInstance(wfinstid, mangerUserCode, admindesc);
        }
        JsonResultUtils.writeSingleDataJson("已暂挂", response);
    }
    
    /**
     * 更改机构
     * @param wfinstid
     * @param unitcode
     * @param request
     * @param response
     */
    @RequestMapping(value="/changeunit/{wfinstid}/{unitcode}",method=RequestMethod.GET)
    public void changeUnit(@PathVariable Long wfinstid,@PathVariable String unitcode,HttpServletRequest request, HttpServletResponse response)
    {
        flowManager.updateFlowInstUnit(wfinstid, unitcode,this.getLoginUser(request).getUserCode());
        JsonResultUtils.writeSingleDataJson("", response);
    }

    /*流程实例状态管理api*/
    /**
     * 终止流程实例
     * @param flowInstId
     * @param request
     * @param response
     */
    @RequestMapping(value="/stopinst/{flowInstId}",method=RequestMethod.GET)
    public void stopInstance(@PathVariable Long flowInstId, HttpServletRequest request,HttpServletResponse response){
        flowManager.stopInstance(flowInstId, this.getLoginUser(request).getUserCode(), "");
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /**
     * 激活流程实例
     * @param flowInstId
     * @param request
     * @param response
     */
    @RequestMapping(value="/activizeinst/{flowInstId}",method=RequestMethod.GET)
    public void activizeInstance(@PathVariable Long flowInstId, HttpServletRequest request,HttpServletResponse response){
        flowManager.activizeInstance(flowInstId, this.getLoginUser(request).getUserCode(), "");
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /*节点状态管理api
     * 1.回滚一个流程节点到上一节点
     * 2.提交，强制一个流程节点前进到下一个节点
     * 3.对一个正在运行的节点实例强制游离
     * 4.针对一个正在运行且被强制游离的节点实例，结束游离状态
     * 5. 针对一个完成的节点实例，创建游离节点
     * 6.唤醒一个暂挂节点实例
     * 7.从这个节点重新运行该流程，包括已经结束的流程
     * 8.暂挂一个节点实例
     * */
    /**
     * 回滚一个流程节点到上一节点
     */
    @RequestMapping(value="/nodestate/{nodeInstId}/{bo}",method=RequestMethod.GET)
    public void changeFlowInstState(@PathVariable Long nodeInstId,  HttpServletRequest request,@PathVariable String bo,HttpServletResponse response) {
        switch(bo.charAt(0))
        {
        case '1':
            flowEng.rollbackOpt(nodeInstId, this.getLoginUser(request).getUserCode());
            break;//这儿必须有break，不然会继续往后执行的。
        case '2':
            flowManager.forceCommit(nodeInstId, this.getLoginUser(request).getUserCode());
            break;
        case '3':  
            flowManager.forceDissociateRuning(nodeInstId, this.getLoginUser(request).getUserCode());
            break;
        case '6':
            String mangerUserCode =this.getLoginUser(request).getUserCode();
            String timeLimit=request.getParameter("timeLimit");
            if (timeLimit != null) {
                flowManager.activizeInstance(nodeInstId, timeLimit,
                    mangerUserCode);
            } else {
                flowManager.activizeNodeInstance(nodeInstId, mangerUserCode);
            }
            break;
        case '7':
            flowManager.resetFlowToThisNode(nodeInstId, this.getLoginUser(request).getUserCode());
            break;
        case '8':
            flowManager.suspendNodeInstance(nodeInstId, this.getLoginUser(request).getUserCode());
            break;
         
        }
        NodeInstance newNode=flowEng.getNodeInstById(nodeInstId);
        excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(NodeInstance.class,new String[]{"wfActionLogs","node"});
        JsonResultUtils.writeSingleDataJson(newNode, response,JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }

    /**
     * 从这个节点重新运行该流程，包括已经结束的流程
     * @return
     */
    @RequestMapping(value="/resetToCurrent/{nodeInstId}",method=RequestMethod.GET)
    public void resetToCurrent(@PathVariable Long nodeInstId,  HttpServletRequest request,HttpServletResponse response){
        CentitUserDetails user =getLoginUser(request);
        flowManager.resetFlowToThisNode(nodeInstId, user.getUserCode());
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    /**
     * 任务列表查询，查询条件可自助添加
     */
    @RequestMapping(value="/listNodeOpers/{nodeInstId}",method=RequestMethod.GET)
    public void listNodeOpers(@PathVariable Long nodeInstId,  HttpServletRequest request,HttpServletResponse response) {
        List<UserTask> objList = flowManager.listNodeTasks(nodeInstId);
        JsonResultUtils.writeSingleDataJson(objList, response);
    }

    /**
     * 返回节点的操作记录，或者日志
     * @return
     */
    @RequestMapping(value="/viewnode/{nodeInstId}",method=RequestMethod.GET)
    public void viewNodeInstanceInfo(@PathVariable Long nodeInstId,HttpServletResponse response){
        NodeInstance  nodeInst = flowEng.getNodeInstById(nodeInstId);
        NodeInfo nodeInfo =flowDef.getNodeInfoById(nodeInst.getNodeId());
        List<UserTask> tasks = flowManager.listNodeTasks(nodeInstId);
        List<ActionLog> logs = flowManager.listNodeActionLogs(nodeInstId);
        ResponseData resData = new ResponseData();
        resData.addResponseData("inst", nodeInst);
        resData.addResponseData("node", nodeInfo);
        resData.addResponseData("tasks", tasks);
        resData.addResponseData("logs", logs);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    /**
     * 返回节点的操作记录，或者日志
     * @return
     */
    @RequestMapping(value="/viewflownode/{flowInstId}/{nodeId}",method=RequestMethod.GET)
    public void viewFlowNodeInfo(@PathVariable Long flowInstId,@PathVariable Long nodeId,HttpServletResponse response){
      try{
        FlowInstance  dbobject = flowManager.getFlowInstance(flowInstId);
        NodeInfo nodeInfo =flowDef.getNodeInfoById(nodeId);
        JSONObject nodeOptInfo = new JSONObject();
        nodeOptInfo.put("nodename", nodeInfo.getNodeName());
        int nodeInstInd = 0;
        for(NodeInstance nodeInst : dbobject.getNodeInstances() ){
           if( nodeInst.getNodeId().equals(nodeId)) {
               JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].createtime",DatetimeOpt.convertDatetimeToString(nodeInst.getCreateTime()));
               JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].unitcode",nodeInst.getUnitCode());
               JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].unitname",CodeRepositoryUtil.getValue("unitcode",nodeInst.getUnitCode()));
               if("N".equals(nodeInst.getNodeState())||"R".equals(nodeInst.getNodeState())){
                   List<UserTask> tasks = flowManager.listNodeTasks(nodeInst.getNodeInstId());
                   JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].state", "办理中");
                   int taskInd=0;
                   for(UserTask task:tasks){
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].task["+taskInd+"].usercode", task.getUserCode());
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].task["+taskInd+"].username", 
                               CodeRepositoryUtil.getValue("userCode",task.getUserCode()));
                       taskInd++;
                   }
               }else{
                   JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].state", 
                           CodeRepositoryUtil.getValue("WFInstType", nodeInst.getNodeState()) );
                   List<ActionLog> actions = flowManager.listNodeActionLogs(nodeInst.getNodeInstId());
                   int actionInd=0;
                   for(ActionLog action:actions){                       
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].action["+actionInd+"].usercode", action.getUserCode());
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].action["+actionInd+"].username", 
                               CodeRepositoryUtil.getValue("userCode",action.getUserCode()));
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].action["+actionInd+"].actiontime", 
                               DatetimeOpt.convertDatetimeToString(action.getActionTime()));
                       JSONOpt.setAttribute(nodeOptInfo, "instance["+nodeInstInd+"].action["+actionInd+"].actiontype", 
                               CodeRepositoryUtil.getValue("WfActionType",action.getActionType()));
                       actionInd++;
                   }
               }
               nodeInstInd++;
           }
        }
        nodeOptInfo.put("count", nodeInstInd);
        JsonResultUtils.writeSingleDataJson(nodeOptInfo, response);
        }catch (Exception e) {
         e.printStackTrace();
      }
    }
    
    /**
     * 查找所有没有操作用户的节点
     * @return
     */
    @RequestMapping(value="/nooptnodes",method=RequestMethod.GET)
    public void listNoOptNodes(HttpServletResponse response){
        List<NodeInstance>nodeList = flowManager.listNodesWithoutOpt();
        JsonResultUtils.writeSingleDataJson(nodeList, response);
    }
   
    /**
     * 查看流程工作小组
     * @return
     */
    @RequestMapping(value="/workteam/{flowInstId}",method=RequestMethod.GET)
    public void viewWorkTeam(@PathVariable Long flowInstId,HttpServletRequest request,HttpServletResponse response){
        
        JsonResultUtils.writeSingleDataJson("", response);
    }
    
    
    /**
     * 查询某节点实例下的任务信息
     * @return String
     */
    @RequestMapping(value="/listusertasks/{nodeInstId}",method=RequestMethod.GET)
    public void listNodeInstTasks(@PathVariable Long nodeInstId,HttpServletRequest request,HttpServletResponse response){
        List <ActionTask>taskList = flowManager.listNodeInstTasks(nodeInstId);
        resData.addResponseData(OBJLIST, taskList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    } 
    
    /**
     * 查询某节点实例下的日志信息
     * @return String
     */
    @RequestMapping(value="/nodelogs/{nodeInstId}",method=RequestMethod.GET)
    public void listNodeInstLogs(@PathVariable Long nodeInstId,HttpServletRequest request,HttpServletResponse response){
        List<ActionLog>logList = flowManager.listNodeActionLogs(nodeInstId);
        resData.addResponseData(OBJLIST, logList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    
    /**
     * 当前用户参与的流程
     * @return
     */
    @RequestMapping(value="/myflowinsts",method=RequestMethod.GET)
    public void listUserAttach(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);
        String loginUserCode=this.getLoginUser(request).getUserCode();
        //如果带参数oper,则为过滤当前用户流程，包括在办的或者完成的.
        if(filterMap.get("oper") != null ){
            //办结事项
            if( filterMap.get("oper").equals("comp")){
                filterMap.put("inststate", "C");
            }
            if(!filterMap.get("oper").equals("all")){
              //默认当前在办
                filterMap.put("attachuser", loginUserCode);
            }
        }else{
            if(filterMap.get("attachuser") == null || filterMap.get("attachuser").toString().length() == 0){
                filterMap.put("attachuser", loginUserCode);
            }
        }
        //默认提供参与用户动态查询条件，userFlowInstList.jsp --- s_attachuser属性调用
        List <FlowInstance> objList = flowManager.listUserAttachFlowInstance(loginUserCode,(String)filterMap.get("flowPhase"),filterMap, pageDesc);
        /*request.setAttribute("s_attachuser", filterMap.get("attachuser"));
        request.setAttribute("s_inststate", filterMap.get("inststate"));*/
        resData.addResponseData(OBJLIST, objList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    /**
     * 查看用户相关流程
     * @return
     */
    @RequestMapping(value="/relatedflowinsts",method=RequestMethod.GET)
    public void listUserFlow(PageDesc pageDesc,HttpServletRequest request,HttpServletResponse response){
        Map<String, Object> filterMap = convertSearchColumn(request);
        String loginUserCode=this.getLoginUser(request).getUserCode();
        //如果带参数oper,则为过滤当前用户流程，包括在办的或者完成的
        if(filterMap.get("oper") != null ){
            //办结事项
            if( filterMap.get("oper").equals("comp")){
                filterMap.put("inststate", "C");
                
            }
            if(!filterMap.get("oper").equals("all")){
              //默认当前在办
                filterMap.put("attachuser", loginUserCode);
            }
            
        }else{
            if(filterMap.get("attachuser") == null || filterMap.get("attachuser").toString().length() == 0){
                filterMap.put("attachuser",loginUserCode);
            }
        }
        //默认提供参与用户动态查询条件，userFlowInstList.jsp --- s_attachuser属性调用
        List<FlowInstance> objList = flowManager.listUserAttachFlowInstance(loginUserCode,(String)filterMap.get("flowPhase"),filterMap, pageDesc);
        /*request.setAttribute("s_attachuser", filterMap.get("attachuser"));
        request.setAttribute("s_inststate", filterMap.get("inststate"));*/
        resData.addResponseData(OBJLIST, objList);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    //新增工作组
    @RequestMapping(value="/assignFlowWorkTeam/{flowInstId}/{roleCode}/{userCode}/{authdesc}",method=RequestMethod.POST)
    public void assignFlowWorkTeam(@PathVariable String flowInstId,@PathVariable String roleCode,
                                   @PathVariable String userCode,@PathVariable String authdesc,HttpServletRequest request,HttpServletResponse response){
        flowEng.assignFlowWorkTeam(Long.parseLong(flowInstId), roleCode,
                 userCode, authdesc);
        JsonResultUtils.writeBlankJson(response);
    };

    /**
     * 获取流程实例的关注列表
     * @param flowInstId
     * @param response
     */
    @RequestMapping(value = "/getAttByFlowInstId/{flowInstId}",method = RequestMethod.GET)
    public void getAttByFlowInstId(@PathVariable long flowInstId,HttpServletResponse response){
        List<InstAttention> attentions = flowEng.viewFlowAttention(flowInstId);
        resData.addResponseData(OBJLIST, attentions);
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 新增流程关注
     * @param instAttention
     * @param response
     */
    @RequestMapping(value = "/addAttention/{flowInstId}",method = RequestMethod.POST)
    public void addAttention(@PathVariable long flowInstId, InstAttention instAttention,HttpServletResponse response){
        flowEng.saveFlowAttention(flowInstId,instAttention.getUserCode(),instAttention.getAttSetUser());
        JsonResultUtils.writeBlankJson(response);
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public void Test(HttpServletResponse response){
        flowManager.moveUserTaskTo("u0000002","u0000001","u0000000","测试");
        JsonResultUtils.writeBlankJson(response);
    }
}
