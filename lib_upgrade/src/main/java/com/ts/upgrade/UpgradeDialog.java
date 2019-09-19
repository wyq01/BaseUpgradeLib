package com.ts.upgrade;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpgradeDialog {
    public interface OnClickListener {
        void onClick(DialogInterface dialog);
    }

    private UpgradeDialog instance = null;

    private TextView versionTv;
    private TextView fileSizeTv;
    private TextView upgradeTimeTv;
    private TextView upgradeMsgTv;
    private ProgressBar downloadPb;

    private Context context;
    private AlertDialog.Builder builder;
    private OnClickListener positiveListener;
    private OnClickListener negativeListener;
    private OnClickListener neutralListener;

    public UpgradeDialog(Context context) {
        this.context = context;
        this.builder = new AlertDialog.Builder(context, R.style.upgradeDialog);

        View view = LayoutInflater.from(context).inflate(R.layout.upgrade_view_dialog, null);
        versionTv = view.findViewById(R.id.versionTv);
        fileSizeTv = view.findViewById(R.id.fileSizeTv);
        upgradeTimeTv = view.findViewById(R.id.upgradeTimeTv);
        upgradeMsgTv = view.findViewById(R.id.upgradeMsgTv);
        upgradeMsgTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        downloadPb = view.findViewById(R.id.downloadPb);
        downloadPb.setVisibility(View.INVISIBLE);

        this.builder.setView(view);

        instance = this;
    }

    public void setProgress(int progress) {
        downloadPb.setVisibility(View.VISIBLE);
        downloadPb.setProgress(progress);
    }

    public UpgradeDialog setTitle(CharSequence title) {
        builder.setTitle(title);
        return instance;
    }

    public UpgradeDialog setVersion(CharSequence version) {
        versionTv.setText(String.format("版本：%s", version));
        return instance;
    }

    public UpgradeDialog setFileSize(CharSequence fileSize) {
        fileSizeTv.setText(String.format("包大小：%s", fileSize));
        return instance;
    }

    public UpgradeDialog setUpgradeTime(CharSequence upgradeTime) {
        upgradeTimeTv.setText(String.format("更新时间：%s", upgradeTime));
        return instance;
    }

    public UpgradeDialog setUpgradeMsg(CharSequence upgradeMsg) {
        upgradeMsgTv.setText(upgradeMsg);
        return instance;
    }

    public UpgradeDialog setPositiveButton(CharSequence text, OnClickListener listener) {
        builder.setPositiveButton(text, null);
        this.positiveListener = listener;
        return instance;
    }

    public UpgradeDialog setNegativeButton(CharSequence text, OnClickListener listener) {
        builder.setNegativeButton(text, null);
        this.negativeListener = listener;
        return instance;
    }

    public UpgradeDialog setNeutralButton(CharSequence text, OnClickListener listener) {
        builder.setNeutralButton(text, null);
        this.neutralListener = listener;
        return instance;
    }

    public UpgradeDialog setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return instance;
    }

    public UpgradeDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        builder.setOnCancelListener(onCancelListener);
        return instance;
    }

    public UpgradeDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        builder.setOnDismissListener(onDismissListener);
        return instance;
    }

    public UpgradeDialog show() {
        final AlertDialog dialog = builder.create();
        dialog.show();

        // 在show之后改变按钮的颜色
        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveBtn != null) {
            positiveBtn.setTextColor(context.getResources().getColor(R.color.upgrade_theme));

            positiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveListener != null) positiveListener.onClick(dialog);
                }
            });
        }
        final Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negativeBtn != null) {
            negativeBtn.setTextColor(context.getResources().getColor(R.color.upgrade_theme));

            negativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeListener != null) negativeListener.onClick(dialog);
                }
            });
        }
        final Button neutralBtn = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if (neutralBtn != null) {
            neutralBtn.setTextColor(context.getResources().getColor(R.color.upgrade_theme));

            neutralBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (neutralListener != null) neutralListener.onClick(dialog);
                }
            });
        }
        return instance;
    }

}