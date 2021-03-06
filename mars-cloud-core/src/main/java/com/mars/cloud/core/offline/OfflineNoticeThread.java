package com.mars.cloud.core.offline;

import com.mars.cloud.constant.MarsCloudConstant;
import com.mars.cloud.core.notice.model.NotifiedModel;
import com.mars.cloud.core.util.NoticeUtil;

/**
 * 下线通知线程，用来通知被下线的服务 将自己移除已广播名单
 */
public class OfflineNoticeThread implements Runnable {

    /**
     * 被下线的服务
     */
    private String offlineHost;

    /**
     * 本服务
     */
    private NotifiedModel notifiedModel;

    public void setOfflineHost(String offlineHost) {
        this.offlineHost = offlineHost;
    }

    public void setNotifiedModel(NotifiedModel notifiedModel) {
        this.notifiedModel = notifiedModel;
    }

    @Override
    public void run() {
        try {
            /*
              既然发生了误判，那么这个服务肯定是有一定的问题的，比如接口不通之类的
              所以，这里如果通知失败，就重试，直到满5次为止
            */
            for (int i = 0; i < 5; i++) {

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(offlineHost);
                stringBuffer.append("/");
                stringBuffer.append(MarsCloudConstant.REMOVE_NOTIFIED);

                boolean isSuccess = NoticeUtil.removeNotified(stringBuffer.toString(), notifiedModel);
                if (isSuccess) {
                    return;
                }

                Thread.sleep(1000L);
            }
        } catch (Exception e) {
            /*
                这本来就是个保险机制，请求出现异常是正常现象，要是通了才不正常
                所以这里 什么都不处理，静静的就好
             */
        }
    }
}
