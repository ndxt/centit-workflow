package com.centit.workflow.commons;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by codefan on 17-9-11.
 * @author codefan
 *
 * String nodeInstId, String userCode, String grantorCode,
 *                                       String unitCode,
 *                                       Map<Long, Set<String>> nodeUnits, Map<Long, Set<String>> nodeOptUsers
 */
public interface FlowOptParamOptions {
    String getUserCode();

    String getUnitCode();

    boolean isLockOptUser();

    String getWorkUserCode();

    Map<String, Object> getVariables();

    Map<String, Object> getGlobalVariables();

    Map<String, List<String>> getFlowRoleUsers();

    Map<String, List<String>> getFlowOrganizes();

    Map<String, String> getNodeUnits();

    Map<String, Set<String>> getNodeOptUsers();

    //-----------------------------------------------
    void setVariables(Map<String, Object> variables);

    void setGlobalVariables(Map<String, Object> variables);

    void setFlowRoleUsers(Map<String, List<String>> flowRoleUsers);

    void setFlowOrganizes(Map<String, List<String>> flowOrganizes);

    void setNodeUnits(Map<String, String> nodeUnits);

    void setNodeOptUsers(Map<String, Set<String>> nodeOptUsers);
}
