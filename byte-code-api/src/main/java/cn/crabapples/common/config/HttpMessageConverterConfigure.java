package cn.crabapples.common.config;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * TODO springboot 消息转换配置(主要用于配置fastJson解析)
 *
 * @author Mr.He
 * 2020/1/31 2:02
 * e-mail crabapples.cn@gmail.com
 * qq 294046317
 * pc-name 29404
 */
@Configuration
public class HttpMessageConverterConfigure implements WebMvcConfigurer {

    @Bean
    public FastJsonHttpMessageConverter httpMessageConverters(FastJsonConfig fastJsonConfig) {
        /*
         * 配置了消息转换器之后sse会无法连接
         * 因为配置的MessageConverters处理的媒体类型是 *//*,但是又没有对sse
         */
        FastJsonHttpMessageConverter messageConverter = new FastJsonHttpMessageConverter();
//        messageConverter.setFastJsonConfig(fastJsonConfig);
        return messageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.remove(0);
        // 添加对SSE的支持
//        converters.add(new ServerSentEventHttpMessageReader());
        // 添加对JSON的支持
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new FastJsonHttpMessageConverter());
    }
}