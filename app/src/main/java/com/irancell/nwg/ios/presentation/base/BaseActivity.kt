package com.irancell.nwg.ios.presentation.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.viewbinding.ViewBinding
import com.irancell.nwg.ios.Application
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.base.BasePermissionModel
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.presentation.dialog.fragment.LoadingFragment
import com.irancell.nwg.ios.util.constants.ENGLISH
import java.util.*


abstract class BaseActivity<V : ViewBinding, VMC : ViewModel> : AppCompatActivity() {


    override fun attachBaseContext(newBase: Context) {

        updateConfig(this)
        super.attachBaseContext(newBase)
    }

    val PERMISSION_REQUEST_CODE = 9824
    lateinit var activity: AppCompatActivity
    val loading by lazy {
      LoadingFragment.newInstance()
    }
    var navController: NavController? = null


    val viewBinding by lazy{
        inflateBiding()
    }
    private var _actionBar: SearchActionBarBinding? = null
    val actionBar get() = _actionBar!!


    private lateinit var permissionCallback: (Array<BasePermissionModel>) -> Unit


    val fm by lazy {
       initFragmentManager()
    }
    val viewModel by lazy{
        initViewModel()
    }





    fun updateConfig(wrapper: ContextThemeWrapper) {
        val lang: String = Application.shared.getString(SELECTED_LANGUAGE)!!
        if (lang == "") // Do nothing if dLocale is null
            return

        Locale.setDefault(Locale(lang))
        val configuration = Configuration()
        configuration.setLocale(Locale(lang))
        wrapper.applyOverrideConfiguration(configuration)
    }


    open fun initNavController(): NavController? {
        return navController
    }

    abstract fun inflateBiding(): V
    abstract fun initViewModel(): VMC

    abstract fun initFragmentManager(): FragmentManager



    open fun onActivityResult(resultCode: Int, data: Intent?) {

    }


    protected abstract fun initViews()
    protected open fun initActionBar(): SearchActionBarBinding?{
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//        _loading = LoadingFragment()
        _actionBar = initActionBar()
        setActionBar()

        setContentView(viewBinding.root)
        navController = initNavController()
//        loading.isCancelable = false
       // disable dismiss by tapping outside of the dialog
        initViews()
    }


    private fun setActionBar() {
        _actionBar?.let {
            val lang: String = Application.shared.getString(SELECTED_LANGUAGE)!!
            if (lang == "") // Do nothing if dLocale is null
                return
            Log.i("BASE_ACTIVITY", "setActionBar: "+lang)
            if (lang == ENGLISH)
                it.search.layoutDirection = View.LAYOUT_DIRECTION_RTL
            else
                it.search.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }


    fun LoadingFragment.showLoading()  {
        if (!loading.isAdded) {
            val wtf = fm.beginTransaction()
            wtf.add(loading, "loading")
            wtf.commit()
        }

    }


    fun LoadingFragment.dismissLoading() {
            val wtf = fm.beginTransaction()
            wtf.remove(loading)
            wtf.commit()



    }

    fun tabNavigate(destination: Int) {
        _actionBar?.let {
            if (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_from_left)
            .setPopExitAnim(R.anim.enter_to_right)
            .setPopEnterAnim(R.anim.exit_to_left)
            .setLaunchSingleTop(true)
            .setPopUpTo(navController?.graph!!.startDestinationId, false)
            .build()
        navController?.navigate(destination, null, navOptions);
    }

    fun navigate(destination: Int) {
        _actionBar?.let {
            if (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_from_left)
            .setPopExitAnim(R.anim.enter_to_right)
            .setPopEnterAnim(R.anim.exit_to_left)
            .build()

        navController?.navigate(destination, null, navOptions);
    }

    fun navigateWithArg(destination: Int, bundle: Bundle) {
        _actionBar?.let {
            if (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_from_left)
            .setPopExitAnim(R.anim.enter_to_right)
            .setPopEnterAnim(R.anim.exit_to_left)
            .setLaunchSingleTop(true)
            .build()


        navController?.navigate(destination, bundle, navOptions);
    }


    fun checkPermissions(permission: String, fn: (Array<BasePermissionModel>) -> Unit) {
        val permissions = arrayOf(permission)
        checkPermissions(permissions, fn)
    }

    fun checkPermissions(permissions: Array<String>, fn: (Array<BasePermissionModel>) -> Unit) {
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        permissionCallback = fn
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            val permissionResults = arrayListOf<BasePermissionModel>()

            permissions.forEachIndexed { index, permission ->

                permissionResults.add(
                    BasePermissionModel(
                        permission, grantResults[index] == PackageManager.PERMISSION_GRANTED
                    )
                )

            }

            permissionCallback(permissionResults.toTypedArray())

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    fun startForResults(action: String) {
        val chooserIntent = Intent(action)
        startActivityIntent.launch(chooserIntent);
    }

    fun startForResultsExtras(action: String, bundle: Bundle) {
        val chooserIntent = Intent(action)
        chooserIntent.extras?.putBundle("bundle", bundle)
        startActivityIntent.launch(chooserIntent);
    }

    var startActivityIntent: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        onActivityResult(it.resultCode, it.data);
    }

    fun startDialogFragment(dialogFragment: DialogFragment) {
        _actionBar?.let {
            if (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val wft = fm.beginTransaction()
        dialogFragment.show(wft, "dialog")
    }



    override fun onDestroy() {
        super.onDestroy()

    }

}