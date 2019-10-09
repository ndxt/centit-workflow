package com.centit.workflow.service.impl;

import com.centit.framework.model.adapter.UserUnitVariableTranslate;
import com.centit.support.compiler.ObjectTranslate;
import com.centit.support.compiler.VariableTranslate;
import com.centit.workflow.po.FlowInstance;
import com.centit.workflow.po.FlowVariable;
import com.centit.workflow.po.NodeInstance;

import java.util.*;

public class FlowVariableTranslate implements UserUnitVariableTranslate, VariableTranslate {

    private UserUnitVariableTranslate flowVarTrans;
    private List<FlowVariable> flowVariables;
    private Map<String,List<String>> flowOrganizes;
    private Map<String,List<String>> flowWorkTeam;
    private Map<String,Set<String>> nodeUnits;
    private Map<String,Set<String>> nodeUsers;

    private NodeInstance nodeInst;
    private FlowInstance flowInst;

    public void collectNodeUnitsAndUsers(FlowInstance flowInst) {
        nodeUnits = new HashMap<>();
        nodeUsers = new HashMap<>();

        String token = nodeInst.getRunToken();

        for(NodeInstance ni : flowInst.getNodeInstances()){
            String nc = ni.getNodeCode();
            if(nc != null && (token.equals(ni.getRunToken()) || token.startsWith(ni.getRunToken()+"."))){
                Set<String> nUnits = nodeUnits.get(nc);
                if(nUnits == null)
                    nUnits = new HashSet<>();
                nUnits.add( ni.getUnitCode());
                nodeUnits.put(nc, nUnits);

                Set<String> nUsers = nodeUsers.get(nc);
                if(nUsers == null)
                    nUsers = new HashSet<>();
                nUsers.add( ni.getUserCode());
                nodeUsers.put(nc, nUsers);
            }
        }
    }

    public FlowVariableTranslate(NodeInstance nodeInstance, FlowInstance flowInstance){
        nodeInst = nodeInstance;
        flowInst = flowInstance;
        collectNodeUnitsAndUsers(flowInst);
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
        String thisToken = nodeInst.getRunToken();
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
    public Object getGeneralVariable(String varName) {
        if(flowVarTrans !=null){
            Object obj =  flowVarTrans.getGeneralVariable(varName);
            if(obj!=null)
                return obj;
        }
        /**
         * 程序设置的流程变量
         */
        FlowVariable v = findFlowVariable(varName);
        if(v !=null)
            return v.getVarValue();

        List<String> users = flowWorkTeam.get(varName);
        if(users != null)
            return users;

        List<String> units = flowOrganizes.get(varName);
        if(units != null)
            return units;

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
        else if("nodeunit".equalsIgnoreCase(varName))
            return nodeInst.getUnitCode();

        return null;
    }


    @Override
    public Object getLabelValue(String varName) {
        return this.getGeneralVariable(varName);
    }

    @Override
    public Object getVarValue(String varName) {
        return this.getGeneralVariable(varName);
    }

    public Set<String> getUsersVariable(String varName){
        if(flowVarTrans !=null){
            Set<String> sUsers =  flowVarTrans.getUsersVariable(varName);
            if(sUsers!=null && sUsers.size()>0)
                return sUsers;
        }
        FlowVariable v = findFlowVariable(varName);
        if(v !=null)
            return v.getVarSet();

        if(flowWorkTeam !=null){
            List<String> sUsers = flowWorkTeam.get(varName);
            if(sUsers != null )
                return new HashSet<>(sUsers);
        }
        return nodeUsers.get(varName);
    }

    /**
     * 返回机构表达式中的自定义变量对应的用户组
     * @param varName 自定义变量
     * @return
     */
    public Set<String> getUnitsVariable(String varName)
    {
        if(flowVarTrans !=null){
            Set<String> sUnits =  flowVarTrans.getUnitsVariable(varName);
            if(sUnits!=null && sUnits.size()>0)
                return sUnits;
        }
        FlowVariable v = findFlowVariable(varName);
        if(v !=null)
            return v.getVarSet();
        if(flowOrganizes !=null){
            List<String> sUnits = flowOrganizes.get(varName);
            if(sUnits != null )
                return new HashSet<>(sUnits);
        }
        return nodeUnits.get(varName);
    }

    public UserUnitVariableTranslate getFlowVarTrans() {
        return flowVarTrans;
    }
}
