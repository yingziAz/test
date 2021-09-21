package com.mj.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.ext.kit.RetKit;
import com.jfinal.kit.Ret;
import com.mj.constant.CoreConstant;

/**
 * 防止表单重复提交 Interceptor
 * @author daniel.su
 *
 */
public class FormTokenInterceptor implements Interceptor{

    @Override
    public void intercept(Invocation inv) {
        Controller ci = inv.getController();
        String formTokenSuffix = ci.getPara("formTokenSuffix");
		boolean token = com.jfinal.token.TokenManager.validateToken(ci, CoreConstant.FORM_TOKEN + formTokenSuffix);
        if(token){
            inv.invoke();
        }else{
            if(ci.getRequest().getHeader("x-requested-with")!=null 
                    && ci.getRequest().getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
                Ret ret = RetKit.fail("表单重复提交");
                ci.renderJson(ret);
            }else{
                ci.renderText("表单重复提交");
            }
        }
    }
}