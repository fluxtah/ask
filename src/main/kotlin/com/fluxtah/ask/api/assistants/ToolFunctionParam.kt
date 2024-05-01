package com.fluxtah.ask.api.assistants

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToolFunctionParam(val description: String)