package com.fluxtah.ask.api.assistants

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ToolFunction(val description: String)