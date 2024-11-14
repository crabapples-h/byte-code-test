//package cn.crabapples.common.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//import javax.websocket.server.ServerEndpointConfig;
//
////@Configuration
//public class WebSocketConfigureBAK extends ServerEndpointConfig.Configurator implements ApplicationContextAware {
//
//    private static volatile BeanFactory context;
//
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
//
//    @Override
//    public <T> T getEndpointInstance(Class<T> clazz) {
//        return context.getBean(clazz);
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        context = applicationContext;
//    }
//}
