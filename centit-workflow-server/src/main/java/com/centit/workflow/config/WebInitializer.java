package com.centit.workflow.config;

import com.centit.framework.config.SystemSpringMvcConfig;
import com.centit.framework.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;

/**
 * Created by zou_wy on 2017/3/29.
 */
public class WebInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        initializeSpringConfig(servletContext);
        initializeSystemServletConfig(servletContext);
        initializeNormalServletConfig(servletContext);
        String [] servletUrlPatterns = {"/system/*","/workflow/*"};
        WebConfig.registerRequestContextListener(servletContext);
        WebConfig.registerSingleSignOutHttpSessionListener(servletContext);
        WebConfig.registerCharacterEncodingFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHttpPutFormContentFilter(servletContext, servletUrlPatterns);
        WebConfig.registerHiddenHttpMethodFilter(servletContext, servletUrlPatterns);
        WebConfig.registerRequestThreadLocalFilter(servletContext);
        WebConfig.registerSpringSecurityFilter(servletContext, servletUrlPatterns);
    }
    /**
     * 加载Spring 配置
     * @param servletContext ServletContext
     */
    private void initializeSpringConfig(ServletContext servletContext){
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(ServiceConfig.class);
        servletContext.addListener(new ContextLoaderListener(springContext));
    }
    /**
     * 加载Servlet 配置
     * @param servletContext ServletContext
     */
    private void initializeSystemServletConfig(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SystemSpringMvcConfig.class,SwaggerConfig.class);
        Dynamic system  = servletContext.addServlet("system", new DispatcherServlet(context));
        system.addMapping("/system/*");
        system.setLoadOnStartup(1);
        system.setAsyncSupported(true);
    }
    private void initializeNormalServletConfig(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext contextSer = new AnnotationConfigWebApplicationContext();
        contextSer.register(NormalSpringMvcConfig.class,SwaggerConfig.class);
        ServletRegistration.Dynamic workflow  = servletContext.addServlet("workflow", new DispatcherServlet(contextSer));
        workflow.addMapping("/workflow/*");
        workflow.setLoadOnStartup(1);
        workflow.setAsyncSupported(true);
    }
}
