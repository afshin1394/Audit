package com.irancell.nwg.ios.presentation.main

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.databinding.ActivityMainBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.SESSION_TOKEN
import com.irancell.nwg.ios.presentation.base.BaseActivity
import com.irancell.nwg.ios.presentation.login.LoginActivity
import com.irancell.nwg.ios.presentation.main.fragment.SettingMainFragment
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.system.exitProcess


const val SENDER = "sender"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    NavigationView.OnNavigationItemSelectedListener {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    @Inject
    lateinit var sharedPref: SharedPref

//    @Inject
//    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    lateinit var headerView: View

//    lateinit var headerBinding : IosBackgroundBinding

    override fun inflateBiding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): MainViewModel {
        val mainViewModel: MainViewModel by viewModels()
        return mainViewModel
    }

    override fun initFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun initNavController(): NavController {
        return viewBinding.mainNavigationHost.getFragment<NavHostFragment>().navController
    }

    override fun initViews() {



        viewModel.getProfile().observe(this) {
            Log.i("getProfile", "initViews: ")

            when (it.status) {
                AsyncStatus.LOADING -> {
                    loading.showLoading()
                }
                AsyncStatus.SUCCESS -> {
                    it.data?.let { profileDomain ->
                        val name_fname = headerView.findViewById<TextView>(R.id.txt_name_user)

                        name_fname.text = String.format(
                            "%1s\t%2s",
                            profileDomain[0].first_name?.trim(),
                            profileDomain[0].last_name?.trim()
                        )

                    }
                    loading.dismissLoading()
                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()
                }
            }
        }


        setSupportActionBar(viewBinding.actionBar.toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            viewBinding.drawerLayoutMain,
            R.string.action_settings,
            R.string.action_exit
        )

        viewBinding.actionBar.imgMenu.setAnimationClick(lifecycleScope,R.anim.button_click) {
            if (viewBinding.drawerLayoutMain.isDrawerOpen(
                    viewBinding.drawerMain
                )
            ) viewBinding.drawerLayoutMain.closeDrawer(GravityCompat.END) else viewBinding.drawerLayoutMain.openDrawer(
                GravityCompat.START
            )
        }

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        viewBinding.drawerMain.addHeaderView(
            layoutInflater.inflate(
                R.layout.ios_background,
                null
            )
        )
        headerView = viewBinding.drawerMain.getHeaderView(0)
//        headerBinding  = IosBackgroundBinding.inflate(layoutInflater)
        headerView.findViewById<ImageView>(R.id.img_back).setAnimationClick(lifecycleScope,R.anim.button_click) {
            if (sharedPref.getString(SELECTED_LANGUAGE) == "fa")
                viewBinding.drawerLayoutMain.closeDrawer(Gravity.RIGHT)
            else
                viewBinding.drawerLayoutMain.closeDrawer(Gravity.LEFT)

        }
        viewBinding.drawerLayoutMain.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        viewBinding.drawerMain.setNavigationItemSelectedListener(this)
        viewBinding.actionBar.txtLoadedPage.text = this.getString(R.string.action_home)

        when (intent.getStringExtra(SENDER)) {
            SettingMainFragment::class.java.simpleName -> {
                viewBinding.actionBar.txtLoadedPage.text = this.getString(R.string.action_settings)
                navigate(R.id.settingMainFragment)
            }
        }
    }

    override fun initActionBar(): SearchActionBarBinding? {
        return viewBinding.actionBar
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                viewBinding.actionBar.txtLoadedPage.text = item.title
                navigate(R.id.homeMainFragment)
            }

            R.id.action_profile -> {
                viewBinding.actionBar.txtLoadedPage.text = item.title
                navigate(R.id.profileMainFragment)
            }
            R.id.action_audit -> {
                viewBinding.actionBar.txtLoadedPage.text = item.title
                navigate(R.id.auditMainFragment)
            }
            R.id.action_settings -> {
                viewBinding.actionBar.txtLoadedPage.text = item.title
                navigate(R.id.settingMainFragment)
            }
            R.id.action_dashboard -> {
                viewBinding.actionBar.txtLoadedPage.text = item.title
                navigate(R.id.reportMainFragment)
            }
            R.id.action_share -> {
//                throw RuntimeException()
//                exportDatabaseFile(context = this@MainActivity)
                showToast(this@MainActivity, R.string.UnderDev)

            }
            R.id.action_logout -> {
                viewModel.logout().observe(this@MainActivity){
                    when(it.status){
                        AsyncStatus.LOADING->{ loading.showLoading()}
                        AsyncStatus.SUCCESS->{
                            sharedPref.remove(SESSION_TOKEN)
                            loading.dismissLoading()
                            this@MainActivity.finish()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        }
                        AsyncStatus.ERROR->
                        {


                            if (it.code == NETWORK_UNREACHABLE){
                                showToast(this@MainActivity, R.string.networkError)
                            }else{
                                sharedPref.remove(SESSION_TOKEN)
                                loading.dismissLoading()
                                this@MainActivity.finish()
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            }
                            loading.dismissLoading()}
                    }
                }

            }
            R.id.action_exit -> {
                this.finish()
                exitProcess(0)
            }
        }

        if (sharedPref.getString(SELECTED_LANGUAGE) == "fa")
            viewBinding.drawerLayoutMain.closeDrawer(Gravity.RIGHT)
        else viewBinding.drawerLayoutMain.closeDrawer(Gravity.LEFT)


        return true
    }

//    fun populateData(){
//        var options : ArrayList<Option> = arrayListOf()
//        var i = 0.0
//        var counter = 0
//        while (i<15) {
//            i += 0.5f
//            counter++
//            options.add(Option(UUID.randomUUID().toString(), (i).toString(), i.toDouble()))
//        }
//        var data = Extra(options)
//        Log.i("DADAATAA", "populateData: "+ Gson().toJson(data))
//
//    }
//    fun populateData() {
//        var data1 = Data(null, null, "0", false,false,null)
//        var element = AttrElement(
//            UUID.randomUUID().toString(), Name("مشکل دار", "Problematic"), Type( SwitchGenerator, "SwitchGenerator"),null,
//            data1
//        )
//
//
//        var subGroup = SubGroup(UUID.randomUUID().toString(), Name("مشکل دار", "Problematic"), arrayListOf(element))
//        Log.i("subGroup", "populateData: " + Gson().toJson(subGroup))
//
//        var problematicIssue = arrayListOf(
//            Option(UUID.randomUUID().toString(), "LandLord issue", 0.0),
//            Option(UUID.randomUUID().toString(), "Key Issue", 0.0),
//            Option(UUID.randomUUID().toString(), "Permission-MCI", 0.0),
//            Option(UUID.randomUUID().toString(), "Permission-Rightel", 0.0),
//            Option(UUID.randomUUID().toString(), "Permission-Organization", 0.0),
//            Option(UUID.randomUUID().toString(), "Access issue", 0.0),
//            Option(UUID.randomUUID().toString(), "Other", 0.0)
//        )
//        var data = Data(null, problematicIssue, null, false,false,null)
//        var elementgen = AttrElement(
//            UUID.randomUUID().toString(), Name("مورد مشکل دار", "Problematic issue"),Type( MultiChoice, "MultiChoice"),
//            Extra(options = problematicIssue),null
//        )
////        var elementgen2 = AttrElement(
////            UUID.randomUUID().toString(), Name("مورد مشکل دار", "Problematic issue"),Type( Comment, "Comment"),
////            null
////        )
////        var elementGen3 = AttrElement(
////            UUID.randomUUID().toString(), Name("مورد مشکل دار", "Problematic issue"),Type( Image, "Image"),
////            null
////        )
////        var subGen = SubGroup(UUID.randomUUID().toString(), Name("", ""), arrayListOf(elementgen))
//        Log.i("subGennnn", "populateData: " + Gson().toJson(elementgen))
//    }
//    fun populateData() {
//      var element900 = AttrElement(1,"E-Tilt 900", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element900.generatorId = 5000
//        element900.generateType = GenerateElement
//      var element1800 = AttrElement(2,"E-Tilt 1800", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element1800.generatorId = 6000
//        element1800.generateType = GenerateElement
//      var element2100 = AttrElement(3,"E-Tilt 2100", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element2100.generatorId = 7000
//        element2100.generateType = GenerateElement
//      var element2300 = AttrElement(4,"E-Tilt 2300", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element2300.generatorId = 8000
//        element2300.generateType = GenerateElement
//      var element2600 = AttrElement(5,"E-Tilt 2600", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element2600.generatorId = 9000
//        element2600.generateType = GenerateElement
//
//        var subgroup900 = SubGroup(1,"E-Tilt 900", arrayListOf(element900))
//        var subgroup1800 = SubGroup(2,"E-Tilt 1800", arrayListOf(element1800))
//        var subgroup2100 = SubGroup(3,"E-Tilt 2100", arrayListOf(element2100))
//        var subgroup2300 = SubGroup(4,"E-Tilt 2300", arrayListOf(element2300))
//        var subgroup2600 = SubGroup(5,"E-Tilt 2600", arrayListOf(element2600))
//         var groupFDD = Group(1,"???", arrayListOf(subgroup900,subgroup1800,subgroup2100,subgroup2300,subgroup2600))
//        Log.i("groupFDD", "populateData: "+Gson().toJson(groupFDD))
//
//
//
//        var element3500 = AttrElement(2,"E-Tilt 3500", SwitchGenerator,"SwitchGenerator", arrayListOf())
//        element3500.generatorId = 11000
//        element3500.generateType = GenerateElement
//        var subgroup3500 = SubGroup(1,"E-Tilt 3500", arrayListOf(element3500))
//
//        var groupTDD = Group(1,"???", arrayListOf(subgroup2300,subgroup3500))
//        Log.i("GroupTDD", "populateData: "+Gson().toJson(groupTDD))
//
//        var options : ArrayList<Option> = arrayListOf()
//        options.add(Option(0, "OFF", (-1).toDouble()))
//        var i= 0f
//        var counter = 0
//        while (i<15) {
//            i += 0.5f
//            counter++
//            options.add(Option(counter, (i).toString(), i.toDouble()))
//        }
//
//        var optionsss = Data(null,options,null,null)
//        Log.i("optionss", "populateData: "+Gson().toJson(optionsss))
//
//
//        for (i in 0 ..15)
//            options.add(Option(i , (i/2+ 0.5).toString(),i/2+0.5))
//
//        var data = Data(null,options,null,null)
//
//        var attr1 = AttrElement(1,"OnSite E-Tilt 900", Image,"Image", arrayListOf())
//        var attr2 = AttrElement(2,"OnSite E-Tilt 900", Picker,"Picker", arrayListOf(data))
//        var attr3 = AttrElement(3,"Changed E-Tilt 900", Image,"Image", arrayListOf())
//        var attr4 = AttrElement(4,"Changed E-Tilt 900", Picker,"Picker", arrayListOf(data))
//        var subGroup1 = SubGroup(1,"E_Tilt 900", arrayListOf(attr1,attr2,attr3,attr4))
//
//
//
//        var attr11 = AttrElement(1,"OnSite E-Tilt 1800", Image,"Image", arrayListOf())
//        var attr22 = AttrElement(2,"OnSite E-Tilt 1800", Picker,"Picker", arrayListOf(data))
//        var attr33 = AttrElement(3,"Changed E-Tilt 1800", Image,"Image", arrayListOf())
//        var attr44 = AttrElement(4,"Changed E-Tilt 1800", Picker,"Picker", arrayListOf(data))
//        var subGroup2 = SubGroup(1,"E_Tilt 1800", arrayListOf(attr11,attr22,attr33,attr44))
//
//
//        var attr111 = AttrElement(1,"OnSite E-Tilt 2100", Image,"Image", arrayListOf())
//        var attr222 = AttrElement(2,"OnSite E-Tilt 2100", Picker,"Picker", arrayListOf(data))
//        var attr333 = AttrElement(3,"Changed E-Tilt 2100", Image,"Image", arrayListOf())
//        var attr444 = AttrElement(4,"Changed E-Tilt 2100", Picker,"Picker", arrayListOf(data))
//        var subGroup3 = SubGroup(1,"E_Tilt 2100", arrayListOf(attr111,attr222,attr333,attr444))
//
//
//        var attr1111 = AttrElement(1,"OnSite E-Tilt 2300", Image,"Image", arrayListOf())
//        var attr2222 = AttrElement(2,"OnSite E-Tilt 2300", Picker,"Picker", arrayListOf(data))
//        var attr3333 = AttrElement(3,"Changed E-Tilt 2300", Image,"Image", arrayListOf())
//        var attr4444 = AttrElement(4,"Changed E-Tilt 2300", Picker,"Picker", arrayListOf(data))
//        var subGroup4 = SubGroup(1,"E_Tilt 2300", arrayListOf(attr1111,attr2222,attr3333,attr4444))
//
//
//
//        var attr11111 = AttrElement(1,"OnSite E-Tilt 2600", Image,"Image", arrayListOf())
//        var attr22222 = AttrElement(2,"OnSite E-Tilt 2600", Picker,"Picker", arrayListOf(data))
//        var attr33333 = AttrElement(3,"Changed E-Tilt 2600", Image,"Image", arrayListOf())
//        var attr44444 = AttrElement(4,"Changed E-Tilt 2600", Picker,"Picker", arrayListOf(data))
//        var subGroup5 = SubGroup(1,"E_Tilt 2600", arrayListOf(attr11111,attr22222,attr33333,attr44444))
//
//
//        var attr111111 = AttrElement(1,"OnSite E-Tilt 3500", Image,"Image", arrayListOf())
//        var attr222222 = AttrElement(2,"OnSite E-Tilt 3500", Picker,"Picker", arrayListOf(data))
//        var attr333333 = AttrElement(3,"Changed E-Tilt 3500", Image,"Image", arrayListOf())
//        var attr444444 = AttrElement(4,"Changed E-Tilt 3500", Picker,"Picker", arrayListOf(data))
//        var subGroup6 = SubGroup(1,"E_Tilt 3500", arrayListOf(attr111111,attr222222,attr333333,attr444444))
//
//
////        var groupGenEtilt900 = Group(1,"???", arrayListOf(subGroup1))
////        var groupGenEtilt1800 = Group(1,"???", arrayListOf(subGroup2))
////        var groupGenEtilt2100 = Group(1,"???", arrayListOf(subGroup3))
////        var groupGenEtilt2300 = Group(1,"???", arrayListOf(subGroup4))
////        var groupGenEtilt2600 = Group(1,"???", arrayListOf(subGroup5))
////        var groupGenEtilt3500 = Group(1,"???", arrayListOf(subGroup6))
//
//        Log.i("groupGen900", "populateData: "+Gson().toJson( subGroup1))
//        Log.i("groupGen1800", "populateData: "+Gson().toJson(subGroup2))
//        Log.i("groupGen2100", "populateData: "+Gson().toJson(subGroup3))
//        Log.i("groupGen2300", "populateData: "+Gson().toJson(subGroup4))
//        Log.i("groupGen2600", "populateData: "+Gson().toJson(subGroup5))
//        Log.i("groupGen3500", "populateData: "+Gson().toJson(subGroup6))
////        var attr3 = AttrElement(4,"OnSite E-Tilt 900 Number", SingleChoice,"SingleChoice", arrayListOf(dataE_tilt))
//
//        var antennaView = AttrElement(1,"Antenna View",Image,"Image", arrayListOf())
//        var changedAntennaView = AttrElement(2,"Changed Antenna View",Image,"Image", arrayListOf())
//        var mechanicalArm = AttrElement(3,"Mechanical Arm",Image,"Image", arrayListOf())
//        var changedMechanicalArm = AttrElement(4,"Changed Mechanical Arm",Image,"Image", arrayListOf())
//        var deviceOnAntenna = AttrElement(5,"Device On Antenna",Image,"Image", arrayListOf())
//        var AntennaLeftSide = AttrElement(6,"Antenna Left Side",Image,"Image", arrayListOf())
//        var AntennaRightSide = AttrElement(7,"Antenna Right Side",Image,"Image", arrayListOf())
//        var AntennaLabel = AttrElement(8,"Antenna Label",Image,"Image", arrayListOf())
//        var AntennaSerialNumber = AttrElement(9,"Antenna Serial Number",Image,"Image", arrayListOf())
//        var AntennaSerialNumberBarcode = AttrElement(10,"Antenna Serial Number", Barcode,"Barcode", arrayListOf())
//
//        var subGroup = SubGroup(1,"General", arrayListOf(antennaView,changedAntennaView,mechanicalArm,changedMechanicalArm,deviceOnAntenna,AntennaLeftSide,AntennaRightSide,AntennaLabel,AntennaSerialNumber,AntennaSerialNumberBarcode))
//        Log.i("sadaadad", "populateData: "+Gson().toJson(subGroup))
//
//        var OnSiteAzimuth = AttrElement(1,"OnSite Azimuth",Image,"Image", arrayListOf())
//        var OnSiteAzimuthComment = AttrElement(2,"OnSite Azimuth",Comment,"Comment", arrayListOf())
//        var ChangedAzimuth = AttrElement(3,"Changed Azimuth",Image,"Image", arrayListOf())
//        var ChangedAzimuthComment = AttrElement(4,"Changed Azimuth",Comment,"Comment", arrayListOf())
//        var OnSiteMTilt  = AttrElement(5,"OnSite M Tilt",Image,"Image", arrayListOf())
//        var OnSiteMTiltComment = AttrElement(6,"OnSite M Tilt", Comment,"Comment", arrayListOf())
//        var ChangedMTilt = AttrElement(7,"Changed M Tilt",Image,"Image", arrayListOf())
//        var ChangedMTiltComment = AttrElement(8,"Changed M Tilt", Comment,"Comment", arrayListOf())
//        var OnSiteHeight = AttrElement(9,"OnSite Height",Image,"Image", arrayListOf())
//        var OnSiteHeightComment = AttrElement(10,"OnSite Height", Comment,"Comment", arrayListOf())
//        var ChangedHeight = AttrElement(11,"ChangedHeight", Image,"Image", arrayListOf())
//        var ChangedHeightComment = AttrElement(12,"Changed Height", Comment,"Comment", arrayListOf())
//
//        var subGroupMeasurmenrt = SubGroup(2,"Cell Measurement", arrayListOf(OnSiteAzimuth,OnSiteAzimuthComment,ChangedAzimuth,ChangedAzimuthComment,OnSiteMTilt,OnSiteMTiltComment,ChangedMTilt,ChangedMTiltComment,OnSiteHeight,OnSiteHeightComment,ChangedHeight,ChangedHeightComment))
//        Log.i("sdsads", "populateData: "+Gson().toJson(subGroupMeasurmenrt))
//       var siteTypeOption = arrayListOf(
//        Option(0, "Macro", 0.0),
//        Option(1, "Micro", 0.0),
//        Option(2, "IBS", 0.0),
//        Option(3, "COW", 0.0),
//        Option(4, "ZERO", 0.0),
//        Option(5, "VSat", 0.0)
//        )
//
//        var siteClassOption = arrayListOf(Option(0, "GreenField", 0.0),
//            Option(1, "RoofTop", 0.0),
//            Option(2, "Indoor", 0.0))
//
//
//        var towerTypeOption = arrayListOf(
//            Option(0, "ICB", 0.0),
//            Option(1, "Lattice", 0.0),
//            Option(2, "Monopole", 0.0),
//            Option(3, "Guyed Mast", 0.0),
//            Option(4, "Self Support", 0.0),
//            Option(5, "Wallmount", 0.0),
//            Option(6, "Pole", 0.0),
//            Option(7, "GM", 0.0),
//            Option(8, "Beautified Tower", 0.0),
//            Option(9, "UMTS", 0.0),
//            Option(10, "Platform", 0.0),
//            Option(11, "Free Stand", 0.0),
//        )
//
//        var siteVendorOption = arrayListOf(
//            Option(0, "Huawei", 0.0),
//            Option(1, "Ericsson", 0.0),
//            Option(2, "Nokia", 0.0),
//            Option(3, "ZTE", 0.0),
//
//        )
//
//        var siteTechnologiesOption = arrayListOf(
//            Option(0, "2G", 0.0),
//            Option(1, "3G", 0.0),
//            Option(2, "4G", 0.0),
//            Option(3, "5G", 0.0),
//
//            )
//
//        var siteAccessOption = arrayListOf(
//            Option(0, "24 * 7", 0.0),
//            Option(1, "Telecome Center", 0.0),
//            Option(2, "Mosque", 0.0),
//            Option(3, "Army Zone", 0.0),
//            Option(4, "Office Zone", 0.0),
//            Option(5, "Fire Station", 0.0),
//
//            )
//        var attSiteLabel = AttrElement(1,"SiteLabel",Image,"Image", arrayListOf())
//        var siteClass = AttrElement(2,"Site Class", SingleChoice,"SingleChoice", arrayListOf(Data(null, siteClassOption,null,null)))
//        var siteType = AttrElement(3,"Site Type", SingleChoice,"SingleChoice", arrayListOf(Data(null, siteTypeOption,null,null)))
//        var towerType = AttrElement(4,"Tower Type", SingleChoice,"SingleChoice", arrayListOf(Data(null, towerTypeOption,null,null)))
//
//        var siteVendor = AttrElement(5,"Site Vendor", SingleChoice,"SingleChoice", arrayListOf(Data(null, siteVendorOption,null,null)))
//        var technologies = AttrElement(6,"Technologies", MultiChoice,"MultiChoice", arrayListOf(Data(null, siteTechnologiesOption,null,null)))
//        var siteAccess = AttrElement(7,"Site Access", MultiChoice,"MultiChoice", arrayListOf(Data(null, siteAccessOption,null,null)))
//        var siteAccessComment = AttrElement(8,"Site Access", Comment,"Comment", arrayListOf())
//
//        var subgroupSiteDetails = SubGroup(1,"Site Details", arrayListOf(attSiteLabel,siteClass,siteType,towerType,siteVendor,technologies,siteAccess,siteAccessComment))
//
//
//        var dataIsCollocation = Data(null,null,"0",null)
//        var attrColocation =  AttrElement(1,"Is Collocated", SwitchGenerator,"SwitchGenerator",arrayListOf(dataIsCollocation))
//        attrColocation.generateType = GenerateElement
//        attrColocation.generatorId = 13000
//
//        var subGroupCollocation = SubGroup(2,"Collocation", arrayListOf(attrColocation))
//
//        var collocationMode = arrayListOf(
//            Option(0, "Host", 0.0),
//            Option(1, "Guest", 0.0),
//            )
//
//        var operator = arrayListOf(
//            Option(0, "MCI", 0.0),
//            Option(1, "Rightel", 0.0),
//            Option(2, "Mobinnet", 0.0),
//            Option(3, "HiWeb", 0.0),
//        )
//
//        var dataCollocationMode = Data(null,collocationMode,null,null)
//        var dataOperator = Data(null,operator,null,null)
//
//        var attrcollocationMode = AttrElement(1,"Collocation Mode", SingleChoice,"SingleChoice",arrayListOf(dataCollocationMode))
//        var attrDataOperator = AttrElement(2,"Operator", MultiChoice,"MultiChoice",arrayListOf(dataOperator))
//        var otherOperatorSiteLabel = AttrElement(3,"Other Operator Site Label", Image,"Image", arrayListOf())
//        //13000
//        var subGroupCollocationGen = SubGroup(13000,"???", arrayListOf(attrcollocationMode,attrDataOperator,otherOperatorSiteLabel))
//        Log.i("General", "populateData:13000 Gen "+Gson().toJson(subGroupCollocationGen))
//
//        var measurements1 = AttrElement(1,"Tower Height", Image,"Image", arrayListOf())
//        var measurements2 = AttrElement(2,"Tower Height", Comment,"Comment", arrayListOf())
//        var subGroupMeasurement = SubGroup(3,"Measurements", arrayListOf(measurements1,measurements2))
//
//
//        var attrGpsImage = AttrElement(1,"GPS", Image, "Image", arrayListOf())
//        var attrGps = AttrElement(2,"GPS", Location, "Location", arrayListOf())
//        var subGroupGPS = SubGroup(4,"GPS", arrayListOf(attrGpsImage,attrGps))
//
//
//        var BTSInside = AttrElement(1,"BTS Inside",Image,"Image", arrayListOf())
//        var BTSOutSide = AttrElement(2,"BTS OutSide",Image,"Image", arrayListOf())
//        var subGroupBTS = SubGroup(5, "BTS",arrayListOf(BTSInside,BTSOutSide))
//
//
//        var AnttenaZoomIn = AttrElement(1,"Antenna Zoom In", Image,"Image", arrayListOf())
//        var AnttenaZoomOut = AttrElement(2,"Antenna Zoom Out", Image,"Image", arrayListOf())
//        var subgroupAntennaZoom = SubGroup(6,"Antenna", arrayListOf(AnttenaZoomIn,AnttenaZoomOut))
//
//
//        var general = Group(1,"General", arrayListOf(subgroupSiteDetails,subGroupCollocation,subGroupMeasurement,subGroupGPS,subGroupBTS,subgroupAntennaZoom))
//        Log.i("general", "populateData: " + Gson().toJson(general))
//
//
//        var CROpened = AttrElement(1,"CR Opened",Image,"Image", arrayListOf())
//        var CRClosed = AttrElement(2,"CR Closed",Image,"Image", arrayListOf())
//        var subGroupCR = SubGroup(1,"CR", arrayListOf(CROpened,CRClosed))
//
//        var Rigger = AttrElement(1,"Rigger",Image,"Image", arrayListOf())
//        var HSE1 = AttrElement(2,"HSE 1",Image,"Image", arrayListOf())
//        var HSE2 = AttrElement(3,"HSE 2",Image,"Image", arrayListOf())
//        var subGroupRigger = SubGroup(2,"Rigger", arrayListOf(Rigger,HSE1,HSE2))
//
//
//        var BaseGroup = Group(1,"Base", arrayListOf (subGroupCR,subGroupRigger))
//
//        Log.i("Base", "Bse: "+Gson().toJson(BaseGroup))
//    }

//     fun checkAccess(){
//         checkPermissions(
//             arrayOf(
//                 Manifest.permission.CAMERA,
//                 Manifest.permission.RECEIVE_SMS,
//                 Manifest.permission.CALL_PHONE,
//                 Manifest.permission.ACCESS_FINE_LOCATION,
//                 Manifest.permission.ACCESS_COARSE_LOCATION,
//                 Manifest.permission.READ_PHONE_STATE,
//             )
//         ) {
//
//             if (it.all { it.granted }) {
//                 loading.dismissLoading()
//             } else {
//                 checkAccess()
//             }
//         }
//     }

    override fun onDestroy() {
        super.onDestroy()
    }


}

