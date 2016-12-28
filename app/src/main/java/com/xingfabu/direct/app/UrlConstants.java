package com.xingfabu.direct.app;

/**
 * Created by 郭平 on 2016/3/30 0030.
 */
public class UrlConstants {

    public static final String sign = "Tk2JUQ5JUEyRSU4Q";

    public static final String REAL_SERVER = "http://api.xingfabu.cn/";//正式器地址
    public static final String TEST_SERVER = "http://dev.xingfabu.cn/";//测试服务器
    public static final String NEW_SERVER = REAL_SERVER;
    public static final String ACTION_SENDVERIFyCODE = NEW_SERVER + "index.php/c_member/sendVerifyCode";
    public static final String ACTION_REGISTER = NEW_SERVER + "index.php/c_member/register";
    public static final String ACTION_LOGIN = NEW_SERVER + "index.php/c_member/login";
    public static final String CHANGE_PASSWORD = NEW_SERVER + "index.php/c_member/forgetPasswd";
    public static final String SUBSCRIBE = NEW_SERVER + "index.php/v3/webinar/subscibe";//订阅string f_uid   关注id;string page   页数
    public static final String RECOMMEND = NEW_SERVER + "index.php/v3/webinar/lists";//推荐
    public static final String TOURIST = NEW_SERVER +"index.php/c_member/tourist";//游客token
    public static final String GET_VHALL_TOKEN = NEW_SERVER + "index.php/c_webinar/getToken";//获取微吼token
    public static final String VIDEO_DETAIL = NEW_SERVER + "index.php/c_webinar/detail";//视频内容详情sid webinar_id
    public static final String GET_TOURIST_TOKEN = NEW_SERVER + "index.php/c_member/tourist";//获取游客token
    public static final String GET_USER_INFO = NEW_SERVER + "index.php/c_member/userInfo";//获取用户信息
    public static final String VersionUpdate = NEW_SERVER + "index.php/v3/webinar/version";//版本升级
    public static final String LOGOUT = NEW_SERVER + "index.php/c_member/logout";
    public static final String ABOUT_US = NEW_SERVER + "html/gywm.html";
    public static final String HELP_CENTER = NEW_SERVER + "html/help_center.html";
    public static final String USER_PROTOCOL = NEW_SERVER + "html/agreement.html";

    public static final String PLAY_RECORD = NEW_SERVER + "index.php/v3/webinar/playLogs";
    public static final String CLEAN_PLAY_RECORD = NEW_SERVER + "index.php/v3/webinar/clearPlayLogs";//要删除一个就传sid
    public static final String ADD_COLLECTION = NEW_SERVER + "index.php/c_webinar/enjoy";
    public static final String COLLECTION_LIST = NEW_SERVER + "index.php/c_member/enjoyVideo";
    public static final String SYSTEM_MESSAGE = NEW_SERVER + "index.php/v3/webinar/announce";

    /**
     * 修改个人信息
     * string name 名称
     * string sex 1 男 2 女
     * string pic 头像地址
     * string idol 偶像
     * string sign 签名
     * string city_name 城市名
     * @param string birthday 生日
     */
    public static final String SAVE_USER_INFO = NEW_SERVER + "index.php/c_member/saveUserInfo";
    public static final String BANNER = NEW_SERVER + "index.php/c_webinar/adver";
    /**
     * 获取弹幕
     * webinar_id 微吼直播id
     */
    public static final String GETBARRAGE = NEW_SERVER + "index.php/c_webinar/getBarrage";
    /*
     * 弹幕存放
     * @param string webinar_id 微吼直播id
     * @param string video_time 当前视频时间
     * @param string content 内容
     */
    public static final String SAVEBARRAGE = NEW_SERVER + "index.php/c_webinar/saveBarrage";
    /*
     * 自动会话
     * @param string webinar_id 微吼直播id
     * @param string num  次数
     * @param string total  回放总时长
     * @param string current  直播 当前时间
     */
    public static final String ROBOT = NEW_SERVER + "index.php/c_webinar/robot";

    public static final String GET_OTHERINFO = NEW_SERVER + "index.php/c_member/otherInfo";

    //webinar_id
    public static final String GET_SUPPORT = NEW_SERVER + "index.php/v3/webinar/getSupport";
    //webinar_id
    //support
    //退出上传总数
    public static final String SAVE_SUPPORT = NEW_SERVER + "index.php/v3/webinar/saveSupport";

    public static final String SAVE_CLICKS = NEW_SERVER + "index.php/v3/webinar/saveClicks";

    public static final String BINDING_PHONENUM = NEW_SERVER + "index.php/c_member/binding";
    /*
     * 第三方登陆
     * @param string $type 1 qq  2 微信 3 微博
     * @param string $userName 用户名
     * @param string $usid 唯一标识
     * @param string $iconUrl 用户头像
     */
    public static final String THIRDPARTY_LANDED = NEW_SERVER + "index.php/c_member/thirdPartyLanded";
    public static final String SHARE_URL = "http://www.xingfabu.cn/xiazai2/index.html";
    //获取 现场 发布会...
    public static final String GETCOLUMN = NEW_SERVER + "c_webinar/getColumn";
    //传type page
    public static final String DISCOVER = NEW_SERVER + "c_webinar/discover";

    //keyword关键字  anonymity 1无痕 page
    public static final String SEARCH = NEW_SERVER + "index.php/v3/webinar/search";

    public static final String HOT = NEW_SERVER + "index.php/v3/webinar/ranking";
    public static final String RANK = NEW_SERVER + "index.php/v3/webinar/marketVideo";

    public static final String SEARCH_LOG = NEW_SERVER + "index.php/v3/webinar/searchLog";
    public static final String CLEAR_SEARCH_LOG = NEW_SERVER  + "index.php/v3/webinar/clearSearchLog";
    //积分商城
    public static final String JIFEN_SHOP = NEW_SERVER + "index.php/v3/webinar/buildUrl";

    //每日分享
    public static final String DAILYSHARE = NEW_SERVER + "index.php/v3/webinar/dailyShare";
    //霸屏榜活动 获取 活动是否关闭、剩余点赞数
    public static final String SUPPORT_SURPLUS = NEW_SERVER + "index.php/v3/webinar/supportSurplus";
    public static final String ACSUPPORT = NEW_SERVER + "index.php/v3/webinar/acSupport";
    //霸屏榜广告
    public static final String HOT_ADVER = NEW_SERVER + "index.php/v3/webinar/getMarketAdver";
    //"传page"
    public static final  String STAR_INFO = NEW_SERVER + "index.php/v3/starNews/lists";
    //"传 aid （作者id）  page"作者详情页
    public static final String AUTHOR_DETAIL = NEW_SERVER + "index.php/v3/starNews/author";
    public static final String SHARE_RANK = NEW_SERVER + "index.php/v3/starNews/shareRank";
    //传 webinar_id(霸屏榜以外的分享)
    public static final String SIMPLE_SHARE = NEW_SERVER + "index.php/v3/webinar/webinarShare";

    //	payType
    //	note  注释
    //	price  金额
    //	sid
    //	subject
    public static final String CREATE_ORDER = NEW_SERVER + "index.php/c_pay/createOrder";

    public static final String APP_ID = "wx836b98514de0fc3e";
    public static final String partnerId = "1298706801";

    // 商户PID
    public static final String PARTNER = "2088121344339734";
    // 商户收款账号
    public static final String SELLER = "xingfabu@xingfabu.cn";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALws7RtXKn/HLPjy1y0J/fcL4kZ3gKPpocaW9IeBXgsFJLVlibekD8yu4UP4EnxSxAfETyXrgU08mYaV/SXgPfphjHmxAhDCtObEfqkkST14Xjr43655rP76CZHtoF9mabu81QMDF8aViZ6r29tAuHbaG0GACz5YzVKq17DTc/3DAgMBAAECgYAY1i0tcKHRX66V5Sd1cF4u30isWFWITOvnVEFOKUsKVVF1B8PXkzXqpCJjYDjAZAQvqj9wF9dnJFp3IxDYkPJdAbtPQVHBF6Ol9GeMrI7xfoF1e6sqeMAk83BDGN8RwiE/FcJpcBH6GNjOHj1FWFgQvbroz3942GSAEE+Bs2bRYQJBAOlCiXl81yWWS0nZ3Rlz012EdUHzU3GtIt3iLcNhDIIn10iE+y5MacetQi6XH4SC6s3lnCAEBqCsOep9vBlDkfsCQQDOhTfg1dSm+4TtA9NR0I8ssrTuIQf9NWCIM0XTaNtowXjBsJgu5mhhkv7Rkv7tl3nDiJ2MxJ4utS2uT5jlQ8DZAkEA4YPbp6ID26oXthm1AkOSCSrjttRKS3AJcp2WgvuT/U4JfGpc8eEcr9kZUDP5W8K6wj+IFwftZG++OrA/J+nE+wJBAK82kyvr/xkUt7gm0LjPC0nO7HZEC/UlvhqP9aPhiJUd4AfkfZj83n46KntADY0iQKpGU/TEM7hI+tKysXOBwjECQAZH4R8aBHUYMJf3Rv6YKRKORhzWSgitYBQ1moYp7rtHQNR0tX7wtBSDeaVxDYSn4SRYcWJkdizMny2qxMAr444=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    public static final String Pay_Result = NEW_SERVER + "index.php/c_alipay/notify";
    public static final String wx_Result = NEW_SERVER + "index.php/c_wxpay/notify";
    //platform 2
    public static final String CONTROL_REDPACKET = NEW_SERVER + "index.php/c_member/setting";

    public static final String LAUNCHER_AD = NEW_SERVER + "index.php/c_webinar/advertisements";

    /**
     * 是否第一次启动
     */
    public static final String KEY_FRITST_START = "KEY_FRIST_START";

    //广告是否已经缓存
    public static final String IsAdvertisementCache = "IsAdvertisementCache";

    //广告Json已经缓存
    public static final String IsAdvertisementJsonCache = "AdvertisementCache";

    //广告Json已经缓存时间
    public static final String IsAdvertisementJsonCacheTime = "IsAdvertisementCacheTime";

    public static final String IsAdvertisementUrlCache = "IsAdvertisementCacheUrl";

    //sid
    public static final String GET_RELATED = NEW_SERVER + "index.php/v3/webinar/webinarRelated";


//    market_id  广告id
//    type 1 首页  2 分类  3 霸屏榜 4 暂停时广告
//    type = 4的时候  market_id  咱们传webinar_id

    public static final String ADVER_CLICK = NEW_SERVER + "index.php/v3/webinar/adverClick";
}
