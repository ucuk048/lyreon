package com.lyreon.desktop.yt

import com.lyreon.desktop.core.LyreonLog
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

internal object SignatureDecipher {
    private const val TAG = "SignatureDecipher"
    private val cache = ConcurrentHashMap<String, String>()

    private val SIG_FN = Pattern.compile(
        "(?:var\\s+)?([a-zA-Z0-9$]+)\\s*=\\s*function\\s*\\(([a-zA-Z0-9$]+)\\)\\{\\s*\\2=\\2\\.split\\(\"\"\\);([\\s\\S]*?)return \\2\\.join\\(\"\"\\)\\}"
    )
    private val SIG_FN_ALT = Pattern.compile(
        "function\\s*\\(([a-zA-Z0-9$]+)\\)\\{\\s*\\1=\\1\\.split\\(\"\"\\);([\\s\\S]*?)return \\1\\.join\\(\"\"\\)\\}"
    )

    fun decipherS(baseJs: String, s: String): String? {
        val key = "s:${baseJs.hashCode()}"
        val script = cache.getOrPut(key) {
            extractSignatureScript(baseJs) ?: ""
        }
        if (script.isBlank()) return null
        return runInRhino(script, "sig", s)
    }

    fun decipherN(baseJs: String, n: String): String? {
        val key = "n:${baseJs.hashCode()}"
        val script = cache.getOrPut(key) {
            extractNScript(baseJs) ?: ""
        }
        if (script.isBlank()) return null
        return runInRhino(script, "n", n)
    }

    private fun extractSignatureScript(baseJs: String): String? {
        val funcBody: String
        val rawFunc: String
        val m1 = SIG_FN.matcher(baseJs)
        if (m1.find()) {
            rawFunc = m1.group()
            funcBody = m1.group(3).orEmpty()
        } else {
            val m2 = SIG_FN_ALT.matcher(baseJs)
            if (m2.find()) {
                rawFunc = "var __sig = " + m2.group()
                funcBody = m2.group(2).orEmpty()
            } else {
                return null
            }
        }

        val objPattern = Pattern.compile("([a-zA-Z0-9$]+)\\.[a-zA-Z0-9$]+\\(")
        val objMatcher = objPattern.matcher(funcBody)
        val helperObjDef = if (objMatcher.find()) {
            val objName = objMatcher.group(1)
            val escapedObjName = Pattern.quote(objName)
            val helperPattern = Pattern.compile("(?:var\\s+|const\\s+|let\\s+)?$escapedObjName\\s*=\\s*\\{[\\s\\S]*?\\n*\\};")
            val hm = helperPattern.matcher(baseJs)
            if (hm.find()) hm.group() else ""
        } else ""

        val fullFn = if (rawFunc.startsWith("var __sig")) rawFunc else "var __sig = $rawFunc;"
        return "$helperObjDef\n$fullFn"
    }

    private fun extractNScript(baseJs: String): String? {
        val nPatterns = listOf(
            Pattern.compile("function\\s*\\([a-zA-Z0-9$]+\\)\\{[^{}]*\\}[\\s\\S]{0,150}?\\.join\\(\"\"\\)\\}"),
            Pattern.compile("([a-zA-Z0-9$]+)\\s*=\\s*function\\s*\\([a-zA-Z0-9$]+\\)\\{var [a-zA-Z0-9$]+=[a-zA-Z0-9$]+\\.split\\(\"\"\\)[\\s\\S]*?return [a-zA-Z0-9$]+\\.join\\(\"\"\\)\\}")
        )
        for (pattern in nPatterns) {
            val m = pattern.matcher(baseJs)
            if (m.find()) {
                val funcStr = m.group()
                val helperPattern = Pattern.compile("([a-zA-Z0-9$]+)\\.[a-zA-Z0-9$]+\\(")
                val objMatcher = helperPattern.matcher(funcStr)
                val helperObjDef = if (objMatcher.find()) {
                    val objName = objMatcher.group(1)
                    val escapedObjName = Pattern.quote(objName)
                    val hp = Pattern.compile("(?:var\\s+|const\\s+|let\\s+)?$escapedObjName\\s*=\\s*\\{[\\s\\S]*?\\n*\\};")
                    val hm = hp.matcher(baseJs)
                    if (hm.find()) hm.group() else ""
                } else ""
                return "$helperObjDef\nvar __n = $funcStr;"
            }
        }
        return null
    }

    private fun runInRhino(func: String, fnName: String, input: String): String? {
        return try {
            val cx = Context.enter()
            cx.optimizationLevel = -1
            try {
                val scope: Scriptable = cx.initStandardObjects()
                scope.put("window", scope, cx.newObject(scope))
                scope.put("globalThis", scope, scope)
                cx.evaluateString(scope, func, "def", 1, null)
                val quoted = input.replace("\\", "\\\\").replace("\"", "\\\"")
                val varName = if (fnName == "n") "__n" else "__sig"
                val call = "(typeof $varName==='function' ? $varName(String(\"$quoted\")) : String(\"$quoted\"))"
                val out = Context.jsToJava(cx.evaluateString(scope, call, "call", 1, null), Any::class.java)
                out?.toString()?.takeIf { it.isNotBlank() }
            } finally {
                Context.exit()
            }
        } catch (e: Exception) {
            LyreonLog.w(TAG, "Rhino eval ($fnName) gagal: ${e.message}")
            null
        }
    }
}
