package com.osano.privacymonitor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.osano.privacymonitor.data.AppDatabase
import com.osano.privacymonitor.data.DomainDao
import com.osano.privacymonitor.models.Domain
import org.hamcrest.core.IsEqual.equalTo
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DomainDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var domainDao: DomainDao
    private val domainA = Domain("microsoft.com", "Microsoft", 722, 0, Calendar.getInstance())
    private val domainB = Domain("github.com", "GitHub", 820, 750, Calendar.getInstance())
    private val domainC = Domain("google.com", "Google", 635, 799, Calendar.getInstance())

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        domainDao = database.domainDao()

        // Insert domains to test that results are storing on database
        domainDao.insertAll(listOf(domainA, domainB, domainC))
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetDomains() {
        val domainList = domainDao.getDomains()
        Assert.assertThat(domainList.size, equalTo(3))

        Assert.assertThat(domainList[0], equalTo(domainA))
        Assert.assertThat(domainList[1], equalTo(domainB))
        Assert.assertThat(domainList[2], equalTo(domainC))
    }

    @Test
    fun testGetDomain() {
        Assert.assertThat(domainDao.getDomain("microsoft.com"), equalTo(domainA))
    }

    @Test
    fun testUpdateDomain() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1998)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 4)
        }

        domainDao.updateDomain("microsoft.com", 831, 722, calendar)

        val storedDomain = domainDao.getDomain("microsoft.com")
        storedDomain?.let {
            Assert.assertEquals(it.score, 831)
            Assert.assertEquals(it.previousScore, 722)
            Assert.assertEquals(it.lastVisited.get(Calendar.YEAR), 1998)
            Assert.assertEquals(it.lastVisited.get(Calendar.MONTH), Calendar.SEPTEMBER)
            Assert.assertEquals(it.lastVisited.get(Calendar.DAY_OF_MONTH), 4)
        }
    }

    @Test
    fun updateLastVisitedDate() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 1998)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 4)
        }

        domainDao.updateLastVisitedDate("microsoft.com", calendar)

        val storedDomain = domainDao.getDomain("microsoft.com")
        storedDomain?.let {
            Assert.assertEquals(it.lastVisited.get(Calendar.YEAR), 1998)
            Assert.assertEquals(it.lastVisited.get(Calendar.MONTH), Calendar.SEPTEMBER)
            Assert.assertEquals(it.lastVisited.get(Calendar.DAY_OF_MONTH), 4)
        }
    }
}