package org.apereo.cas.ext.login.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wudongshen on 2017/3/10.
 * 发送短信验证码Util
 * message.resturl.sms=http://172.18.0.126:8092/portal/mq/produce/shortMessage
 */
public class ValidateCodeSmsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCodeSmsUtil.class);

    // 短信发送地址
    private String smsUrl;

    // 短信模板id
    private String templateId;

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public int sendValidateSmsMessage(String mobileNum, String validateCode){
        ValidateCodeSms validateCodeSms = new ValidateCodeSms(mobileNum, templateId, validateCode);
        return sendSms(validateCodeSms);
    }

    public int sendSms(ValidateCodeSms validateCodeSms){
        HttpURLConnection conn = null;
        OutputStream out = null;

        try {
            URL url = new URL(smsUrl);
            conn = (HttpURLConnection) url.openConnection();
            // 设置允许输出
            conn.setDoOutput(true);

            conn.setDoInput(true);
            // 设置不用缓存
            conn.setUseCaches(false);
            // 设置传递方式
            conn.setRequestMethod("POST");
            //设置连接超时时间
            conn.setConnectTimeout(3000);
            //设置读超时时间
            conn.setReadTimeout(30000);
            // 设置维持长连接
            conn.setRequestProperty("Connection", "Keep-Alive");
            // 转换为字节数组
            byte[] data = JSONObject.toJSON(validateCodeSms).toString().getBytes("UTF-8");
            // 设置文件长度
            conn.setFixedLengthStreamingMode(data.length);
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            // 开始连接请求
            conn.connect();

            out = conn.getOutputStream();
            // 写入请求的字符串
            out.write(data);
            out.flush();
            out.close();

            String resultMsg = conn.getResponseMessage();
            int code = conn.getResponseCode();
            LOGGER.debug("post {},status : {},return msg : {}",smsUrl,code,resultMsg);
            return code;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.debug("发送短信验证码错误：{}", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return 500;
    }



    public static class ValidateCodeSms{

        private String receiverPhoneNumber;

        private String templateId;

        private String code;

        public ValidateCodeSms(){}

        public ValidateCodeSms(String mobileNum, String templateId, String validateCode){
            this.receiverPhoneNumber  = mobileNum;
            this.templateId = templateId;
            this.code = validateCode;
        }

        public String getReceiverPhoneNumber() {
            return receiverPhoneNumber;
        }

        public void setReceiverPhoneNumber(String receiverPhoneNumber) {
            this.receiverPhoneNumber = receiverPhoneNumber;
        }

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String validateCode) {
            this.code = validateCode;
        }

    }
}
