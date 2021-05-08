package com.centit.workflow.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"com.centit.workflow.controller"},
    includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = org.springframework.stereotype.Controller.class)},
    useDefaultFilters = false)
public class WorkflowSpringMvcConfig extends BaseSpringMvcConfig {

}
