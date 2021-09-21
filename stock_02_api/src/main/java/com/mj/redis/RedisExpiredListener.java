package com.mj.redis;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.LogKit;

import redis.clients.jedis.JedisPubSub;

/**
 * Redis订阅监听
 * @author fhw
 *
 */
public class RedisExpiredListener extends JedisPubSub {

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
    	LogKit.info("onPSubscribe "
                + pattern + " " + subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

        LogKit.info("onPMessage pattern ："
                        + pattern + " | " + channel + "  | " + message);
        
        if(StringUtils.isNotBlank(message)){
        	String[] msgArr = StringUtils.trim(message).split(":");
        	if(msgArr.length <= 1){
        		return;
        	}
        	if(StringUtils.equals("SHOPPING_CART_ID", msgArr[0])){
//        		Integer cartId = Integer.parseInt(msgArr[1]);
//        		ShoppingCartApi.api.timeOutRemove(cartId);
        		/* modify by hongwan.fan 2018-07-25 避免redis订阅事件与用户操作发生冲突
        		ShoppingCart entity = ShoppingCartApi.api.findById(cartId);
        		if(entity != null && StringUtils.equals("Y", entity.getValidFlg())){
        			ShoppingCartApi.api.timeOutRemove(entity.getId());
        		}*/
        	}else if(StringUtils.equals("ORDER_WAIT_TO_PAY", msgArr[0])){
        		Integer orderId = Integer.parseInt(msgArr[1]);
        		Date now = new Date();
//        		BaseOrderApi.api.orderPayTimeOut(orderId, now, "系统跑批");
        	}
        }
         
    }

}