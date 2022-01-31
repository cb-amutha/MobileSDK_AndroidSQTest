package com.chargebee.android.resources

import com.chargebee.android.Chargebee
import com.chargebee.android.ErrorDetail
import com.chargebee.android.exceptions.CBException
import com.chargebee.android.exceptions.ChargebeeResult
import com.chargebee.android.network.CBAuthResponse
import com.chargebee.android.network.CBAuthentication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch

@RunWith(MockitoJUnitRunner::class)
class AuthResourceTest {

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        Chargebee.configure(
            site = "cb-imay-test",
            publishableApiKey = "test_EojsGoGFeHoc3VpGPQDOZGAxYy3d0FF3",
            sdkKey = "cb-j53yhbfmtfhfhkmhow3ramecom"
        )

    }
    @After
    fun tearDown(){

    }
    @Test
    fun test_validateSdkKey_success(){

        val authentication = CBAuthentication("123","item","active","","","")
        val queryParam = "0000987657"
        val lock = CountDownLatch(1)
        CBAuthentication.isSDKKeyValid(queryParam) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List plans :"+it.data)
                    MatcherAssert.assertThat(
                        (it.data),
                        Matchers.instanceOf(CBAuthResponse::class.java)
                    )
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await()

        CoroutineScope(Dispatchers.IO).launch {
            Mockito.`when`(AuthResource().authenticate(queryParam)).thenReturn(
                ChargebeeResult.Success(
                    authentication
                )
            )
            Mockito.verify(AuthResource(), Mockito.times(1)).authenticate(queryParam)
        }
    }
    @Test
    fun test_validateSdkKey_error(){
        val exception = CBException(ErrorDetail("Error"))
        val queryParam = "0000987657"
        val lock = CountDownLatch(1)
        CBAuthentication.isSDKKeyValid(queryParam) {
            when (it) {
                is ChargebeeResult.Success -> {
                    lock.countDown()
                    System.out.println("List plans :"+it.data)
                    MatcherAssert.assertThat(
                        (it.data),
                        Matchers.instanceOf(CBAuthResponse::class.java)
                    )
                }
                is ChargebeeResult.Error -> {
                    lock.countDown()
                    System.out.println("Error :"+it.exp.message)
                }
            }
        }
        lock.await()

        CoroutineScope(Dispatchers.IO).launch {
            Mockito.`when`(AuthResource().authenticate(queryParam)).thenReturn(
                ChargebeeResult.Error(
                    exception
                )
            )
            Mockito.verify(AuthResource(), Mockito.times(1)).authenticate(queryParam)
        }
    }
}