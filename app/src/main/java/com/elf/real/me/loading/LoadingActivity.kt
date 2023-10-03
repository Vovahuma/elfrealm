package com.elf.real.me.loading

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.elf.real.me.App.Companion.sharedPrefs
import com.elf.real.me.R
import com.elf.real.me.databinding.ActivityLoadingBinding
import com.elf.real.me.menu.GameMenuActivity

class LoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingBinding

    private var mCameraPhotoUri: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var results: Array<Uri>? = null
            if (data == null || data.data == null) {
                if (mCameraPhotoUri != null) {
                    results = arrayOf(mCameraPhotoUri!!)
                }
            } else {
                val dataString: String? = data.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.apply {
            userAgentString = this.userAgentString
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(false)
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.requestFocus(View.FOCUS_DOWN)
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)


        binding.webView.webViewClient = MyWebViewClient()
        binding.webView.webChromeClient = MyWebChromeClient()
        onBackPressedDispatcher.addCallback(
            this,
            backPressedCallback
        )

        turnOnCookieCollection()

        val url = sharedPrefs.getRef().toString()
        binding.webView.loadUrl(url)
    }

    private fun backPressed() {
        val webView = binding.webView
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backPressed()
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val uri = request?.url.toString()
            val botParam = request?.url?.getQueryParameter("bot")
            val isBot = botParam?.toBoolean() ?: false
            return if (isBot) {
                sharedPrefs.deleteRef()
                startActivity(Intent(this@LoadingActivity, GameMenuActivity::class.java))
                finish()
                true
            } else if (uri.startsWith("mailto:")) {
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(uri)))
                true
            } else if (uri.startsWith("tg:") || uri.startsWith("https://t.me") || uri.startsWith("https://telegram.me")) {
                try {
                    if (view != null) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(view.hitTestResult.extra)
                            )
                        )
                    }
                } catch (_: Exception) {
                }
                true
            } else if (uri.startsWith("https://diia.app") || uri.startsWith("diia:")) {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(uri)
                        )
                    )
                } catch (_: Exception) {
                }
                true
            } else {
                false
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (sharedPrefs.getRefFirst()) {
                if (url != null) {
                    sharedPrefs.setRef(url)
                }
                sharedPrefs.setRefNotFirst()
                CookieManager.getInstance().flush()
            }
            CookieManager.getInstance().flush()
        }
    }

    private fun turnOnCookieCollection() {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.acceptCookie()
        cookieManager.setAcceptThirdPartyCookies(binding.webView, true)
        cookieManager.flush()
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == 100) {
                binding.progressBar.visibility = View.GONE
            } else {

                Glide.with(applicationContext).asGif().load(R.drawable.dollar_coin)
                    .into(binding.progressBar)
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            val permStat = ContextCompat.checkSelfPermission(
                this@LoadingActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (permStat == PackageManager.PERMISSION_GRANTED) {
                mFilePathCallback?.onReceiveValue(null)
                mFilePathCallback = filePathCallback

                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                mCameraPhotoUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    mCameraPhotoUri
                )

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?> = arrayOf(takePictureIntent)
                val chooser = Intent(Intent.ACTION_CHOOSER)
                chooser.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooser.putExtra(Intent.EXTRA_TITLE, "Take Photo")
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

                resultLauncher.launch(chooser)

                return true
            } else checkPermissions()
            return false
        }

    }

    private fun checkPermissions() {
        ActivityCompat.requestPermissions(
            this@LoadingActivity, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), 1
        )
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onResume() {
        super.onResume()
        CookieManager.getInstance().flush()
    }
}