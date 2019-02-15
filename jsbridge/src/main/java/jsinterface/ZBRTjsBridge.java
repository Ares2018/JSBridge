package jsinterface;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.commonwebview.webview.CommonWebView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 浙报集团通用JSSDK
 * Created by wanglinjie.
 * create time:2019/2/14  上午10:32
 */
public class ZBRTjsBridge {
    public static final String PREFIX_JS_METHOD_NAME = "ZBJTJSBridge";

    private CommonWebView webview;

    public ZBRTjsBridge(CommonWebView webview) {
        this.webview = webview;
    }

    /**
     * 通过反射去查找指定的方法
     *
     * @param api      执行的api方法
     * @param json     js传的json数据
     * @param callback 回传js的api方法
     */
    @JavascriptInterface
    public String invoke(String api, String json, String callback) {
        if (TextUtils.isEmpty(api) || !checkJSApiValid(api)) {
            return "11001";
        }

        if (json == null || callback == null) {
            return "11002";
        }

        try {
            //获取方法
            Method method = getClass().getDeclaredMethod(api, Class.forName("java.lang.String"), Class.forName("java.lang.String"));
            //调用
            method.invoke(getClass().newInstance(), json, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }

    /**
     * 检测该方法是否有效
     *
     * @param api
     * @return
     */
    private boolean checkJSApiValid(String api) {
        try {
            Method[] methods = getClass().getMethods();
            for (Method method : methods) {
                if (method.getName() != null) {
                    if (api.equals(method.getName())) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * webview加载js方法
     *
     * @param url
     */
    private void webviewLoadUrl(String callback, String url) {
        final String execUrl;
        if (!TextUtils.isEmpty(callback)) {
            execUrl = "javascript:" + callback + "(" + url + ")";
        } else {
            execUrl = "javascript:" + "console.error" + "(" + url + ")";
        }
        if (webview != null) {
            webview.post(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl(execUrl);
                }
            });
        }

    }

    /**
     * 判断当前客户端版本是否支持指定JS接口
     */
    private String checkJSApi(String json, String callback) {
        //该方法中json与callback不能为空
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        //解析数据
        Gson gson = new Gson();
        List<String> apiList = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

        //比对是否存在
        try {
            //获取方法
            Method[] methods = getClass().getMethods();
            if (apiList != null && apiList.size() > 0) {
                //api容器
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("code", "1");
                Map<String, String> checkResult = new HashMap<>();
                for (int i = 0; i < apiList.size(); i++) {
                    if (methods != null && methods.length > 0) {
                        for (Method method : methods) {
                            if (method.getName() != null) {
                                if (apiList.get(i).equals(method.getName())) {
                                    checkResult.put(apiList.get(i), "1");
                                } else {
                                    jsonObj.put("code", "0");
                                    checkResult.put(apiList.get(i), "0");
                                }
                            } else {
                                return "11001";
                            }
                        }
                    } else {
                        return "11001";
                    }
                }
                jsonObj.put("checkResult", checkResult);
                //存在回调方法
                webviewLoadUrl(callback, jsonObj.toString());
            } else {
                return "11001";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1";
    }

    /**
     * 打开分享，分享成功后传入回调
     */
    private String openAppShareMenu(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 更新原生分享内容接口
     *
     * @return
     */
    private String updateAppShareData(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 拍照或从手机相册中选择图片，并将结果回传给Js
     *
     * @return
     */
    private String selectImage(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 录音
     *
     * @return
     */
    private String startRecord(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 获取客户端信息接口
     *
     * @return
     */
    private String getAppInfo(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 定位
     *
     * @return
     */
    private String getLocation(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 关闭页面
     *
     * @return
     */
    private String closeWindow(String json, String callback) {
        return "1";
    }

    /**
     * 利用客户端进行数据Key-Value取值
     *
     * @return
     */
    private String saveValueToLocal(String json, String callback) {
        if (TextUtils.isEmpty(json) || TextUtils.isEmpty(callback)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 登录
     *
     * @return
     */
    private String login(String json, String callback) {
        return "1";
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    private String getUserInfo(String json, String callback) {
        return "1";
    }

    /**
     * 实名认证功能-绑定手机号（调用判断手机绑定前先判断登录状态）
     *
     * @return
     */
    private String openAppMobile(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

    /**
     * 修改用户相关信息-[收货名称\收货地址] 调用判断用户收货名称修改前先判断登录状态
     *
     * @return
     */
    private String modifyUserInfo(String json, String callback) {
        if (TextUtils.isEmpty(json)) {
            return "11002";
        }

        return "1";
    }

}
