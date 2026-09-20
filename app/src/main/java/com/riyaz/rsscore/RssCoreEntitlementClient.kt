package com.riyaz.rsscore

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class RssCoreEntitlementClient(private val context: Context) {
 companion object { private const val PREFS="rss-core-entitlements"; private const val SERVER_PREMIUM="server_premium"; private const val CORE_HOST="https://rsscore.cv" }
 fun cachedPremium(): Boolean = context.getSharedPreferences(PREFS,0).getBoolean(SERVER_PREMIUM,false)
 fun checkPremium(email:String, appKey:String, callback:(Result<Boolean>)->Unit) { if(email.isBlank()||appKey.isBlank()){callback(Result.success(false));return}; Thread { try { val url=URL(CORE_HOST+"/api/v1/entitlements/check?app_key="+URLEncoder.encode(appKey,"UTF-8")+"&email="+URLEncoder.encode(email.trim(),"UTF-8")); val c=url.openConnection() as HttpURLConnection; c.requestMethod="GET"; c.connectTimeout=10000; c.readTimeout=10000; c.setRequestProperty("Accept","application/json"); val body=c.inputStream.bufferedReader().use{it.readText()}; val premium=JSONObject(body).optBoolean("premium",false); context.getSharedPreferences(PREFS,0).edit().putBoolean(SERVER_PREMIUM,premium).apply(); callback(Result.success(premium)) } catch(t:Throwable){callback(Result.failure(t))} }.start() }
}
