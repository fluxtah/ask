package com.fluxtah.ask.api.tools.fn

import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRunStepDetails.ToolCalls.ToolCallDetails.FunctionToolCallDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions

class FunctionInvokerTest {

    class TestTarget {
        fun testFunction(param1: String, param2: Int): String {
            return "param1: $param1, param2: $param2"
        }

        fun testFunction2(param1: String, param2: String): String {
            return "param1: $param1, param2: $param2"
        }

        @Serializable
        data class TestTargetData(val param1: String, val param2: Int)

        fun testFunctionData(data: TestTargetData): String {
            return "param1: ${data.param1}, param2: ${data.param2}"
        }

        fun testFunctionMixed(param1: String, data: TestTargetData): String {
            return "param1: $param1, param2: ${data.param2}"
        }
    }

    private val functionInvoker = FunctionInvoker()

    @Test
    fun `test invokeFunction`() {
        val target = TestTarget()
        val callDetails = FunctionToolCallDetails(
            function = FunctionToolCallDetails.FunctionSpec(
                name = "testFunction",
                arguments = "{\"param1\":\"test\",\"param2\":42}"
            ),
            id = "testId"
        )

        val result = functionInvoker.invokeFunction(target, callDetails)
        assertEquals("param1: test, param2: 42", result)
    }

    @Test
    fun `test invokeFunction missing arg`() {
        val target = TestTarget()
        val callDetails = FunctionToolCallDetails(
            function = FunctionToolCallDetails.FunctionSpec(
                name = "testFunction",
                arguments = "{\"param1\":\"test\"}"
            ),
            id = "testId"
        )

        val result = functionInvoker.invokeFunction(target, callDetails)
        assertEquals("param1: test, param2: 0", result)
    }

    @Test
    fun `test invokeFunction2 missing arg`() {
        val target = TestTarget()
        val callDetails = FunctionToolCallDetails(
            function = FunctionToolCallDetails.FunctionSpec(
                name = "testFunction2",
                arguments = "{\"param1\":\"test\"}"
            ),
            id = "testId"
        )

        val result = functionInvoker.invokeFunction(target, callDetails)
        assertEquals("param1: test, param2: ", result)
    }


    @Test
    fun `test invokeFunction with data class`() {
        val target = TestTarget()
        val callDetails = FunctionToolCallDetails(
            function = FunctionToolCallDetails.FunctionSpec(
                name = "testFunctionData",
                arguments = "{\"data\":{\"param1\":\"test\",\"param2\":42}}"
            ),
            id = "testId"
        )

        val result = functionInvoker.invokeFunction(target, callDetails)
        assertEquals("param1: test, param2: 42", result)
    }

    @Test
    fun `test invokeFunction with mixed arguments`() {
        val target = TestTarget()
        val callDetails = FunctionToolCallDetails(
            function = FunctionToolCallDetails.FunctionSpec(
                name = "testFunctionMixed",
                arguments = "{\"param1\":\"test\",\"data\":{\"param1\":\"test\",\"param2\":42}}"
            ),
            id = "testId"
        )

        val result = functionInvoker.invokeFunction(target, callDetails)
        assertEquals("param1: test, param2: 42", result)
    }
}
