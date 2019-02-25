package com.osano.privacymonitor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.osano.privacymonitor.data.AppDatabase
import com.osano.privacymonitor.data.FavoritesDao
import com.osano.privacymonitor.models.FavoriteURL
import org.hamcrest.core.IsEqual
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var favoritesDao: FavoritesDao
    private val favoriteA = FavoriteURL(url = "https://google.com")
    private val favoriteB = FavoriteURL(url = "https://youtube.com")
    private val favoriteC = FavoriteURL(url = "https://microsoft.com")

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        favoritesDao = database.favoriteURLDao()

        // Insert urls to test that results are storing on database
        favoritesDao.insertAll(listOf(favoriteA, favoriteB, favoriteC))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetDomains() {
        val favoriteList = favoritesDao.getUrls()
        Assert.assertThat(favoriteList.size, IsEqual.equalTo(3))

        Assert.assertThat(favoriteList[0].url, IsEqual.equalTo(favoriteA.url))
        Assert.assertThat(favoriteList[1].url, IsEqual.equalTo(favoriteB.url))
        Assert.assertThat(favoriteList[2].url, IsEqual.equalTo(favoriteC.url))
    }

    @Test
    fun testInsertFavorite() {
        val favoriteUrl = FavoriteURL(url = "https://privacymonitor.com")
        favoritesDao.insert(favoriteUrl)

        val storedFavorite = favoritesDao.getUrls().last()
        Assert.assertThat(storedFavorite.url, IsEqual.equalTo(favoriteUrl.url))
    }

    @Test
    fun testDuplicateFavorites() {
        favoritesDao.deleteAll()

        val favorite = FavoriteURL(url = "https://privacymonitor.com")
        val favoriteDuplicate = FavoriteURL(url = "https://privacymonitor.com")

        favoritesDao.insertAll(listOf(favorite, favoriteDuplicate))

        val favoriteList = favoritesDao.getUrls()
        Assert.assertThat(favoriteList.size, IsEqual.equalTo(1))
    }

    @Test
    fun testDeleteAll() {
        favoritesDao.deleteAll()

        val favoriteList = favoritesDao.getUrls()
        Assert.assertThat(favoriteList.size, IsEqual.equalTo(0))
    }
}