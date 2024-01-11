package com.ruifox.util;
import com.alibaba.fastjson.JSONObject;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/10/16 16:07
 */
public class JsonUtil {
        public static String resp(Integer code,String data){
            JSONObject json=new JSONObject();
            json.put("code",code);
            json.put("data",data);
            return json.toJSONString();
        }

    public static String successResp(String data){
        return resp(200,data);
    }
    public static String failResp(String data){
        return resp(500,data);
    }
}
