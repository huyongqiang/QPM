package com.jm.android.gt.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.jm.android.gt.executor.IExecutor;
import com.jm.android.gt.floatview.JMFloatViewBean;
import com.jm.android.gt.utils.PrefsParser;

import java.util.List;

public class JMGTSwitchManager {

    private static final String SP_NAME_SWITCH = "jmgt_switch";
    private static volatile JMGTSwitchManager instance;

    private JMGTSwitchManager() {
    }

    public static JMGTSwitchManager getInstance() {
        if (instance == null) {
            synchronized (JMGTSwitchManager.class) {
                if (instance == null) {
                    instance = new JMGTSwitchManager();
                }
            }
        }
        return instance;
    }

    public void init() {
        List<PrefsParser.PrefItem> prefs = getSwitchs();
        List<JMFloatViewBean> typeBeans = JMFloatViewManager.getInstance().getTypeBeans();
        // 如果缓存比内存数据个数多了，则直接清空缓存
        if (prefs.size() > typeBeans.size()) {
            deleteSwitchSP();
            prefs.clear();
        }
        for (JMFloatViewBean typeBean : typeBeans) {
            initSwitch(prefs, typeBean.type, typeBean.switchDefault);
        }
    }

    private void deleteSwitchSP() {
        SharedPreferences preferences = JMGTManager.getInstance().getContext().getSharedPreferences(SP_NAME_SWITCH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void initSwitch(List<PrefsParser.PrefItem> prefs, String key, boolean defaultValue) {
        for (PrefsParser.PrefItem item : prefs) {
            if (TextUtils.equals(item.key, key)) {
                return;
            }
        }
        PrefsParser.PrefItem item = new PrefsParser.PrefItem(PrefsParser.IPrefTypeDefine.TYPE_BOOLEAN, key, String.valueOf(defaultValue));
        writeSwitch(item);
        prefs.add(item);
    }

    public List<PrefsParser.PrefItem> getSwitchs() {
        return PrefsParser.getPrefs(JMGTManager.getInstance().getContext(), SP_NAME_SWITCH, PrefsParser.PARSER_SP);
    }

    public boolean isSwitchOpen(Context context, String key) {
        List<PrefsParser.PrefItem> items = PrefsParser.getPrefs(context, SP_NAME_SWITCH, PrefsParser.PARSER_SP);
        return isSwitchOpen(items, key);
    }

    public boolean isSwitchOpen(List<PrefsParser.PrefItem> prefs, IExecutor executor) {
        return isSwitchOpen(prefs, executor.type());
    }

    public boolean isSwitchOpen(List<PrefsParser.PrefItem> items, String key) {
        if (items.isEmpty()) {
            return false;
        }
        for (PrefsParser.PrefItem item : items) {
            if (TextUtils.equals(item.key, key)) {
                return Boolean.parseBoolean(item.value);
            }
        }
        return false;
    }

    public void writeSwitch(PrefsParser.PrefItem item) {
        PrefsParser.writePrefs(JMGTManager.getInstance().getContext(), SP_NAME_SWITCH, item);
    }
}
