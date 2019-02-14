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
     * 判断当前客户端版本是否支持指定JS接口
     */
    private String checkJSApi(String json, String callback) {
        //该方法中json与callback不能为空
        if (TextUtils.isEmpty(json) || TextUtils.isEmpty(callback)) {
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
                                return "0";
                            }
                        }
                    } else {
                        return "0";
                    }
                }
                jsonObj.put("checkResult", checkResult);
                //存在回调方法
                if (!TextUtils.isEmpty(callback)) {
                    final String execUrl = "javascript:" + callback + "(" + jsonObj.toString() + ")";
                    webview.post(new Runnable() {
                        @Override
                        public void run() {
                            webview.loadUrl(execUrl);
                        }
                    });
                }
            } else {
                return "0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "1";
    }

}