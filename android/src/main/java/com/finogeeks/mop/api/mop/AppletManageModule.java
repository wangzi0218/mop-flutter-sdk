package com.finogeeks.mop.api.mop;

import android.content.Context;
import android.util.Log;

import com.finogeeks.lib.applet.anim.FadeInAnim;
import com.finogeeks.lib.applet.anim.NoneAnim;
import com.finogeeks.lib.applet.anim.SlideFromBottomToTopAnim;
import com.finogeeks.lib.applet.anim.SlideFromLeftToRightAnim;
import com.finogeeks.lib.applet.anim.SlideFromRightToLeftAnim;
import com.finogeeks.lib.applet.anim.SlideFromTopToBottomAnim;
import com.finogeeks.lib.applet.client.FinAppClient;
import com.finogeeks.lib.applet.db.entity.FinApplet;
import com.finogeeks.lib.applet.interfaces.FinCallback;
import com.finogeeks.mop.api.BaseApi;
import com.finogeeks.mop.interfaces.ICallback;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class AppletManageModule extends BaseApi {

    private static final String TAG = "AppletManageModule";

    public AppletManageModule(Context context) {
        super(context);
    }

    @Override
    public String[] apis() {
        return new String[]{"currentApplet", "closeAllApplets", "clearApplets", "removeUsedApplet", "closeApplet",
                "setActivityTransitionAnim", "sendCustomEvent", "callJS","finishRunningApplet"};
    }

    @Override
    public void invoke(String event, Map param, ICallback callback) {
        if (event.equals("currentApplet")) {
            String appId = FinAppClient.INSTANCE.getAppletApiManager().getCurrentAppletId();
            if (appId != null) {
                FinApplet applet = FinAppClient.INSTANCE.getAppletApiManager().getUsedApplet(appId);
                if (applet != null) {
                    Map<String, Object> res = new HashMap<>();
                    res.put("appId", applet.getId());
                    res.put("name", applet.getName());
                    res.put("icon", applet.getIcon());
                    res.put("description", applet.getDescription());
                    res.put("version", applet.getVersion());
                    res.put("thumbnail", applet.getThumbnail());
                    callback.onSuccess(res);
                } else {
                    callback.onSuccess(null);
                }
            } else {
                callback.onSuccess(null);
            }
        } else if (event.equals("closeAllApplets")) {
            FinAppClient.INSTANCE.getAppletApiManager().closeApplets();
            callback.onSuccess(null);
        }else if(event.equals("finishRunningApplet")){
            if (param.containsKey("appId") && param.get("appId") instanceof String) {
                String appId = (String) param.get("appId");
                FinAppClient.INSTANCE.getAppletApiManager().finishRunningApplet(appId);
                callback.onSuccess(null);
            } else {
                callback.onFail(null);
            }
        } else if (event.equals("closeApplet")) {
            if (param.containsKey("appId") && param.get("appId") instanceof String) {
                String appId = (String) param.get("appId");
                FinAppClient.INSTANCE.getAppletApiManager().closeApplet(appId);
                callback.onSuccess(null);
            } else {
                callback.onFail(null);
            }
        } else if (event.equals("clearApplets")) {
            FinAppClient.INSTANCE.getAppletApiManager().clearApplets();
            callback.onSuccess(null);
        } else if (event.equals("removeUsedApplet")) {
            if (param.containsKey("appId") && param.get("appId") instanceof String) {
                String appId = (String) param.get("appId");
                FinAppClient.INSTANCE.getAppletApiManager().removeUsedApplet(appId);
                callback.onSuccess(null);
            } else {
                callback.onFail(null);
            }
        } else if (event.equals("setActivityTransitionAnim")) {
            String anim = (String) param.get("anim");
            Log.d(TAG, "setActivityTransitionAnim:" + anim);
            if ("SlideFromLeftToRightAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromLeftToRightAnim.INSTANCE);
            } else if ("SlideFromRightToLeftAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromRightToLeftAnim.INSTANCE);
            } else if ("SlideFromTopToBottomAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromTopToBottomAnim.INSTANCE);
            } else if ("SlideFromBottomToTopAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(SlideFromBottomToTopAnim.INSTANCE);
            } else if ("FadeInAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(FadeInAnim.INSTANCE);
            } else if ("NoneAnim".equals(anim)) {
                FinAppClient.INSTANCE.getAppletApiManager().setActivityTransitionAnim(NoneAnim.INSTANCE);
            }
            callback.onSuccess(null);
        } else if (event.equals("sendCustomEvent")) {
            String appId = (String) param.get("appId");
            Map eventData = (Map) param.get("eventData");
            Log.d(TAG, "sendCustomEvent:" + appId);
            if (appId != null) {
                FinAppClient.INSTANCE.getAppletApiManager().sendCustomEvent(appId, eventData == null ? "" : new Gson().toJson(eventData));
                callback.onSuccess(null);
            } else {
                callback.onFail(null);
            }
        } else if (event.equals("callJS")) {
            String appId = (String) param.get("appId");
            String eventName = (String) param.get("eventName");
            String nativeViewId = (String) param.get("nativeViewId");
            int viewId = 0;
            if (nativeViewId != null && !nativeViewId.equals("")) {
                try {
                    viewId = Integer.parseInt(nativeViewId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Map eventData = (Map) param.get("eventData");
            Log.d(TAG, "callJS:" + appId);
            if (appId != null && eventName != null) {
                FinAppClient.INSTANCE.getAppletApiManager().callJS(appId, eventName, eventData == null ? "" : new Gson().toJson(eventData),
                        viewId, new FinCallback<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Map<String, Object> res = new HashMap<>();
                                res.put("data", s);
                                callback.onSuccess(res);
                            }

                            @Override
                            public void onError(int i, String s) {
                                callback.onFail(null);
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
            } else {
                callback.onFail(null);
            }
        }
    }
}
