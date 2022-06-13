package com.ginfon.scada.config;


public class Constants {
	
	
    // 金峰云
    public static final String CHANNEL_GINFON_YUN = "GinfonYun";

    /**
     * 	WCS和金峰的连接状态的请求URL。
     */
    public static final String REQUEST_URL_FOR_SERVER_CONN_STATUS = "/request/serverconn/status";
    
    /**
     * 	控制线体启停状态的请求URL，对应功能码103。
     */
    public static final String REQUEST_URL_FOR_CONTROL_LINE_DEVICE = "/request/control/linedevice";
    
    /**
     * 	控制堵包功能启停的请求URL，对应功能码203。
     */
    public static final String REQUEST_URL_FOR_CONTROL_BLOCKING = "/request/control/blocking";
    
    /**
     * 	请求堵包功能启用状态的URL，对应功能码202。
     */
    public static final String REQUEST_URL_FOR_BLOCKING_STATUS = "/request/blocking/status";
    
    /**
     * 	请求线体运行状态的URL，对应功能码201。
     */
    public static final String REQUEST_URL_FOR_LING_STATUS = "/request/line/status";
    
    
    /**
     * 	设置线体的休眠时间，对应功能码205。
     */
    public static final String REQUEST_URL_FOR_SET_SLEEP_TIME = "/request/control/line/sleeptime";
    
    /**
     * 	设置线体的启动间隔时间，对应功能码206。
     */
    public static final String REQUEST_URL_FOR_SET_START_TIME = "/request/control/line/starttime";
    
    /**
     * 	查询已经设定的休眠时长，对应功能码207。
     */
    public static final String REQUEST_URL_FOR_SLEEP_TIME = "/request/line/sleeptime";
    
    /**
     * 	查询已经设定的启动时长，对应功能码208。
     */
    public static final String REQUEST_URL_FOR_START_TIME = "/request/line/starttime";
    
    /**
     * 	所有控制类型请求的URL，具体请求的功能按传递的参数做处理,211功能码。
     */
    public static final String REQUEST_URL_FOR_CONTROL_COMMAND = "/request/control/command";
    
    
    /**
     * 	WCS和金峰的连接状态的推送URL。
     */
    public static final String TOPIC_URL_FOR_SERVER_CONN_STATUS = "/topic/serverconn/status";
    
    /**
     * 	线体运行状态的推送URL。对应功能码301。
     */
    public static final String TOPIC_URL_FOR_LINE_STATUS = "/topic/line/status";
    
    /**
     * 	堵包功能状态的瑞松URL。对应功能码204。
     */
    public static final String TOPIC_URL_FOR_BLOKCING_STATUS = "/topic/blocking/status";
    
    /**
     * 	对应功能码209。
     */
    public static final String TOPIC_URL_FOR_SLEEP_TIME = "/topic/line/sleeptime";
    
    /**
     * 	对应功能码210。
     */
    public static final String TOPIC_URL_FOR_START_TIME = "/topic/line/starttime";
    
    /**
     * 	SCADA异常状态推送URL
     */
    public static final String TOPIC_URL_FOR_SCADA_ERROR = "/topic/scada/error";
    
    
    //==========================================================================================

    // 格口状态推送URL
    public static String TOPIC_URL_FOR_CHUTE_STATUS = "/topic/chute/status";
    // 急停按钮状态推送URL
    public static String TOPIC_URL_FOR_ESTOP_STATUS = "/topic/estop/status";
    // 马达故障状态推送URL
    public static String TOPIC_URL_FOR_MOTOR_STATUS = "/topic/motor/status";
    // 小车故障状态推送URL
    public static String TOPIC_URL_FOR_CART_STATUS = "/topic/cart/status";
    // 供件台状态推送URL
    public static String TOPIC_URL_FOR_INDUCTIONUNIT_STATUS = "/topic/inductionunit/status";
    // 小车组通讯状态推送URL
    public static String TOPIC_URL_FOR_CARTGROUP_STATUS = "/topic/cartgroup/status";
    // 系统状态推送URL
    public static String TOPIC_URL_FOR_SYSTEM_STATUS = "/topic/system/status";
    // 供件台操作推送URL
    public static String TOPIC_URL_FOR_INDUCTIONUNITOPER_OPER = "/topic/inductionunit/oper";
    // 格口组状态推送URL
    public static String TOPIC_URL_FOR_CHUTEGROUP_STATUS = "/topic/chutegroup/status";
    // 灰度仪状态推送URL
    public static String TOPIC_URL_FOR_GRAYSCALESCANNER_STATUS = "/topic/grapscale/scanner/status";
    // 灰度检测配套光电状态推送URL
    public static String TOPIC_URL_FOR_GRAYSCALEPHOTOELECTRIC_STATUS = "/topic/grapscale/photoelectric/status";
    // PLC通讯状态推送URL
    public static String TOPIC_URL_FOR_PLCCONN_STATUS = "/topic/plcconn/status";
    // 客户上位机通讯状态推送URL
    public static String TOPIC_URL_FOR_SFHOSTCONN_STATUS = "/topic/sfhost/status";
    // 相机秤状态推送URL
    public static String TOPIC_URL_FOR_CAMERASCALES_STATUS = "/topic/camerascales/status";
    // 电流电压推送URL
    public static String TOPIC_URL_FOR_CURRENTVOLTAGE_STATUS = "/topic/plcconn/status";
    // 环线速度推送URL
    public static String TOPIC_URL_FOR_LOOPLINE_SPEED = "/topic/loopline/speed";

}
