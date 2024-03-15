package its.madruga.wpp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.materialswitch.MaterialSwitch;

import its.madruga.wpp.R;

public class SwitchButtonTop extends LinearLayout {

    public static String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    public SwitchButtonTop(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwitchButtonTop(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.switch_button_top_layout, (ViewGroup) getRootView());

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.SwitchButtonTop,
                0, 0
        );

        try {
            var title = a.getText(R.styleable.SwitchButtonTop_android_text);
            var summary = a.getText(R.styleable.SwitchButtonTop_android_summary);
            var tag = attrs.getAttributeValue(ANDROID_NS, "tag");
            var category = a.getText(R.styleable.SwitchButtonTop_android_title);

            var switchButton = (MaterialSwitch) findViewById(R.id.switch_button);
            switchButton.setText(title);

            var switchButtonSummary = (TextView) findViewById(R.id.switch_button_description);
            if (summary != null) {
                switchButtonSummary.setText(summary);
            } else switchButtonSummary.setVisibility(GONE);
            switchButton.setTag(tag);

            var switchButtonCategory = (TextView) findViewById(R.id.category_title);
            switchButtonCategory.setText(category);

        } finally {
            a.recycle();
        }
    }

}
