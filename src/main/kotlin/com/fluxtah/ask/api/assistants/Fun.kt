package com.fluxtah.ask.api.assistants

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Fun(val description: String)