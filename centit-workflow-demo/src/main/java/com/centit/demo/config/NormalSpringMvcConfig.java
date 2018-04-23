package com.centit.demo.config;

import com.centit.framework.config.BaseSpringMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * Created by zou_wy on 2017/3/29.
 */
@Configuration
@ComponentScan(basePackages = {"com.centit.demo.controller","com.centit.workflow.client.controller"},
        includeFilters = {@ComponentScan.Filter(type= FilterType.ANNOTATION,value= org.springframework.stereotype.Controller.class)},
        useDefaultFilters = false)
public class NormalSpringMvcConfig extends BaseSpringMvcConfig {

}
