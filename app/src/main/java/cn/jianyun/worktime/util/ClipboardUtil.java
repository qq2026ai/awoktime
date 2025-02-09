package cn.jianyun.worktime.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 创建：wukuiqing
 * <p>
 * 时间：2018/4/17
 * <p>
 * 描述：
 */
public class ClipboardUtil {


    private ClipboardUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    public static void copyText(Context context,final CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static String getText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(context).toString();
        }
        return "";
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    public static void copyUri(Context context,final Uri uri) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newUri(context.getContentResolver(), "uri", uri));
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    public static Uri getUri(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getUri();
        }
        return null;
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    public static void copyIntent(Context context,final Intent intent) {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newIntent("intent", intent));
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    public static Intent getIntent(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getIntent();
        }
        return null;
    }


    /**
     * 清空剪切板第一条
     * @param context
     * @return
     */
    public static void clearFirstClipboard(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            clipboard.setPrimaryClip(ClipData.newPlainText(null, ""));
            if (clipboard.hasPrimaryClip()) {
                clipboard.getPrimaryClip().getItemAt(0).getText();


            }
        }

    }
}

