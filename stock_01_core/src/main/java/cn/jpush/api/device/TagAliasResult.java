package cn.jpush.api.device;

import java.util.List;

import com.google.gson.annotations.Expose;

import cn.jpush.api.common.resp.BaseResult;

public class TagAliasResult extends BaseResult {

    @Expose public List<String> tags;
    @Expose public String alias;
        
}

