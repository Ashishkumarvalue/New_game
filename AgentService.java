package com.ashish.geminiplayer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AgentService extends AccessibilityService {
    private static volatile AgentService instance;
    @Override public void onServiceConnected() { super.onServiceConnected(); instance = this; }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) { /* Events are used only to keep the service active; no data is uploaded. */ }
    @Override public void onInterrupt() { }
    @Override public void onDestroy() { if (instance == this) instance = null; super.onDestroy(); }
    public static boolean isConnected() { return instance != null; }

    public static boolean typeIntoFocusedField(String text) {
        AgentService s = instance; if (s == null) return false;
        AccessibilityNodeInfo root = s.getRootInActiveWindow(); if (root == null) return false;
        AccessibilityNodeInfo focus = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (focus == null || !focus.isEditable()) { root.recycle(); return false; }
        Bundle args = new Bundle(); args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        boolean ok = focus.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        focus.recycle(); root.recycle(); return ok;
    }

    public static String readVisibleText() {
        AgentService s = instance; if (s == null) return "Accessibility service is not connected.";
        AccessibilityNodeInfo root = s.getRootInActiveWindow(); if (root == null) return "No active window text available.";
        StringBuilder out = new StringBuilder(); collect(root, out, 0); root.recycle();
        String result = out.toString().trim();
        if (result.length() > 5000) result = result.substring(0, 5000) + "\n…(truncated)";
        return result;
    }
    private static void collect(AccessibilityNodeInfo node, StringBuilder out, int depth) {
        if (node == null || depth > 30 || out.length() > 5000) return;
        CharSequence t = node.getText();
        if (t != null && t.length() > 0) { if (out.length() > 0) out.append('\n'); out.append(t); }
        for (int i = 0; i < node.getChildCount(); i++) { AccessibilityNodeInfo child = node.getChild(i); if (child != null) { collect(child, out, depth + 1); child.recycle(); } }
    }
    public static boolean tapCenter() {
        AgentService s = instance; if (s == null) return false;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) s.getSystemService(WINDOW_SERVICE); if (wm == null) return false;
        wm.getDefaultDisplay().getRealMetrics(dm);
        return s.gesture(dm.widthPixels * 0.5f, dm.heightPixels * 0.5f, dm.widthPixels * 0.5f, dm.heightPixels * 0.5f, 90);
    }
    public static boolean swipe(String direction) {
        AgentService s = instance; if (s == null) return false;
        DisplayMetrics dm = new DisplayMetrics(); WindowManager wm = (WindowManager) s.getSystemService(WINDOW_SERVICE); if (wm == null) return false;
        wm.getDefaultDisplay().getRealMetrics(dm);
        float cx = dm.widthPixels * .5f, cy = dm.heightPixels * .5f;
        float dx = dm.widthPixels * .32f, dy = dm.heightPixels * .28f;
        float x1 = cx, y1 = cy, x2 = cx, y2 = cy;
        switch (direction) { case "up": y1 = cy + dy; y2 = cy - dy; break; case "down": y1 = cy - dy; y2 = cy + dy; break; case "left": x1 = cx + dx; x2 = cx - dx; break; case "right": x1 = cx - dx; x2 = cx + dx; break; default: return false; }
        return s.gesture(x1, y1, x2, y2, 300);
    }
    private boolean gesture(float x1, float y1, float x2, float y2, long duration) {
        Path path = new Path(); path.moveTo(x1, y1); path.lineTo(x2, y2);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, duration);
        return dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(), null, null);
    }
}
