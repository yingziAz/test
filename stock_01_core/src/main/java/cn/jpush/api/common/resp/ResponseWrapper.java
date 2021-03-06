package cn.jpush.api.common.resp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ResponseWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseWrapper.class);
    private static final int RESPONSE_CODE_NONE = -1;
    
    private static Gson _gson = new Gson();
    
    public int responseCode = RESPONSE_CODE_NONE;
    public String responseContent;
    
    public ErrorObject error;     // error for non-200 response, used by new API
    
    public int rateLimitQuota;
    public int rateLimitRemaining;
    public int rateLimitReset;
	
    public void setRateLimit(String quota, String remaining, String reset) {
        if (null == quota) return;
        
        try {
            rateLimitQuota = Integer.parseInt(quota);
            rateLimitRemaining = Integer.parseInt(remaining);
            rateLimitReset = Integer.parseInt(reset);
            
            LOG.debug("JPush API Rate Limiting params - quota:" + quota + ", remaining:" + remaining + ", reset:" + reset);
        } catch (NumberFormatException e) {
            LOG.debug("Unexpected - parse rate limiting headers error.");
        }
    }
    
    public void setErrorObject() {
        try {
            error = _gson.fromJson(responseContent, ErrorObject.class);
        } catch (JsonSyntaxException e) {
            int index = responseContent.indexOf("error");
            if( -1 != index ) {
                int from = responseContent.indexOf("{", index);
                int to = responseContent.indexOf("}", from);
                String errorStr = responseContent.substring(from, to + 1);
                error = new ErrorObject();
                try {
                    error.error = _gson.fromJson(errorStr, ErrorEntity.class);
                } catch (JsonSyntaxException e1) {
                    LOG.error("unknown response content:" + responseContent, e);
                }
            }
        }
    }
    
    public boolean isServerResponse() {
        if (responseCode / 100 == 2) return true;
        if (responseCode > 0 && null != error && error.error.code > 0) return true;
        return false;
    }
    
	@Override
	public String toString() {
		return _gson.toJson(this);
	}
	
	public class ErrorObject {
	    public long msg_id;
	    public ErrorEntity error;
	}
	
	public class ErrorEntity {
	    public int code;
	    public String message;
	    
	    @Override
	    public String toString() {
	        return _gson.toJson(this);
	    }
	}
	
}
