package com.centit.workflow.service.impl;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import com.centit.support.compiler.VariableFormula;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.NodeInstance;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FlowVariableTranslate implements UserUnitVariableTranslate {

    private Map<String, Object> innerVariable;

    private UserUnitVariableTranslate flowVarTrans;
    private List<FlowVariable> flowVariables;
    private Map<String,List<String>> flowOrganizes;
    private Map<String,List<String>> flowWorkTeam;
    private Map<String,Set<String>> nodeUnits;
    private Map<String,Set<String>> nodeUsers;
    //可能为 Null
    private NodeInstance nodeInst;
    private FlowInstance flowInst;

    public void collectNodeUnitsAndUsers(FlowInstance flowInst) {
        nodeUnits = new HashMap<>();
        nodeUsers = new HashMap<>();

        String token = nodeInst == null? "T" : nodeInst.getRunToken();
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

    public void setInnerVariable(String name, Object value) {
        this.innerVariable.put(name, value);
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

    public void setFlowWorkTeam(Map<String,List<String>> flowWorkTeam) {
        this.flowWorkTeam = flowWorkTeam;
    }

    private FlowVariable findFlowVariable(String varName){
        if(flowVariables==null || flowVariables.size()==0)
            return null;
        String thisToken = nodeInst ==null? "T" : nodeInst.getRunToken();
        FlowVariable sValue = null;
        int nTL=0;
        for(FlowVariable variable : flowVariables){
          String currToken = variable.getRunToken();
          int cTL = currToken.length();
          if( varName.equals(variable.getVarName()) && ( "A".equals(variable.getRunToken()) || thisToken==null
                  || currToken.equals(thisToken) || thisToken.startsWith(currToken+'.' )) &&  nTL< cTL){
              nTL = cTL;
              sValue = variable;
          }
        }

        return sValue;

    }

    @Override
    public Object getVarValue(String varName) {
        // 内部变量最高优先级
        Object objs = innerVariable.get(varName);
        if(objs != null ){
            return objs;
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
            if("S".equals(v.getVarType())) {
                if (StringRegularOpt.isNumber(varStr)) {
                    return NumberBaseOpt.castObjectToNumber(varStr);
                }
                return varStr;
            } else {
                return StringBaseOpt.objectToStringList(varStr);
            }
        }
        if(flowWorkTeam !=null ) {
            List<String> users = flowWorkTeam.get(varName);
            if (users != null)
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

        return null;
    }

    public Set<String> getUsersVariable(String varName){
        // 内部变量最高优先级
        Set<String> sUsers = StringBaseOpt.objectToStringSet(innerVariable.get(varName));
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
            return v.getVarSet();

        if(flowWorkTeam !=null ){
            List<String> listUsers = flowWorkTeam.get(varName);
            if(listUsers != null && !listUsers.isEmpty())
                return new HashSet<>(listUsers);
        }
        return nodeUsers.get(varName);
    }

    /**
     * 返回机构表达式中的自定义变量对应的用户组
     * @param varName 自定义变量
     * @return
     */
    public Set<String> getUnitsVariable(String varName) {
        Set<String> sUnits = StringBaseOpt.objectToStringSet(innerVariable.get(varName));
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
            return v.getVarSet();
        }

        if(flowOrganizes !=null){
            List<String> listUnits = flowOrganizes.get(varName);
            if(listUnits != null && !listUnits.isEmpty() )
                return new HashSet<>(listUnits);
        }
        return nodeUnits.get(varName);
    }

    public UserUnitVariableTranslate getFlowVarTrans() {
        return flowVarTrans;
    }

    public Map<String, Object> calcScript(String script){
        int nPos = script.indexOf(':');
        if(nPos<1) return null;
        String [] varNames = script.substring(0, nPos).split(",");
        if(/*varNames==null || */varNames.length<1){
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
            if(retObj!=null){
                retMap.put(varName, retObj);
                innerVariable.put(varName, retObj);
            }
            String s = formula.skipAWord();
            if(!",".equals(s)) break;
            i++;
        }
        return retMap;
    }
}
