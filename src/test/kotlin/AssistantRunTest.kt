import com.fluxtah.ask.api.clients.openai.assistants.model.AssistantRun
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class AssistantRunTest {

    @Test
    fun testDeserializeRun() {
        val run = Json.decodeFromString<AssistantRun>(JSON_RUN)
        println(run)
    }
}

private val JSON_RUN = """
{
  "id": "run_NVLgBHCyoEy5mO8aMDm5NUsH",
  "object": "thread.run",
  "created_at": 1700752628,
  "assistant_id": "asst_RTs5JMyLSSYQ5KtoNf9UyEUd",
  "thread_id": "thread_VJOIAslnbRuP0KNRZPJ7vkD8",
  "status": "queued",
  "started_at": null,
  "expires_at": 1700753228,
  "cancelled_at": null,
  "failed_at": null,
  "completed_at": null,
  "last_error": null,
  "model": "gpt-4-1106-preview",
  "instructions": "Your role is to assistant the developer to write code and compile code, we shall use the gradle build system.\n\nno prose\n\n",
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "buildProject",
        "description": "Builds a software project",
        "parameters": {
          "type": "object",
          "properties": {},
          "required": []
        }
      }
    },
    {
      "type": "function",
      "function": {
        "name": "createDirectory",
        "description": "Creates a directory for a software project",
        "parameters": {
          "type": "object",
          "properties": {
            "directoryName": {
              "type": "string",
              "description": "The desired relative path and name of the project if it does not exist already"
            }
          },
          "required": [
            "directoryName"
          ]
        }
      }
    },
    {
      "type": "function",
      "function": {
        "name": "createFile",
        "description": "Creates a file for a software project",
        "parameters": {
          "type": "object",
          "properties": {
            "fileName": {
              "type": "string",
              "description": "The relative project path of the file"
            }
          },
          "required": [
            "fileName"
          ]
        }
      }
    },
    {
      "type": "function",
      "function": {
        "name": "writeFile",
        "description": "Creates and/or writes to a file for a software project",
        "parameters": {
          "type": "object",
          "properties": {
            "fileName": {
              "type": "string",
              "description": "The relative project path of the file to write to"
            },
            "fileContents": {
              "type": "string",
              "description": "The contents of the file"
            }
          },
          "required": [
            "fileName"
          ]
        }
      }
    }
  ],
  "file_ids": [],
  "metadata": {}
}
"""