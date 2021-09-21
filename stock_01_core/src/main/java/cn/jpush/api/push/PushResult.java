package cn.jpush.api.push;

import com.google.gson.annotations.Expose;

import cn.jpush.api.common.resp.BaseResult;

public class PushResult extends BaseResult {
    
    @Expose public long msg_id;
    @Expose public int sendno;
    
}

