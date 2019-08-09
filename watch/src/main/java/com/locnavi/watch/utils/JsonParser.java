package com.locnavi.watch.utils;

import com.locnavi.location.xunjimap.utils.L;
import com.locnavi.watch.XJMapSDK;
import com.locnavi.watch.R;
//import com.xunji.xunjimap.XJMapSDK;
//import com.locnavi.watch.R;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liberty on 2017/5/4.
 */

public class JsonParser {

    public static String parseIatResult(String json) {
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            List<String> phrases = new ArrayList<>();
            JSONArray words = joResult.optJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.optJSONObject(i).optJSONArray("cw");
                JSONObject obj = items.optJSONObject(0);
                phrases.add(obj.optString("w"));
                L.d(XJMapSDK.context.getString(R.string.ipsmap_log_tag_parse), obj.optString("w"));
            }
            return getKeyword(phrases);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static String getKeyword(List<String> phrases) {
        String result = "";
        for (int i = 0; i < phrases.size(); i++) {
            String phrase = phrases.get(i);
            if (phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_me)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_want)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_go)) ||
                    phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_at)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_where)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_a))
                    || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_where_a)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_where_b)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_carry))
                    || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_tell)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_how)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_go_1))
                    || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_please)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_want)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_goto))
                    || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_dream)) || phrase.equals(XJMapSDK.context.getString(R.string.ipsmap_key_up))) {
                phrases.remove(i);
                i = -1;
            }
        }
        for (int i = 0; i < phrases.size(); i++) {
            String phrase = phrases.get(i);
            result += phrase;
        }
        return result;
    }


}
