/* SPDX-License-Identifier: GPL-3.0-only */
package devcat.catboard.cleaner

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * The only exported component in the network-capable companion.
 * The signature permission and package allowlist make the IPC caller explicit.
 */
class TranscriptCleanerService : Service() {
    override fun onBind(intent: Intent): IBinder = CleanerBinder()

    private inner class CleanerBinder : Binder() {
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code != CleanerProtocol.TRANSACTION_CLEAN) return super.onTransact(code, data, reply, flags)
            enforceCallingPermission(CleanerProtocol.PERMISSION, "Cleaner IPC requires the CatBoard signature permission")
            enforceAllowedCaller()
            data.enforceInterface(CleanerProtocol.DESCRIPTOR)
            val request = data.readBundle(javaClass.classLoader) ?: Bundle()
            val transcript = request.getString(CleanerProtocol.TEXT).orEmpty()
            val mode = request.getString(CleanerProtocol.MODE) ?: CleanerProtocol.MODE_NORMAL
            val response = Bundle()
            if (transcript.isBlank() || transcript.length > CleanerProtocol.MAX_INPUT_CHARS) {
                response.putString(CleanerProtocol.ERROR, "invalid_input")
            } else {
                try {
                    response.putString(CleanerProtocol.TEXT, GigaChatClient(this@TranscriptCleanerService).cleanupText(transcript, mode))
                } catch (failure: Exception) {
                    response.putString(CleanerProtocol.ERROR, failure.javaClass.simpleName)
                }
            }
            reply?.writeNoException()
            reply?.writeBundle(response)
            return true
        }
    }

    private fun enforceAllowedCaller() {
        val packages = packageManager.getPackagesForUid(Binder.getCallingUid()).orEmpty().toSet()
        check(packages.any { it == "devcat.catboard" || it == "devcat.catboard.debug" }) {
            "Unexpected cleaner caller"
        }
    }
}

internal const val CLEANER_PREFS = "gigachat_cleaner"
internal const val AUTHORIZATION_KEY = "gigachat_authorization_key"

internal object CleanerProtocol {
    const val DESCRIPTOR = "devcat.catboard.cleaner.ITranscriptCleaner"
    const val PERMISSION = "devcat.catboard.permission.CLEAN_TRANSCRIPT"
    const val TRANSACTION_CLEAN = IBinder.FIRST_CALL_TRANSACTION
    const val TEXT = "transcript"
    const val MODE = "mode"
    const val ERROR = "error"
    const val MODE_NORMAL = "normal"
    const val MAX_INPUT_CHARS = 6_000
}

private class GigaChatClient(private val service: Service) {
    companion object {
        private const val MAX_INPUT_CHARS = 6_000
        private const val TOKEN_TTL_MS = 25 * 60 * 1000L
        @Volatile private var token: String? = null
        @Volatile private var tokenExpiry = 0L
        @Volatile private var tokenKey: String? = null
    }

    fun cleanupText(input: String, mode: String): String {
        val key = service.getSharedPreferences(CLEANER_PREFS, 0)
            .getString(AUTHORIZATION_KEY, "").orEmpty().trim()
        check(key.isNotEmpty()) { "missing_authorization_key" }
        require(input.length <= MAX_INPUT_CHARS) { "input_too_long" }
        val response = postJson(
            "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
            mapOf("Authorization" to "Bearer ${accessToken(key)}"),
            JSONObject().put("model", "GigaChat-2-Lite").put("temperature", 0.2)
                .put("messages", JSONArray()
                    .put(JSONObject().put("role", "system").put("content", prompt(mode)))
                    .put(JSONObject().put("role", "user").put("content", input)))
        )
        return response.optJSONArray("choices")?.optJSONObject(0)
            ?.optJSONObject("message")?.optString("content")?.trim()
            ?.takeIf { it.isNotEmpty() } ?: error("empty_response")
    }

    private fun accessToken(key: String): String {
        val now = System.currentTimeMillis()
        if (token != null && tokenKey == key && now + 60_000 < tokenExpiry) return token!!
        val response = postForm(
            "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
            mapOf("Authorization" to "Basic $key", "RqUID" to UUID.randomUUID().toString()),
            "scope=GIGACHAT_API_PERS"
        )
        return response.optString("access_token").trim().also {
            require(it.isNotEmpty()) { "oauth_empty_token" }
            token = it
            tokenKey = key
            tokenExpiry = response.optLong("expires_at", now + TOKEN_TTL_MS)
                .let { expiry -> if (expiry < 1_000_000_000_000L) expiry * 1000 else expiry }
        }
    }

    private fun prompt(mode: String) = when (mode) {
        "light" -> "Исправь пунктуацию и очевидные опечатки. Не меняй смысл. Верни только исправленный текст без комментариев."
        "clean" -> "Сделай текст аккуратным и читаемым: исправь пунктуацию, явные ошибки, убери повторы и слова-паразиты, сохрани исходный смысл. Верни только исправленный текст без комментариев."
        else -> "Исправь пунктуацию и явные ошибки, убери лишние повторы и слова-паразиты. Сохрани смысл. Верни только исправленный текст без комментариев."
    }

    private fun postForm(url: String, headers: Map<String, String>, body: String) =
        post(url, headers + ("Content-Type" to "application/x-www-form-urlencoded"), body)

    private fun postJson(url: String, headers: Map<String, String>, body: JSONObject) =
        post(url, headers + ("Content-Type" to "application/json"), body.toString())

    private fun post(url: String, headers: Map<String, String>, body: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 2_000
            readTimeout = 5_000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            headers.forEach { (name, value) -> setRequestProperty(name, value) }
        }
        try {
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val raw = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) error("provider_http_$code")
            return JSONObject(raw)
        } catch (failure: IOException) {
            error("network_failure")
        } finally {
            connection.disconnect()
        }
    }
}
