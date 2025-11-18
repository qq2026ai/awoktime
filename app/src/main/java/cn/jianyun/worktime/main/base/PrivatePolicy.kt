package cn.jianyun.worktime.views.base




import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import cn.jianyun.worktime.util.goBack
import cn.jianyun.worktime.ui.component.nav.HeaderView
import cn.jianyun.worktime.ui.component.nav.IconFont.Companion.settings
@Composable
fun PrivatePolicyView(){
    Column {
        PrivacyPolicyWebView()
    }
}


@Composable
fun PrivacyPolicyWebView() {
    val url = "https://api.kotal.cn/hm/ttime.html?a=2"

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false

                    // 启用安全浏览（如果可用）
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                        WebSettingsCompat.setSafeBrowsingEnabled(this, true)
                    }
                }

                // 处理页面加载
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 页面加载完成后的处理
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        url: String?
                    ): Boolean {
                        // 在 WebView 内打开链接，不跳转到外部浏览器
                        url?.let {
                            view?.loadUrl(it)
                        }
                        return true
                    }
                }
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}