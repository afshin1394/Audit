package com.irancell.nwg.ios.presentation.barcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.databinding.ActivityBarcodeBinding
import com.irancell.nwg.ios.util.searchQueryOnGoogle
import com.irancell.nwg.ios.presentation.barcode.viewModel.BarCodeViewModel
import com.irancell.nwg.ios.presentation.base.BaseActivity
import java.io.IOException


const val REQUEST_CAMERA_PERMISSION = 201
const val SCAN_BARCODE = 202
const val BARCODE_CONTENT = "BARCODE_CONTENT"


class BarcodeActivity : BaseActivity<ActivityBarcodeBinding, BarCodeViewModel>() {
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private val toneGen1: ToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    private var barcodeData: String? = null
    private var intentIntegrator : IntentIntegrator? = null
    override fun inflateBiding(): ActivityBarcodeBinding =
        ActivityBarcodeBinding.inflate(layoutInflater)

    override fun initViewModel(): BarCodeViewModel {
        val barCodeViewModel: BarCodeViewModel by viewModels()
        return barCodeViewModel
    }

    override fun initViews() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        viewBinding.searchInWeb.isCheckable = false
        viewBinding.searchInWeb.setOnClickListener {
            searchQueryOnGoogle(this@BarcodeActivity,barcodeData!!)
        }

        barcodeDetector?.let {
            if (!it.isOperational) {
                intentIntegrator = IntentIntegrator(this@BarcodeActivity)
                intentIntegrator?.let { intentIntegrate->
                    intentIntegrate.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    intentIntegrate.setCameraId(0)
                    intentIntegrate.setBeepEnabled(false)
                    intentIntegrate.setBarcodeImageEnabled(false)
                    intentIntegrate.initiateScan()
                }

            } else {
                initialiseDetectorsAndSources();
            }
        }

    }

//    override fun initRootLayout(): ViewGroup {
//        return viewBinding.root
//    }
//
//    override fun initContext(): Context {
//        return this@BarcodeActivity
//    }

    override fun initFragmentManager(): FragmentManager {
        return supportFragmentManager
    }


    private fun initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()


        viewBinding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@BarcodeActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(viewBinding.surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@BarcodeActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })
        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            override fun receiveDetections(detections: Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    viewBinding.barcodeText.let { textview ->
                        textview.post(Runnable {
                            textview.removeCallbacks(null)
                            viewBinding.confirm.visibility = View.VISIBLE
                            viewBinding.reject.visibility = View.VISIBLE
                            barcodeData = barcodes.valueAt(0).displayValue
                            textview.text = barcodeData
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                            viewBinding.searchInWeb.visibility = View.VISIBLE
                            viewBinding.searchInWeb.isCheckable = true

                            viewBinding.confirm.setOnClickListener {
                                var intent = Intent().putExtra(BARCODE_CONTENT, barcodeData)
                                setResult(RESULT_OK, intent);
                                finish()
                            }
                            viewBinding.reject.setOnClickListener {
                                viewBinding.searchInWeb.visibility = View.GONE
                                textview.text = getString(R.string.BarcodeContent)
                                viewBinding.confirm.visibility = View.GONE
                                viewBinding.reject.visibility = View.GONE
                            }

                        })
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        result?.let {
            it.contents?.let { content->
                viewBinding.barcodeText.text = content
                viewBinding.confirm.visibility = View.VISIBLE
                viewBinding.reject.visibility = View.VISIBLE
                viewBinding.confirm.setOnClickListener {
                    var intent = Intent().putExtra(BARCODE_CONTENT, content)
                    setResult(RESULT_OK, intent);
                    finish()
                }
                viewBinding.reject.setOnClickListener {

                    viewBinding.barcodeText.text = getString(R.string.BarcodeContent)
                    viewBinding.confirm.visibility = View.GONE
                    viewBinding.reject.visibility = View.GONE
                    intentIntegrator?.let { scan->
                        scan.initiateScan()
                    }
                }
                return
            }
            this.finishAndRemoveTask()
        }
        this.finishAndRemoveTask()

    }
}