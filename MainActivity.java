package com.ashish.geminiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;
    private EditText textToType;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        int pad = dp(18);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(pad, pad, pad, pad);
        page.setBackgroundColor(Color.rgb(248, 249, 252));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(page);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("Gemini Player"); title.setTextSize(27); title.setTextColor(Color.rgb(30, 41, 59)); title.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(title, matchWrap());
        TextView intro = new TextView(this);
        intro.setText("A user-controlled accessibility helper for screen reading, gestures, and entering text you provide.");
        intro.setTextSize(15); intro.setTextColor(Color.DKGRAY); intro.setPadding(0, dp(8), 0, dp(12));
        page.addView(intro, matchWrap());

        Button settings = button("Enable accessibility service");
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        page.addView(settings, matchWrap());

        status = new TextView(this); status.setTextSize(14); status.setTextColor(Color.rgb(22, 101, 52)); status.setPadding(0, dp(10), 0, dp(10));
        page.addView(status, matchWrap());

        TextView heading = new TextView(this); heading.setText("Type text you choose"); heading.setTextSize(18); heading.setTextColor(Color.BLACK); heading.setPadding(0, dp(8), 0, dp(6)); page.addView(heading, matchWrap());
        textToType = new EditText(this); textToType.setHint("Enter your own text here"); textToType.setMinLines(2); textToType.setGravity(Gravity.TOP); page.addView(textToType, matchWrap());
        Button type = button("Type into the currently focused field");
        type.setOnClickListener(v -> {
            String value = textToType.getText().toString();
            if (value.isEmpty()) { status.setText("Enter text first. Nothing was typed."); return; }
            boolean ok = AgentService.typeIntoFocusedField(value);
            status.setText(ok ? "Text sent to the focused field." : "Service not enabled or no editable field is focused.");
        }); page.addView(type, matchWrap());

        TextView gestures = new TextView(this); gestures.setText("Manual gestures"); gestures.setTextSize(18); gestures.setTextColor(Color.BLACK); gestures.setPadding(0, dp(18), 0, dp(6)); page.addView(gestures, matchWrap());
        addAction(page, "Tap center of screen", () -> showResult(AgentService.tapCenter()));
        addAction(page, "Swipe up", () -> showResult(AgentService.swipe("up")));
        addAction(page, "Swipe down", () -> showResult(AgentService.swipe("down")));
        addAction(page, "Swipe left", () -> showResult(AgentService.swipe("left")));
        addAction(page, "Swipe right", () -> showResult(AgentService.swipe("right")));
        addAction(page, "Read visible screen text", () -> {
            String content = AgentService.readVisibleText();
            status.setText(content == null || content.isEmpty() ? "No screen text available. Enable the service and open the target screen." : content);
        });

        TextView note = new TextView(this);
        note.setText("Use only on screens and accounts you are authorized to control. Review survey questions and submit truthful answers yourself. This app does not generate survey answers, bypass CAPTCHAs, or submit forms automatically.");
        note.setTextSize(13); note.setTextColor(Color.GRAY); note.setPadding(0, dp(18), 0, dp(8)); page.addView(note, matchWrap());
    }

    @Override protected void onResume() { super.onResume(); if (status != null && AgentService.isConnected()) status.setText("Accessibility service is connected."); }
    private void showResult(boolean ok) { status.setText(ok ? "Action sent." : "Enable the accessibility service in Android Settings first."); }
    private void addAction(LinearLayout page, String label, Runnable action) { Button b = button(label); b.setOnClickListener(v -> action.run()); page.addView(b, matchWrap()); }
    private Button button(String label) { Button b = new Button(this); b.setText(label); return b; }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }
}
