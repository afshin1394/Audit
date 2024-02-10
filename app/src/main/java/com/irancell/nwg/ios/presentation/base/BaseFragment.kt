package com.irancell.nwg.ios.presentation.base

import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.base.BasePermissionModel
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.presentation.dialog.fragment.LoadingFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.util.constants.ENGLISH
import com.irancell.nwg.ios.util.constants.PERSIAN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val NO_FILTER : String = "No Filter"

abstract class BaseFragment<V : ViewBinding, VM : ViewModel> : Fragment() {
    val PERMISSION_REQUEST_CODE = 9824
    private var _actionBar: SearchActionBarBinding? = null
    val actionBar get() = _actionBar!!




    private var _binding: V? = null
    val viewBinding get() = _binding!!

    private var _viewModel: VM? = null
    val viewModel get() = _viewModel!!
    private lateinit var permissionCallback: (Array<BasePermissionModel>) -> Unit

//    var _loading: LoadingFragment? = null
//    val loading get() = _loading!!

    val loading by lazy {
        LoadingFragment.newInstance()
    }


    var _fm: FragmentManager? = null
    val fm get() = _fm!!

    val wft by lazy {
        fm.beginTransaction()
    }


    companion object {
        private const val TAG = "BaseFragment"
        private lateinit var bundle: Bundle
    }


    protected lateinit var lifecycleOwner: LifecycleOwner
    protected abstract fun inflateBiding(inflater: LayoutInflater?, container: ViewGroup?): V
    protected abstract fun initView(savedInstanceState: Bundle? = null)


    protected abstract fun onShow()

    protected open  fun onSearch(query : String){

    }

    protected abstract fun initViewModel(): VM

   protected open fun initActionBar(): SearchActionBarBinding?{
        return null
    }

   protected open fun initLocal() : String{
       return ENGLISH
   }


    protected open fun onBackPressed() {

    }



    protected abstract fun initFragmentManager(): FragmentManager


    private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        onActivityResult(requestCode, it.resultCode, it.data, bundle)
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, bundle: Bundle) {

    }

    open fun onPermission(basePermissionModels: ArrayList<BasePermissionModel>) {
    }


    fun <V : ViewBinding, VM : ViewModel> startFragment(
        fragmentManager: FragmentManager,
        fragment: BaseFragment<V, VM>,
        containerViewId: Int,
    ): BaseFragment<V, VM> {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(containerViewId, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit();
        return fragment
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initViewModel()
    }

    fun LoadingFragment.showLoading() {
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

    fun startDialogFragment(dialogFragment: DialogFragment) {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val wft = fm.beginTransaction()
        dialogFragment.show(wft, "dialog")
    }


    fun tabNavigate(destination: Int) {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navController = findNavController()
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_from_left)
            .setPopExitAnim(R.anim.enter_to_right)
            .setPopEnterAnim(R.anim.exit_to_left)
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        navController.navigate(destination, null, navOptions);
    }

    fun navigateUp() {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        findNavController().navigateUp()
    }

    fun navigate(destination: Int) {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_from_left)
            .setPopExitAnim(R.anim.exit_from_left)
            .setPopEnterAnim(R.anim.enter_from_right)
            .build()
        findNavController().navigate(destination, null, navOptions);
    }

    fun navigateWithArg(destination: Int, bundle: Bundle) {
        _actionBar?.let {
            while (!actionBar.search.isIconified) {
                actionBar.search.isIconified = true;
            }
        }
        val navOptions: NavOptions = NavOptions.Builder()
            .setEnterAnim(android.R.anim.fade_in)
            .setExitAnim(android.R.anim.fade_out)
            .setPopExitAnim(android.R.anim.fade_in)
            .setPopEnterAnim(android.R.anim.fade_out)
            .build()


        findNavController().navigate(destination, bundle, navOptions);
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _actionBar = initActionBar()
        setActionBar()
        _fm = initFragmentManager()

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                _actionBar?.let {
                    if (!actionBar.search.isIconified) {
                        actionBar.search.isIconified = true;
                    } else {
                        onBackPressed()
                    }
                }?: run{
                    onBackPressed()
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        _viewModel = initViewModel()
        lifecycleOwner = this
        _binding = inflateBiding(inflater, container)

        return _binding!!.root
    }


    @OptIn(FlowPreview::class)
    private fun setActionBar() {
        _actionBar?.let {
            actionBar.search.visibility = View.VISIBLE
            actionBar.logo.root.visibility = View.INVISIBLE

            actionBar.search.setOnQueryTextFocusChangeListener { view, isFocused ->
                if (isFocused) {
                    actionBar.txtLoadedPage.visibility = View.INVISIBLE
                    actionBar.imgMenu.visibility = View.INVISIBLE
                    lifecycleOwner.lifecycleScope.launchWhenResumed {
                        actionBar.search.getQueryTextChangeStateFlow()
                            .debounce(300)
                            .filter { searchQuery ->
                                if (searchQuery.isEmpty()) {
                                    searchQuery.plus(NO_FILTER)
                                    return@filter true
                                } else {
                                    return@filter true
                                }
                            }.distinctUntilChanged()
                            .collectLatest {
                                lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                                    onSearch(it)
                                }
                            }
                    }

                } else {
                    actionBar.txtLoadedPage.visibility = View.VISIBLE
                    actionBar.imgMenu.visibility = View.VISIBLE

                }
            }
        }?:{
            actionBar.search.visibility = View.GONE
            actionBar.logo.root.visibility = View.VISIBLE
        }
    }

    private fun SearchView.getQueryTextChangeStateFlow(): StateFlow<String> {
        val searchQuery = MutableStateFlow("")
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                searchQuery.value = p0?:""
                return true
            }
        })
        return searchQuery
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        loading.isCancelable = false// disable d
        initView(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        onShow()
    }






    var requestCode: Int = 0


    fun startForResultsExtras(requestCode: Int, action: Intent, bundle: Bundle) {
//        startActivityForResult(action, requestCode, bundle)
        this.requestCode = requestCode
        BaseFragment.bundle = bundle
        activityResultLauncher.launch(action)
    }


    fun startForResultsExtras(action: String, bundle: Bundle) {
        val chooserIntent = Intent(action)
        BaseFragment.bundle = bundle
        activityResultLauncher.launch(chooserIntent)
    }


    open fun checkPermissions(permissions: Array<String?>) {
        val permissionArray = java.util.ArrayList<BasePermissionModel>()
        for (permission in permissions) {
            permissionArray.add(BasePermissionModel(permission, true))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        }
        onPermission(permissionArray)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _viewModel = null
//        _loading = null
        _fm = null
    }


    fun checkPermissions(permission: String, fn: (Array<BasePermissionModel>) -> Unit) {
        val permissions = arrayOf(permission)
        checkPermissions(permissions, fn)
    }

    fun checkPermissions(permissions: Array<String>, fn: (Array<BasePermissionModel>) -> Unit) {
        permissionCallback = fn
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
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


}