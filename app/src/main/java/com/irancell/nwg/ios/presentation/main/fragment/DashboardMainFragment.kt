package com.irancell.nwg.ios.presentation.main.fragment

import android.annotation.SuppressLint
import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.irancell.nwg.ios.databinding.FragmentDashboardMainBinding
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.SESSION_TOKEN
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.viewModel.ReportMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DashboardMainFragment : BaseFragment<FragmentDashboardMainBinding, ReportMainViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    @Inject
    lateinit var sharedPref : SharedPref

    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentDashboardMainBinding {
        return FragmentDashboardMainBinding.inflate(layoutInflater, container, false)
    }

    private fun getX509Certificate(sslCertificate: SslCertificate): Certificate? {
        val bundle = SslCertificate.saveState(sslCertificate)
        val bytes = bundle.getByteArray("x509-certificate")
        return if (bytes == null) {
            null
        } else {
            try {
                val certFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
                certFactory.generateCertificate(ByteArrayInputStream(bytes))
            } catch (e: CertificateException) {
                null
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(savedInstanceState: Bundle?) {




        val cf = CertificateFactory.getInstance("X.509")

        val caInput: InputStream =
            resources.openRawResource(com.irancell.nwg.ios.R.raw.io_sroot_ca) // stored at \app\src\main\res\raw

        val certificate = cf.generateCertificate(caInput)
        caInput.close()
        loading.showLoading()


        viewBinding.webGrafana.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (request?.url.toString().contains("uat.ios.mtnirancell")) {
                    Log.i("urlsss", "UAT"+request?.url)
                    val bearer = "Token " + sharedPref.getString(SESSION_TOKEN)
                    val headerMap = HashMap<String,String>()
                    headerMap["Authorization"] = bearer
                    headerMap["cookie"] = bearer
                    request?.requestHeaders?.putAll(headerMap)
                    val requestMod : WebResourceRequest = request!!
                    Log.i("urlsss", "UAT"+requestMod?.requestHeaders)
                    var response  = super.shouldInterceptRequest(view, requestMod)
                    Log.i("urlsss", "UAT"+response?.statusCode)

                    return response
                }else{
                    Log.i("urlsss", "VIZ"+request?.url)

                    return super.shouldInterceptRequest(view, request)
                }
            }


            override fun onPageFinished(view: WebView, url: String) {
                Log.i("WebView", "onPageFinished: ")
                loading.dismissLoading()
            }


            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                Log.i("WebView", "onReceivedSslError: ")
                val sslCertificate = error.certificate
                viewBinding.webGrafana.certificate = sslCertificate
                val cert = getX509Certificate(sslCertificate)

                if (cert != null && certificate != null) {
                    try {
                        cert.verify(certificate.publicKey) // Verify here...
                        handler.proceed()
                    } catch (e: CertificateException) {
                        super.onReceivedSslError(view, handler, error)
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        super.onReceivedSslError(view, handler, error)
                        e.printStackTrace()
                    } catch (e: InvalidKeyException) {
                        super.onReceivedSslError(view, handler, error)
                        e.printStackTrace()
                    } catch (e: NoSuchProviderException) {
                        super.onReceivedSslError(view, handler, error)
                        e.printStackTrace()
                    } catch (e: SignatureException) {
                        super.onReceivedSslError(view, handler, error)
                        e.printStackTrace()
                    }
                } else {
                    super.onReceivedSslError(view, handler, error)
                }


            }


        }
        viewBinding.webGrafana.settings.javaScriptEnabled = true;

// Enable Cross-Origin Resource Sharing (CORS)
        viewBinding.webGrafana.settings.allowUniversalAccessFromFileURLs = true;
        viewBinding.webGrafana.settings.allowFileAccessFromFileURLs = true;

// Enable mixed content (HTTP and HTTPS)
        viewBinding.webGrafana.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
        viewBinding.webGrafana.settings.domStorageEnabled = true
        viewBinding.webGrafana.settings.javaScriptEnabled = true
        viewBinding.webGrafana.loadUrl("https://viz.ios.mtnirancell.ir/")


    }
    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }
    override fun onShow() {

    }

    override fun initViewModel(): ReportMainViewModel {
        return ViewModelProvider(this)[ReportMainViewModel::class.java]
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}