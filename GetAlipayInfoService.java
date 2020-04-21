package cn.com.sinosoft.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetAlipayInfoService {
    private final static Logger logger = LoggerFactory.getLogger(GetAlipayInfoService.class);

    /**
     * 支付宝公钥-从支付宝生活号详情页面获取
     */
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnmQJmOg1tOcIp+zlr01GAm7yq3wJSXGh4sJkUo7bSlSnGJvYNoIdJm/vK7PG2FRpWD7BePwNanqrGfQUDKED81DnG88SCiZp1smnpfRA+bpvLXKzsH6WoJ2pu0QHg5JIMauGhvrLJLhuh216Z+ISg5yjMNRhbPB00cSk2M5FU/SxE1K/L8cn7FIlwL3SsClRU15dXJrNvOIo1WKspWgZqI40QXaE/hvJ5BsxsocYX4hPqZ062mNi2vPlhtj5WL0pkzJmThkLMm+jAdQYpmSTTLKyZJTImtguVY4fczEq0oV/1O+yy1rEVjlEaT5Vr3IByjCKKf/c2lTquKYiOYe9WwIDAQAB";

    /**
     * 签名编码-视支付宝服务窗要求
     */
    public static final String SIGN_CHARSET = "GBK";

    /**
     * 字符编码-传递给支付宝的数据编码
     */
    public static final String CHARSET = "GBK";

    /**
     * 签名类型-视支付宝服务窗要求
     */
    public static final String SIGN_TYPE = "RSA2";

    /**
     * 开发者账号PID
     */
    public static final String PARTNER = "";

    /**
     * 服务窗appId
     */
    //TODO !!!! 注：该appId必须设为开发者自己的生活号id
    public static final String APP_ID = "2016111502854066";

    //TODO !!!! 注：该私钥为测试账号私钥  开发者必须设置自己的私钥 , 否则会存在安全隐患
    public static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCN1W9fQOD9PTIrmiocOnxfbg0PSr+GsEAAKAkolDV23tDgiF6fD9KvdRUx6+VDQYh0AI+EoUkT013Sr3b5fcMfIva64enOxaVFo6kMjP4BokYPV3SOLX8y3pU1h9+yf0yTKOMtjjJrpfQ7zWeSDTAA/2aWJCJN7bNFpa3uSvKceFcUTS2tgiGpoyVR9nPUcNOutMJZAlK4l/vZS1qqcVNFWxM+EeI4Yjp04camRnVss2Fw1urMg8zOsaHfV1wIm7Ff8my7d//pns9kdkgCFOoMj0/17iKq0463UH/c7JapoqIOpWhcLGYm/ShjunxVYLzJf7Fb+wfghBEFKyO+KMdjAgMBAAECggEAKxjx0JXcR9byicE8jntPE4hTO5RRAlJIAEQqFESEwmwOOEv3xImAUYRYPmUZswgUuHr7ISgJFpchaUWb90+5nJp5Mmw3KXdxMun6EjA9dq2icEL4zcMRZZmw6SUyWgDIeebwsQUGWSOCRTGzCPQs5AC3YWP1cIyL7tvbMA9iDR0nHZ/qvCDe6a1I4ccD+pRwRRcw3Vasc/uoRgAPcc4W7dv9OU1Bpqyx3b2pD6/+yn6AbLghNmy1XjgTt06fQZw8nlnQuoofh1XxAzQKduKUG7CzPyslwMOH8wAUJe1AID3PWxzf8tGbCJVk/zgUnTKyRJDw6MnJLm+eYqZFaIH00QKBgQDa5fUPYw6pTWgYnavpemcTVh1d+zhiqSF2ZhOuVsOVXVACvy9kfssIayzys2PwcM9hojlogqDMhwm99ZHte9IwZh4X7OYz3c0oCIGB1ag7F6VgUyAdhGMl+5XGrtH3B1Wozk4wnV02kxJFKa3WH8XyZZJOhhZPMjywypjhBXjNvwKBgQCl36GEcDMAGt80T4HSPl5AQxqyScq8yP0qBxmtEv81ic1e8poNXuewMaqpdSWKbaSpjuW7E3v0sr9W0lGfzvJoB3NPvMRRQm9bmeexFDeuYzo31TfVOhDBtfG0Ft6QIxXuuU5O6hyTluT6xQ1yzCyt3HFddyq0t++Jv3Xz2243XQKBgQDVBDMWZi8+UjWavtUswBDOoF6ztmWInU4TCgLdBVIPaF8UTTixczHhX2q+RBRdR2qIeHWlXnlcj4zLbs4zbOt+beGbJs5eatnau2xMDL9JwhkNQ9dc4hgaPlninv10h460Vw9//6NWGIkPBSBF32WiHXXjNFlBeIHXCVD8qXaFwQKBgGE8USCj7SG4nvO5T+8BZJrjO3kcTN6SK+ZBN0oAAt9NorhwGuKPqd3dn5+q7pDRaV+ERyN9boKpbgTlUbgOxoc5Jt2tWKASA9l8xRdHNMTjojMsqIg0e7IyiDf72AhCqQ7CTidwkcPrBtwCO7n5+o8I1OPngX9i++aR+AE1tsx5AoGBANEr9ezQdq7ye9mfMa5MBlh+7OZgvmRXjY6UfrzAHXOOFktOLVdN9Ocb/vA6fT+GFwfFaHi1LmQwMpPMUVTiUdfbRBa2apVv5ibDtGCJmWdS42+FIvcr76VAKG/KIje6u9DWu8jG7uWtt2HaKZ4sfj/N15Lz9TOaI7r1KCBYuf3z";

    //TODO !!!! 注：该公钥为测试账号公钥  开发者必须设置自己的公钥 ,否则会存在安全隐患
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjdVvX0Dg/T0yK5oqHDp8X24ND0q/hrBAACgJKJQ1dt7Q4Ihenw/Sr3UVMevlQ0GIdACPhKFJE9Nd0q92+X3DHyL2uuHpzsWlRaOpDIz+AaJGD1d0ji1/Mt6VNYffsn9MkyjjLY4ya6X0O81nkg0wAP9mliQiTe2zRaWt7krynHhXFE0trYIhqaMlUfZz1HDTrrTCWQJSuJf72UtaqnFTRVsTPhHiOGI6dOHGpkZ1bLNhcNbqzIPMzrGh31dcCJuxX/Jsu3f/6Z7PZHZIAhTqDI9P9e4iqtOOt1B/3OyWqaKiDqVoXCxmJv0oY7p8VWC8yX+xW/sH4IQRBSsjvijHYwIDAQAB";
    /**
     * 支付宝网关
     */
    public static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";

    /**
     * 授权访问令牌的授权类型
     */
    public static final String GRANT_TYPE = "authorization_code";

    /**
     * 生活号根据code,获取用户openid及authToken
     *
     * @param auth_code
     * @param comFlag
     * @return
     */
    public String getAlipayInfo(String auth_code, String comFlag) {

        logger.info("======生活号网页授权信息=========code======" + auth_code);
        String userId = null;
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY, APP_ID, PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.user.userinfo.share
            AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
            oauthTokenRequest.setCode(auth_code);
            oauthTokenRequest.setGrantType(GRANT_TYPE);
            AlipaySystemOauthTokenResponse execute = alipayClient.execute(oauthTokenRequest);
            userId = execute.getUserId();
        } catch (AlipayApiException alipayApiException) {
            //自行处理异常
            alipayApiException.printStackTrace();
        }
        return userId;
    }
}
