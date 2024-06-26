/*
 * Copyright (c) 2024 Ian Warwick
 * Released under the MIT license
 * https://opensource.org/licenses/MIT
 */

package com.fluxtah.ask.api

import com.fluxtah.ask.api.clients.openai.assistants.AssistantsApi
import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRun
import com.fluxtah.ask.api.clients.openai.assistants.model.RunStatus
import kotlinx.coroutines.delay

/**
 * Poll until the run status is no longer QUEUED or IN_PROGRESS
 */
suspend fun pollRunStatus(
    assistantsApi: AssistantsApi,
    currentThreadId: String,
    initialRunStatus: AssistantRun,
    onStatusChanged: (RunStatus) -> Unit
): AssistantRun {
    var run = initialRunStatus
    while (true) {
        run = assistantsApi.runs.getRun(currentThreadId, run.id)
        when (run.status) {
            RunStatus.QUEUED, RunStatus.IN_PROGRESS, RunStatus.CANCELLING -> {
                // These statuses imply waiting is needed. You can log or handle these differently if needed.
                onStatusChanged(run.status)
                delay(1000)
            }

            RunStatus.REQUIRES_ACTION, RunStatus.CANCELLED, RunStatus.FAILED, RunStatus.COMPLETED, RunStatus.EXPIRED, RunStatus.INCOMPLETE -> {
                onStatusChanged(run.status)
                return run
            }
        }
    }
}