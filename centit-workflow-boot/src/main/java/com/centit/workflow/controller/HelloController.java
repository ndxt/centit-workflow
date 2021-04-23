package com.centit.workflow.controller;

import com.centit.workflow.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liu_cc
 * @create 2021-04-21 17:27
 */
@RestController
public class HelloController {

    @Autowired
    private FlowEngine flowEngine;

    @Autowired
    private FlowManager flowManager;

    @Autowired
    private FlowDefine flowDefine;

    @Autowired
    private FlowOptService flowOptService;

    @Autowired
    private RoleFormulaService roleFormulaService;

    @RequestMapping("/hello")
    public String hello() {
        return "Hello workflow";
    }

}
