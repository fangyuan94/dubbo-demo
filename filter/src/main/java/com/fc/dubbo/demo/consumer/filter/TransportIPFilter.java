package com.fc.dubbo.demo.consumer.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.rpc.*;
import org.springframework.util.StringUtils;

/**
 *获取请求ip
 */
@Activate(order = 1)
@Slf4j
public class TransportIPFilter implements Filter {

    public final static  String REQUEST_IP_KEY = "request_ip_key";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        String attachment = invocation.getAttachment(REQUEST_IP_KEY);

        log.info("请求调用主机ip===============》{}",attachment);

        return invoker.invoke(invocation);
    }
}
