package cn.jpush.api.device;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import cn.jpush.api.common.resp.BaseResult;

public class TagListResult extends BaseResult {
    
    @Expose public List<String> tags = new ArrayList<String>();
    
}

