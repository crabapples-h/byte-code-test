package cn.crabapples.common.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * TODO 使用注解方式的拦截器
 *
 * @author Mr.He
 * 2024-11-14 23:50
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name mshe
 */
@Configuration
public class EndPointConfigure extends ServerEndpointConfig.Configurator {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*
         * ServerEndpointConfig 继承至EndpointConfig
         * HandshakeRequest 接口代表一个请求升级到WebSocket的HTTP请求
         * HandshakeResponse 接口代表一个响应升级到WebSocket的HTTP响应
         */
        //获取请求连接中携带的头和参数；
        Map<String, List<String>> parameterMap = request.getParameterMap();
        Map<String, List<String>> headers = request.getHeaders();
        System.err.println(parameterMap);
        System.err.println(headers);
        List<String> secWebsocketProtocol = headers.get("sec-websocket-protocol");
        if (null != secWebsocketProtocol && !secWebsocketProtocol.isEmpty()) {
            String token = secWebsocketProtocol.get(0);
            System.err.println(token);
            response.getHeaders().put("Sec-WebSocket-Protocol", Collections.singletonList(token));

//            headers.remove("sec-websocket-protocol");
        }
//        {sec-websocket-protocol=[authorization], x-forwarded-proto=[ws], user-agent=[Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36]}
//        //接下来可以直接读取这些map中你想要的参数，将值读取出来之后存储到ServerEndpointConfig 中
//        String value = (String)parameterMap.get("authentication").get(0);
//        String value1 = (String) headers.get("Authentication").get(0);
//        System.err.println(value);
//        System.err.println(value1);
//        //...可以验证码头信息的合法性或者直接存储到用户配置里面
//        getUserProperties().put("Authentication", value);//存储到用户配置里面
        super.modifyHandshake(sec, request, response);
    }
}
