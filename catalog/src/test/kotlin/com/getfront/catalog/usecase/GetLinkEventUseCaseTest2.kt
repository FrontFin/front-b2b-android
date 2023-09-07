package com.getfront.catalog.usecase

import com.getfront.catalog.MainCoroutineTest
import com.getfront.catalog.converter.JsonConverter
import com.getfront.catalog.entity.AccessTokenPayload
import com.getfront.catalog.entity.AccessTokenResponse
import com.getfront.catalog.entity.LinkEvent
import com.getfront.catalog.from
import com.getfront.catalog.readFile
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.IllegalStateException

class GetLinkEventUseCaseTest2 : MainCoroutineTest() {

    private val useCase = GetLinkEventUseCase(
        mainCoroutineRule.dispatcher,
        JsonConverter
    )

    @Test
    fun `test done`() = runTest {
        val result = useCase.launch("{'type':'done'}")
        assert(result.isSuccess)
        assert(result.getOrNull() == LinkEvent.Done)
    }

    @Test
    fun `test close`() = runTest {
        val result = useCase.launch("{'type':'close'}")
        assert(result.isSuccess)
        assert(result.getOrNull() == LinkEvent.Close)
    }

    @Test
    fun `test showClose`() = runTest {
        val result = useCase.launch("{'type':'showClose'}")
        assert(result.isSuccess)
        assert(result.getOrNull() == LinkEvent.ShowClose)
    }

    @Test
    fun `test Undefined`() = runTest {
        val result = useCase.launch("{'type':''}")
        assert(result.isSuccess)
        assert(result.getOrNull() == LinkEvent.Undefined)
    }

    @Test
    fun `test brokerageAccountAccessToken`() = runTest {
        val json = readFile("access-token.json")

        val result = useCase.launch(json)
        assert(result.isSuccess)

        val value = result.getOrNull()
        val payload = (value as LinkEvent.Payload).payload
        assert(payload is AccessTokenPayload)

        val response = Gson().from<AccessTokenResponse>(json)
        assert(response.payload == payload)
    }

    @Test
    fun `test transferFinished`() = runTest {

    }

    @Test
    fun `test onError throws correct message`() {
        val ex = assertThrows(IllegalStateException::class.java) {
            useCase.onError("{'errorMessage':'404'}")
        }
        assert(ex.message == "404")
    }
}