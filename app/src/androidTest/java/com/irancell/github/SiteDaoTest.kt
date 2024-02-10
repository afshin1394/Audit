package com.irancell.github

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.database.dao.SiteDao
import com.irancell.nwg.ios.database.room.DataBase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
class SiteDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: DataBase

    private lateinit var siteDao: SiteDao


    @Before
    fun setup() {
        hiltRule.inject()
        siteDao = database.siteDao
    }


    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun insertSite() = runBlocking {
        val site = DatabaseSite(
           0,"JH999","USMM","R6","Hamedan","HFN","cnractor","GroundFloor","24"
                ,"installed","up",2,1,2,"34.55466","45.6443",10

        )
        siteDao.insertAllSites(arrayListOf(site))
        val allUsers = siteDao.getAllSites()
        assertThat(allUsers).contains(site)
    }
//

}