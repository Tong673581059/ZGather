package com.zcolin.zupdate;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.zcolin.frame.app.BaseApp;
import com.zcolin.frame.app.BaseFrameActivity;
import com.zcolin.frame.app.FramePathConst;
import com.zcolin.frame.http.ZHttp;
import com.zcolin.frame.http.response.ZFileResponse;
import com.zcolin.frame.http.response.ZStringResponse;
import com.zcolin.frame.util.AppUtil;
import com.zcolin.frame.util.GsonUtil;
import com.zcolin.frame.util.LogUtil;
import com.zcolin.frame.util.NetworkUtil;
import com.zcolin.frame.util.SystemDownloadApkUtil;
import com.zcolin.frame.util.ToastUtil;
import com.zcolin.gui.ZConfirm;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 更新管理
 */
public class ZUpdate {
    private boolean isOnlyWifi            = false;          //是否只有wifi状态下提示更新
    private boolean isUseSystemDownloader = false;          //是否使用系统下载器下载
    private boolean isDownloadSilent      = false;          //静默下载
    private boolean isSilent              = true;           //是否静默检测
    private HashMap<String, String>       mapUpdateApply;      //检测更新请求报文参数
    private Object                        objectUpdateApply;//检测更新请求报文参数
    private Class<? extends ZUpdateReply> updateReplyClass; //检测更新回复报文class
    private String                        updateUrl;        //检测更新地址

    public static ZUpdate instance() {
        return new ZUpdate();
    }

    /**
     * 设置是否只有wifi下检测（强制更新除外）
     */
    public ZUpdate setOnlyWifi(boolean isOnlyWifi) {
        this.isOnlyWifi = isOnlyWifi;
        return this;
    }

    /**
     * 是否自动静默更新（不弹进度条，不弹错误信息）
     */
    public ZUpdate setSilent(boolean isSilent) {
        this.isSilent = isSilent;
        return this;
    }

    /**
     * 设置检测更新请求参数，如果未设置，使用默认{"versionName":versionName,"versionCode":"versionCode", "terminal":"android"} 检测
     */
    public ZUpdate setUpdateApply(HashMap<String, String> apply) {
        this.mapUpdateApply = apply;
        return this;
    }

    /**
     * 设置检测更新请求参数，使用实体类进行参数封装。
     */
    public ZUpdate setUpdateApply(Object apply) {
        this.objectUpdateApply = apply;
        return this;
    }

    /**
     * 设置返回报文的class信息。需实现ZUpdateReply接口
     */
    public ZUpdate setUpdateReplyClass(Class<? extends ZUpdateReply> clazz) {
        this.updateReplyClass = clazz;
        return this;
    }

    /**
     * 设置自检测更新地址
     */
    public ZUpdate setUpdateUrl(String url) {
        this.updateUrl = url;
        return this;
    }

    /**
     * 是否使用系统下载器下载
     */
    public ZUpdate setUserSystemDownloader(boolean isUseSystemDownloader) {
        this.isUseSystemDownloader = isUseSystemDownloader;
        return this;
    }

    /**
     * 是否静默下载
     */
    public ZUpdate setDownloadSilent(boolean isDownloadSilent) {
        this.isDownloadSilent = isDownloadSilent;
        return this;
    }

    /**
     * 检查版本更新, 使用默认新版本监听器
     */
    public void checkVersion(final BaseFrameActivity acty) {
        checkVersion(acty, null);
    }

    /**
     * 检查版本更新, 如果有新版本，自己定义之后的操作
     */
    public void checkVersion(final BaseFrameActivity acty, OnNewVersionListener listener) {
        if (!NetworkUtil.isNetworkAvailable(BaseApp.APP_CONTEXT)) {
            if (!isSilent) {
                ToastUtil.toastShort("网络连接不可用，请开启网络！");
            }
            listener.onUpdateError(DownloadErrorStatus.ERROR_NO_NETWORK);
            return;
        }

        ZStringResponse response = new ZStringResponse(isSilent ? null : acty) {
            @Override
            public void onSuccess(Response response, String res) {
                ZUpdateReply reply;
                if (updateReplyClass == null) {
                    reply = GsonUtil.stringToBean(res, ZDefReply.class);
                } else {
                    reply = GsonUtil.stringToBean(res, updateReplyClass);
                }

                if (reply != null) {
                    if (!isSilent && !reply.isUpdate()) {
                        listener.onNewVersion(reply);
                        ToastUtil.toastShort("当前是最新版本");
                    } else if (reply.isUpdate()) {
                        if (listener == null || !listener.onNewVersion(reply)) {
                            showNewUpdateDialog(acty, reply, listener);
                        }
                    }
                } else if (!isSilent) {
                    ToastUtil.toastShort("数据转换错误");
                    if (listener != null) {
                        listener.onUpdateError(DownloadErrorStatus.ERROR_DATE_CONVERT);
                    }
                }
            }

            @Override
            public void onError(int code, Call call, Exception e) {
                if (!isSilent) {
                    ToastUtil.toastShort(getError(e, code));
                }
                if (listener != null) {
                    listener.onUpdateError(DownloadErrorStatus.ERROR_OTHER);
                }
            }
        };

        if (objectUpdateApply != null) {
            ZHttp.get(updateUrl, objectUpdateApply, response);
        } else if (this.mapUpdateApply != null) {
            ZHttp.get(updateUrl, mapUpdateApply, response);
        } else {
            ZHttp.get(updateUrl, getDefUpdateApply(), response);
        }
    }

    /**
     * 获取默认更新参数
     */
    private HashMap<String, String> getDefUpdateApply() {
        HashMap<String, String> mapApply = new HashMap<>(3);
        mapApply.put("versionName", AppUtil.getVersionName(BaseApp.APP_CONTEXT));
        mapApply.put("versionCode", String.valueOf(AppUtil.getVersionCode(BaseApp.APP_CONTEXT)));
        mapApply.put("terminal", "android");
        return mapApply;
    }

    private String getError(Exception ex, int code) {
        String str;
        if (ex instanceof SocketTimeoutException || code == 0) {
            if (!NetworkUtil.isNetworkAvailable(BaseApp.APP_CONTEXT)) {
                str = "当前无网络连接，请开启网络！";
            } else {
                str = "连接服务器失败, 请检查网络或稍后重试";
            }
        } else if (ex instanceof JsonSyntaxException) {
            str = "json conversion failed, code is : -1";
        } else {
            str = LogUtil.ExceptionToString(ex);
        }
        return str;
    }

    /**
     * 处理有新版本需要更新业务
     *
     * @param updateReply 更新信息，服务器回传回来的
     */
    public void showNewUpdateDialog(final BaseFrameActivity acty, final ZUpdateReply updateReply, OnNewVersionListener listener) {
        //界面已经销毁，则不再执行后续操作
        if (acty == null || acty.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && acty.isDestroyed())) {
            return;
        }

        if (updateReply.isForce()) {
            ZConfirm.instance(acty)
                    .setTitle("版本更新  " + updateReply.versionName())
                    .setMessage("必须完成本次更新才能继续使用本系统\n\n" + updateReply.updateMessage())
                    .setCancelBtnText("退出系统")
                    .setOKBtnText("立即升级")
                    .setIsCancelAble(false)
                    .addSubmitListener(() -> {
                        if (listener == null || !listener.onUpdateConfirm(updateReply)) {
                            if (isUseSystemDownloader) {
                                downLoadAppUseSystemDownloader(acty, updateReply.downLoadUrl());
                            } else {
                                downLoadApp(acty, updateReply.downLoadUrl(), listener);
                            }
                        }
                        return true;
                    })
                    .addCancelListener(() -> {
                        AppUtil.quitSystem();
                        return true;
                    })
                    .show();
        } else if (!isSilent && !(isOnlyWifi && NetworkUtil.isMobileConnect(acty))) {
            ZConfirm zConfirm = ZConfirm.instance(acty).setTitle("版本更新  " + updateReply.versionName())
                    .setMessage(updateReply.updateMessage())
                    .setIsCancelAble(false)
                    .setCancelBtnText("暂不升级")
                    .addCancelListener(() -> {
                        if (listener != null) {
                            listener.onUpdateCancel();
                        }
                        return true;
                    })
                    .setOKBtnText("立即升级")
                    .addSubmitListener(() -> {
                        if (isUseSystemDownloader) {
                            downLoadAppUseSystemDownloader(acty, updateReply.downLoadUrl());
                        } else {
                            downLoadApp(acty, updateReply.downLoadUrl(), listener);
                        }
                        return true;
                    });
            zConfirm.show();
        }
    }

    /**
     * 下载App
     */
    public void downLoadApp(final BaseFrameActivity activity, String downLoadUrl, OnNewVersionListener listener) {
        String fileName = getFileNameByUrl(downLoadUrl);
        fileName = fileName == null ? UUID.randomUUID().toString() + ".apk" : fileName;
        ZHttp.downLoadFile(downLoadUrl, new ZFileResponse(FramePathConst.getInstance().getPathTemp() + fileName, isDownloadSilent ? null : activity, "正在下载……") {
            @Override
            public void onError(int code, Call call, Exception e) {
                ToastUtil.toastShort("下载失败！");
                if (listener != null) {
                    listener.onUpdateError(DownloadErrorStatus.ERROR_DOWNLOAD);
                }
            }

            @Override
            public void onSuccess(Response response, File resObj) {
                AppUtil.installBySys(activity, resObj);
            }

            @Override
            public void onProgress(float progress, long total) {
                super.onProgress(progress, total);
                setBarMsg("正在下载……" + (int) (progress * 100) + "/" + 100);
            }
        });
    }

    /**
     * 使用系统下载器下载App
     */
    public void downLoadAppUseSystemDownloader(final Activity activity, String downLoadUrl) {
        String fileName = getFileNameByUrl(downLoadUrl);
        fileName = fileName == null ? UUID.randomUUID().toString() + ".apk" : fileName;

        SystemDownloadApkUtil downloadUtil = new SystemDownloadApkUtil(activity, downLoadUrl);
        //下载显示名字，不能是中文
        downloadUtil.setDownloadFileName(fileName + ".apk");
        downloadUtil.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadUtil.start();
    }

    /**
     * 通过下载地址获取下载的文件名称
     */
    private String getFileNameByUrl(String url) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 下载错误分类
     */
    public enum DownloadErrorStatus {
        /*下载失败*/
        ERROR_DOWNLOAD,
        /*其他*/
        ERROR_OTHER,
        /*无网络*/
        ERROR_NO_NETWORK,
        /*数据转换错误*/
        ERROR_DATE_CONVERT;
    }

    public interface OnNewVersionListener {
        /**
         * 有新版本回调
         *
         * @return 是否拦截有新版本的默认操作，返回true则自己处理
         */
        boolean onNewVersion(ZUpdateReply reply);

        /**
         * 用户确认升级后回调
         *
         * @return 是否拦截确认升级的默认操作，返回true则自己处理
         */
        boolean onUpdateConfirm(ZUpdateReply downLoadUrl);

        /**
         * 用户不升级回调
         *
         */
        void onUpdateCancel();

        /**
         * 用户自动更新出错监听，例如无网络、数据格式转换错误、下载错误、其他等
         */
        void onUpdateError(DownloadErrorStatus errorStatus);
    }
}
