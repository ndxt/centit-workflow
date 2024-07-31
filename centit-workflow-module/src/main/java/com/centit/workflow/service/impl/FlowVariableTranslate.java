package com.centit.workflow.service.impl;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.CollectionsOpt;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.FlowWorkTeam;
import com.centit.workflow.po.NodeInstance;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FlowVariableTranslate implements UserUnitVariableTranslate {

    private Map<String/*varName*/, Map<String/*token*/, Object>> innerVariable;

    private UserUnitVariableTranslate flowVarTrans;
    private List<FlowVariable> flowVariables;
    private Map<String,List<String>> flowOrganizes;
    private List<FlowWorkTeam> flowWorkTeam;
    private Map<String,Set<String>> nodeUnits;
    private Map<String,Set<String>> nodeUsers;
    //可能为 Null
    private NodeInstance nodeInst;
    private FlowInstance flowInst;

    public List<FlowWorkTeam> listTeamUserByRole(String roleCode){
        List<FlowWorkTeam> users = new ArrayList<>();
        if(flowWorkTeam!=null && !flowWorkTeam.isEmpty()){
            for(FlowWorkTeam u : flowWorkTeam){
                if(StringUtils.equals(roleCode ,u.getRoleCode())){
                    users.add(u);
                }
            }
        }
        return users;
    }

    public void collectNodeUnitsAndUsers(FlowInstance flowInst) {
        nodeUnits = new HashMap<>();
        nodeUsers = new HashMap<>();

        String token = nodeInst == null? NodeInstance.RUN_TOKEN_GLOBAL : nodeInst.getRunToken();
        if (null != flowInst.getFlowNodeInstances()) {
            for (NodeInstance ni : flowInst.getFlowNodeInstances()) {
                String nc = ni.getNodeCode();
                if (nc != null && (token.equals(ni.getRunToken()) || token.startsWith(ni.getRunToken() + "."))) {
                    Set<String> nUnits = nodeUnits.get(nc);
                    if (nUnits == null)
                        nUnits = new HashSet<>();
                    nUnits.add(ni.getUnitCode());
                    nodeUnits.put(nc, nUnits);

                    Set<String> nUsers = nodeUsers.get(nc);
                    if (nUsers == null)
                        nUsers = new HashSet<>();
                    nUsers.add(ni.getUserCode());
                    nodeUsers.put(nc, nUsers);
                }
            }
        }
    }

    public FlowVariableTranslate(NodeInstance nodeInstance, FlowInstance flowInstance){
        //可能为 Null
        innerVariable = new HashMap<>();
        nodeInst = nodeInstance;
        flowInst = flowInstance;
        collectNodeUnitsAndUsers(flowInst);
    }

    public void setInnerVariable(String name, String token, Object value) {
        Map<String/*token*/, Object> varMap = this.innerVariable.get(name);
        if(varMap == null){
            varMap = new HashMap<>(4);
        }
        varMap.put(token, value);
        this.innerVariable.put(name, varMap);
    }

    public Object removeInnerVariable(String name, String token) {
        Map<String/*token*/, Object> varMap = this.innerVariable.get(name);
        if(varMap == null){
            return null;
        }
        if(varMap.containsKey(token)){
            return varMap.remove(token);
        }
        return null;
    }
    public FlowVariable removeFLowVariable(String varName) {
        if(flowVariables==null || flowVariables.size()==0)
            return null;
        String thisToken = nodeInst ==null? null : nodeInst.getRunToken();
        FlowVariable sValue = null;
        int nTL=0;
        for(FlowVariable variable : flowVariables){
            String currToken = variable.getRunToken();
            int cTL = currToken.length();
            if( varName.equals(variable.getVarName()) && ( thisToken==null
                || currToken.equals(thisToken) || thisToken.startsWith(currToken+'.' )) &&  nTL< cTL){
                nTL = cTL;
                sValue = variable;
            }
        }
        if(sValue != null) {
            flowVariables.remove(sValue);
        }
        return sValue;
    }

    public void setNodeInst(NodeInstance nodeInst) {
        this.nodeInst = nodeInst;
    }

    public void setFlowVarTrans(UserUnitVariableTranslate flowVarTrans) {
        this.flowVarTrans = flowVarTrans;
    }

    public void setFlowVariables(List<FlowVariable> flowVariables) {
        this.flowVariables = flowVariables;
    }

    public void setFlowOrganizes(Map<String,List<String>> flowOrganizes) {
        this.flowOrganizes = flowOrganizes;
    }

    public void setFlowWorkTeam(List<FlowWorkTeam> flowWorkTeam) {
        this.flowWorkTeam = flowWorkTeam;
    }

    private FlowVariable findFlowVariable(String varName){
        if(flowVariables==null || flowVariables.isEmpty())
            return null;
        String thisToken = nodeInst ==null? null : nodeInst.getRunToken();
        FlowVariable sValue = null;
        int nTL=0;
        for(FlowVariable variable : flowVariables){
          String currToken = variable.getRunToken();
          int cTL = currToken.length();
          if( varName.equals(variable.getVarName()) && ( thisToken==null
                  || currToken.equals(thisToken) || thisToken.startsWith(currToken+'.' )) &&  nTL< cTL){
              nTL = cTL;
              sValue = variable;
          }
        }
        return sValue;
    }

    public Object findInnerVariable(String varName) {
        Map<String/*token*/, Object> varMap = this.innerVariable.get(varName);
        if(varMap==null || varMap.size()==0)
            return null;
        String thisToken = nodeInst ==null? null : nodeInst.getRunToken();
        Object sValue = null;
        int nTL=0;
        for(Map.Entry<String, Object> ent : varMap.entrySet()){
            String currToken = ent.getKey();
            int cTL = currToken.length();
            if( (NodeInstance.RUN_TOKEN_GLOBAL.equals(thisToken) || thisToken==null
                || currToken.equals(thisToken) || thisToken.startsWith(currToken+'.' )) &&  nTL< cTL){
                nTL = cTL;
                sValue = ent.getValue();
            }
        }
        return sValue;
    }

    public List<String> findFlowTeam(String varName) {
        if(flowWorkTeam==null || flowWorkTeam.isEmpty())
            return null;
        String thisToken = nodeInst ==null? null : nodeInst.getRunToken();
        String currentToken = thisToken;
        int nTL=0;

        // 找到最匹配的token
        for(FlowWorkTeam team : flowWorkTeam){
            if(StringUtils.equals(varName, team.getRoleCode())){
                String currToken = team.getRunToken();
                int cTL = currToken.length();
                if( (NodeInstance.RUN_TOKEN_GLOBAL.equals(thisToken) || thisToken==null
                    || currToken.equals(thisToken) || thisToken.startsWith(currToken+'.' )) &&  nTL< cTL) {
                    nTL = cTL;
                    currentToken = currToken;
                }
            }
        }

        //找到所有的对应额的用户
        List<String> sValue = new ArrayList<>();
        for(FlowWorkTeam team : flowWorkTeam){
            if(StringUtils.equals(varName, team.getRoleCode())
                    && StringUtils.equals(currentToken, team.getRunToken())){
                sValue.add(team.getUserCode());
            }
        }
        return sValue;
    }

    @Override
    public Object getVarValue(String varName) {
        // 内部变量最高优先级
        Object objV = findInnerVariable(varName);
        if(objV != null ){
            return objV;
        }

        if(flowVarTrans !=null){
            Object obj =  flowVarTrans.getVarValue(varName);
            if(obj!=null) {
                return obj;
            }
        }
        /**
         * 程序设置的流程变量
         */
        FlowVariable v = findFlowVariable(varName);
        if(v != null) {
            String varStr = v.getVarValue();
            if(FlowVariable.FLOW_VARIABLE_TYPE_SINGLE.equals(v.getVarType())) {
                if (StringRegularOpt.isNumber(varStr)) {
                    return NumberBaseOpt.castObjectToNumber(varStr);
                }
                return varStr;
            } else {
                return StringBaseOpt.objectToStringList(varStr);
            }
        }
        if(flowWorkTeam !=null ) {
            List<String> users = findFlowTeam(varName);
            if (users != null && !users.isEmpty())
                return users;
        }

        if(flowOrganizes !=null ) {
            List<String> units = flowOrganizes.get(varName);
            if (units != null)
                return units;
        }

        Set<String> userset = nodeUsers.get(varName);
        if(userset != null)
            return userset;

        Set<String> unitset = nodeUnits.get(varName);
        if(unitset != null)
            return unitset;
        /**
         * 系统内置变量
         * flowunit 流程机构
         * flowuser 流程创建用户
         * nodeunit 节点机构
         */
        if("flowuser".equalsIgnoreCase(varName))
            return flowInst.getUserCode();
        else if("flowunit".equalsIgnoreCase(varName))
            return flowInst.getUnitCode();
        else if(nodeInst != null && "nodeunit".equalsIgnoreCase(varName))
            return nodeInst.getUnitCode();
        else if(nodeInst != null && "nodeuser".equalsIgnoreCase(varName))
            return nodeInst.getUserCode();
        return null;
    }

    public Set<String> getUsersVariable(String varName){
        // 内部变量最高优先级
        Set<String> sUsers = StringBaseOpt.objectToStringSet(findInnerVariable(varName));
        if(sUsers!=null && !sUsers.isEmpty()) {
            return sUsers;
        }

        if(flowVarTrans !=null){
            sUsers =  flowVarTrans.getUsersVariable(varName);
            if(sUsers!=null && !sUsers.isEmpty() ) {
                return sUsers;
            }
        }

        FlowVariable v = findFlowVariable(varName);
        if(v !=null)
            return CollectionsOpt.cloneSet(v.getVarList());

        if(flowWorkTeam !=null ){
            List<String> users = findFlowTeam(varName);
            if (users != null && !users.isEmpty())
                return new HashSet<>(users);
        }

        if("flowuser".equalsIgnoreCase(varName))
            return CollectionsOpt.createHashSet(flowInst.getUserCode());
        else  if(nodeInst != null && "nodeuser".equalsIgnoreCase(varName))
            return CollectionsOpt.createHashSet(nodeInst.getUserCode());

        return nodeUsers.get(varName);
    }

    /**
     * 返回机构表达式中的自定义变量对应的用户组
     * @param varName 自定义变量
     * @return
     */
    public Set<String> getUnitsVariable(String varName) {
        Set<String> sUnits = StringBaseOpt.objectToStringSet(findInnerVariable(varName));
        if(sUnits!=null && !sUnits.isEmpty()) {
            return sUnits;
        }
        if(flowVarTrans !=null){
            sUnits =  flowVarTrans.getUnitsVariable(varName);
            if(sUnits!=null && !sUnits.isEmpty())
                return sUnits;
        }

        FlowVariable v = findFlowVariable(varName);
        if(v !=null) {
            return CollectionsOpt.cloneSet(v.getVarList());
        }

        if(flowOrganizes !=null){
            List<String> listUnits = flowOrganizes.get(varName);
            if(listUnits != null && !listUnits.isEmpty() )
                return new HashSet<>(listUnits);
        }

        if("flowunit".equalsIgnoreCase(varName))
            return CollectionsOpt.createHashSet(flowInst.getUnitCode());
        else if(nodeInst != null && "nodeunit".equalsIgnoreCase(varName))
            return CollectionsOpt.createHashSet(nodeInst.getUnitCode());

        return nodeUnits.get(varName);
    }

    public UserUnitVariableTranslate getFlowVarTrans() {
        return flowVarTrans;
    }
/*
    public Map<String, Object> calcScript(String script){
        int nPos = script.indexOf(':');
        if(nPos<1) return null;
        String [] varNames = script.substring(0, nPos).split(",");
        if(*//*varNames==null || *//*varNames.length<1){
            return null;
        }

        VariableFormula formula = new VariableFormula();
        formula.setFormula(script.substring(nPos+1));
        formula.setTrans(this);
        Map<String, Object> retMap = new HashMap<>(varNames.length*3/2+1);
        int i=0;
        while(i<varNames.length) {
            Object retObj = formula.calcFormula();
            String varName =  StringUtils.isBlank(varNames[i]) ? "_arg"+i : varNames[i].trim();
            retMap.put(varName, retObj);
            if(retObj!=null){
                this.setInnerVariable(varName,NodeInstance.RUN_TOKEN_GLOBAL, retObj);
            } else {
                this.removeInnerVariable(varName,NodeInstance.RUN_TOKEN_GLOBAL);
                this.removeFLowVariable(varName);
            }
            String s = formula.skipAWord();
            if(!",".equals(s)) break;
            i++;
        }
        return retMap;
    }*/
}
