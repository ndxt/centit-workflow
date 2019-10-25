package com.centit.workflow.po;

import lombok.Data;

import java.util.List;

@Data
public class TaskMove {
    private String formUser;
    private String toUser;
    private String operatorUser;
    private String moveDesc;
    private List<String> nodeInstIds;
}
