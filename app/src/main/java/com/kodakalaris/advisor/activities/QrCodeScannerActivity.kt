package com.kodakalaris.advisor.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.transition.Explode
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.kodakalaris.advisor.R
import com.kodakalaris.advisor.model.BarcodeData
import com.kodakalaris.advisor.utils.Constants
import com.kodakalaris.advisor.utils.Helper.disableDoubleTap
import com.kodakalaris.advisor.utils.Helper.showPrompt
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.ViewFinderView
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * This app detects barcodes/qrcode and displays the value with the rear facing camera.
 * During detection overlay graphics are drawn to indicate the position, size, and ID of each barcode.
 */
class QrCodeScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler, View.OnClickListener {

    private lateinit var btn_back: ImageButton
    private lateinit var mScanner: ImageView
    private lateinit var fbtn_torch: FloatingActionButton
    private lateinit var mScannerView: ZXingScannerView
    private lateinit var mAnimation: TranslateAnimation
    private lateinit var appPermissionsDelegate: AppPermissionsDelegate
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = ArrayList()
    private var mCameraId = -1
    private var cameraPermissionGranted = false

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS) // inside your activity (if you did not enable transitions in your theme)
        window.exitTransition = Explode() // set an exit transition
        window.enterTransition = Explode() // set an enter transition
        val w = window // in Activity's onCreate() for instance
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false)
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }
        setContentView(R.layout.activity_qr_code_sanner)
        initView()
        bindData()
        initListeners()
        initScanFormats()
    }

    /**
     * initialize all the views
     */
    private fun initView() {
        val contentFrame = findViewById<ViewGroup>(R.id.preview)
        mScannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): IViewFinder {
                return CustomViewFinderView(context)
            }
        }
        contentFrame.addView(mScannerView)
        fbtn_torch = findViewById(R.id.fbtn_torch)
        btn_back = findViewById(R.id.btn_back)
        mScanner = findViewById(R.id.img_scanner_line)
    }

    /**
     * bind the data or any variable initialization
     */
    private fun bindData() {
        appPermissionsDelegate = AppPermissionsDelegate()
    }

    /**
     * initialize all the gesture tap, scale etc
     */
    private fun initListeners() {
        fbtn_torch.setOnClickListener(this)
        btn_back.setOnClickListener(this)
    }

    /**
     * initialize the scanner bar line from upside to down using TranslateAnimation
     */
    private fun initScannerAnim() {
        mAnimation = TranslateAnimation(
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.ABSOLUTE, 0f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.050f,
            TranslateAnimation.RELATIVE_TO_PARENT, 0.460f
        )
        mAnimation.duration =
            2 * resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        mAnimation.repeatCount = TranslateAnimation.INFINITE
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = LinearInterpolator()
        mScanner.animation = mAnimation
        mScanner.visibility = View.VISIBLE
    }

    /**
     * initializing the scan format
     */
    private fun initScanFormats() {
        val formats: MutableList<BarcodeFormat> =
            ArrayList()
        if (mSelectedIndices == null || mSelectedIndices!!.isEmpty()) {
            mSelectedIndices = ArrayList()
            for (i in ZXingScannerView.ALL_FORMATS.indices) {
                mSelectedIndices!!.add(i)
            }
        }
        for (index in mSelectedIndices!!) {
            formats.add(ZXingScannerView.ALL_FORMATS[index])
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats)
        }
    }

    /**
     * custom view for QRCode UI, which include hide the mask, hide the mask rectangle, also increase the mask rectangle bounds
     */
    private class CustomViewFinderView : ViewFinderView {
        constructor(context: Context?) : super(context) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        }

        private fun init() {
            setBorderAlpha(0.0f)
            setMaskColor(Color.TRANSPARENT)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            drawLaser(canvas)
        }

        override fun drawLaser(canvas: Canvas) {
            mLaserPaint.alpha = 0
        }

        override fun getFramingRect(): Rect {
            val originalRect = super.getFramingRect()
            return Rect(
                originalRect.left - 20,
                originalRect.top - 60,
                originalRect.right + 20,
                originalRect.bottom + 160
            )
        }
    }

    override fun onClick(v: View) {
        disableDoubleTap(v)
        when (v.id) {
            R.id.fbtn_torch -> {
                /**
                 * on/off the camera flash
                 */
                fbtn_torch.setSelected(!fbtn_torch.isSelected())
                mFlash = fbtn_torch.isSelected()
                mScannerView.flash = mFlash
            }
            R.id.btn_back -> {
                /**
                 * remove the QRCode Activity from the stack
                 */
                finish()
            }
        }
    }

    /**
     * handle the QRCode scanning result,
     * parse the result & redirect the data to last screen
     * @param result
     */
    override fun handleResult(result: Result) {
        Log.i(TAG, "scan result:" + result.text)
        val resultIntent = Intent()
        try {
            val barcodeData: BarcodeData? =
                Gson().fromJson<BarcodeData>(result.text, BarcodeData::class.java)
            if (barcodeData != null && barcodeData.storeinfo != null && barcodeData.storeinfo!!.storeid != null && !barcodeData.storeinfo!!.storeid!!.isEmpty()) {
                resultIntent.putExtra(Constants.APP.LAST_SCANNED_BARCODE_DATA, result.text)
                resultIntent.putExtra(
                    Constants.APP.LAST_SCANNED_STORE_ID,
                    barcodeData.storeinfo!!.storeid
                )
                setResult(Constants.APP.QRCODE_RESULTCODE1, resultIntent)
            } else {
                resultIntent.putExtra(
                    Constants.APP.LAST_SCANNED_BARCODE_ERROR,
                    resources.getString(R.string.not_a_valid_qrcode)
                )
                setResult(Constants.APP.QRCODE_RESULTCODE2, resultIntent)
            }
        } catch (ex: JsonSyntaxException) {
            resultIntent.putExtra(
                Constants.APP.LAST_SCANNED_BARCODE_ERROR,
                resources.getString(R.string.not_a_valid_qrcode)
            )
            setResult(Constants.APP.QRCODE_RESULTCODE2, resultIntent)
        }
        finish()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, mFlash)
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus)
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices)
        outState.putInt(CAMERA_ID, mCameraId)
    }

    override fun onStart() {
        super.onStart()
        appPermissionsDelegate.checkCameraPermission()
    }

    public override fun onResume() {
        super.onResume()
        startCamera()
    }

    /**
     * start the camera if the camera permission is granted for android version > 7.0
     */
    private fun startCamera() {
        try {
            if (cameraPermissionGranted) {
                mScannerView.setResultHandler(this)
                mScannerView.startCamera(mCameraId)
                //mScannerView.setFlash(mFlash);
                mScannerView.setAutoFocus(mAutoFocus)
                initScannerAnim()
            }
        } catch (ex: RuntimeException) {
            ex.printStackTrace()
        }
    }

    public override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
        mScanner.clearAnimation()
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction with the user is interrupted. In this case you will receive empty permissions and results arrays which should be treated as a cancellation.
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either [PackageManager.PERMISSION_GRANTED] or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            RC_HANDLE_CAMERA_PERM -> {
                if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraPermissionGranted = true
                    startCamera()
                    return
                }
                showPrompt(
                    this@QrCodeScannerActivity, resources.getString(
                        R.string.no_camera_permission
                    )
                ) //App was crashing on denying permission.
            }
        }
    }

    /**
     * @AppPermissionsDelegate handles the runtime permission for app
     */
    private inner class AppPermissionsDelegate {
        /**
         * check camera permission at runtime for android ver. 6.0 or greater
         */
        fun checkCameraPermission() {
            // Check for the camera permission before accessing the camera.  If the
            // permission is not granted yet, request permission.
            Log.e("ClerkApp", "Check Camera Permission")
            val rc: Int = ActivityCompat.checkSelfPermission(
                this@QrCodeScannerActivity,
                Manifest.permission.CAMERA
            )
            if (rc == PackageManager.PERMISSION_GRANTED) {
                cameraPermissionGranted = true
                startCamera()
            } else {
                requestCameraPermission()
            }
        }

        /**
         * Handles the requesting of the camera permission.  This includes
         * showing a "Snackbar" message of why the permission is needed then
         * sending the request.
         */
        private fun requestCameraPermission() {
            Log.w(TAG, "Camera permission is not granted. Requesting permission")
            val permissions = arrayOf(Manifest.permission.CAMERA)
            //Need to call camera permission everytime when user reached at this screen.
            /*if (!ActivityCompat.shouldShowRequestPermissionRationale(QrCodeScannerActivity.this, Manifest.permission.CAMERA)) {*/
            ActivityCompat.requestPermissions(
                this@QrCodeScannerActivity,
                permissions,
                RC_HANDLE_CAMERA_PERM
            )
            return
            //}
        }
    }

    companion object {
        private val TAG = QrCodeScannerActivity::class.java.simpleName
        private const val RC_HANDLE_GMS = 9001
        private const val RC_HANDLE_CAMERA_PERM = 2
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val SELECTED_FORMATS = "SELECTED_FORMATS"
        private const val CAMERA_ID = "CAMERA_ID"
    }
}
