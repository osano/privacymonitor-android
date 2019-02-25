package com.osano.privacymonitor.data

import com.osano.privacymonitor.util.rootDomain
import org.hamcrest.core.IsEqual
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class RootDomainTest {

    @Test
    fun testRootDomains() {
        Assert.assertThat("some.subdomain.google.co.uk".rootDomain(), IsEqual.equalTo( "google.co.uk"))
        Assert.assertThat("x.y.z.a.b.blog.osano.com".rootDomain(), IsEqual.equalTo( "osano.com"))
        Assert.assertThat("www.google.co.uk".rootDomain(), IsEqual.equalTo( "google.co.uk"))
        Assert.assertThat("google.co.uk".rootDomain(), IsEqual.equalTo( "google.co.uk"))
        Assert.assertThat("www.google.com".rootDomain(), IsEqual.equalTo( "google.com"))
        Assert.assertThat("amazon.co.uk".rootDomain(), IsEqual.equalTo( "amazon.co.uk"))
        Assert.assertThat("www.amazon.co.uk".rootDomain(), IsEqual.equalTo( "amazon.co.uk"))
        Assert.assertThat("www.youtube.com".rootDomain(), IsEqual.equalTo( "youtube.com"))
        Assert.assertThat("m.youtube.com".rootDomain(), IsEqual.equalTo( "youtube.com"))
        Assert.assertThat("youtube.com".rootDomain(), IsEqual.equalTo( "youtube.com"))
        Assert.assertThat("www.joomla.subdomain.php.net".rootDomain(), IsEqual.equalTo( "php.net"))
        Assert.assertThat("foo.bar.com".rootDomain(), IsEqual.equalTo( "bar.com"))
        Assert.assertThat("foo.ca".rootDomain(), IsEqual.equalTo( "foo.ca"))
        Assert.assertThat("foo.bar.ca".rootDomain(), IsEqual.equalTo( "bar.ca"))
        Assert.assertThat("foo.blogspot.com".rootDomain(), IsEqual.equalTo( "blogspot.com"))
        Assert.assertThat("foo.blogspot.co.uk".rootDomain(), IsEqual.equalTo( "blogspot.co.uk"))
        Assert.assertThat("state.CA.us".rootDomain(), IsEqual.equalTo( "state.ca.us"))
        Assert.assertThat("www.state.pa.us".rootDomain(),IsEqual.equalTo( "state.pa.us"))
        Assert.assertThat("hello.pvt.k12.ca.us".rootDomain(), IsEqual.equalTo( "pvt.k12.ca.us"))
        Assert.assertThat("www4.yahoo.co.uk".rootDomain(), IsEqual.equalTo( "yahoo.co.uk"))
        Assert.assertThat("home.netscape.com".rootDomain(), IsEqual.equalTo( "netscape.com"))
        Assert.assertThat("web.MIT.edu".rootDomain(), IsEqual.equalTo( "mit.edu"))
        Assert.assertThat("utenti.blah.IT".rootDomain(), IsEqual.equalTo( "blah.it"))
        Assert.assertThat("dominio.com.co".rootDomain(), IsEqual.equalTo( "dominio.com.co"))
        Assert.assertThat("www.microsoft.com".rootDomain(), IsEqual.equalTo( "microsoft.com"))
        Assert.assertThat("msdn.microsoft.com".rootDomain(), IsEqual.equalTo( "microsoft.com"))
    }
}
