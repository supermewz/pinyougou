package cn.itcast.core.service;

import cn.itcast.common.utils.HttpClient;
import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.pojo.log.PayLog;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PayLogDao payLogDao;

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Autowired
    private IdWorker idWorker;

    @Override
    public Map<String, String> createNative(String name) {
        //String out_trade_no = String.valueOf(idWorker.nextId());
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);


        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HttpClient httpClient = new HttpClient(url);
        HashMap<String, String> param = new HashMap<>();
        httpClient.setHttps(true);
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid", appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        param.put("mch_id", partner);
//        随机字符串	nonce_str	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
//        商品描述	body	是	String(128)	腾讯充值中心-QQ会员充值
        param.put("body", "巅峰搓澡");
//        商品简单描述，该字段请按照规范传递，具体请见参数规定
//
//        商户订单号	out_trade_no	是	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        param.put("out_trade_no", payLog.getOutTradeNo());
//        标价金额	total_fee	是	Int	88	订单总金额，单位为分，详见支付金额
        param.put("total_fee", "1");
//        终端IP	spbill_create_ip	是	String(64)	123.12.12.123	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        param.put("spbill_create_ip","127.0.0.1" );
//        订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//        建议：最短失效时间间隔大于1分钟
//
//        通知地址	notify_url	是	String(256)	http://www.weixin.qq.com/wxpay/pay.php	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "http://cn.itcast");
//        交易类型	trade_type	是	String(16)	JSAPI
        param.put("trade_type", "NATIVE");
//        签名	sign	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	通过签名算法计算得出的签名值，详见签名生成算法
        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
            httpClient.setXmlParam(xml);
            httpClient.post();
            String content = httpClient.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(content);
            if ("SUCCESS".equals(map.get("return_code"))){
                map.put("total_fee",String.valueOf(payLog.getTotalFee()));
                map.put("out_trade_no",payLog.getOutTradeNo() );
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {


        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        HashMap<String, String> param = new HashMap<>();
//        公众账号ID	appid	是	String(32)	wxd678efh567hg6787	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid", appid);
//        商户号	mch_id	是	String(32)	1230000109	微信支付分配的商户号
        param.put("mch_id", partner);
//        微信订单号	transaction_id	二选一	String(32)	1009660380201506130728806387	微信的订单号，建议优先使用
//        商户订单号	out_trade_no	String(32)	20150806125346	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号
        param.put("out_trade_no", out_trade_no);
//        随机字符串	nonce_str	是	String(32)	C380BEC2BFD727A4B6845133519F3AD6	随机字符串，不长于32位。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
//        签名	sign	是	String(32)	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	通过签名算法计算得出的签名值，详见签名生成算法
        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
            httpClient.setXmlParam(xml);
            httpClient.post();
            String content = httpClient.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(content);

            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
