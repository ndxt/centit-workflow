package com.centit.workflow.dubbo.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:dubbo-workflow-client.xml"})
public class DubboConfigClient {
}
