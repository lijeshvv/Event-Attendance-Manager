package `in`.quadleo.attendance

import `in`.quadleo.attendance.Event.EventsActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val pref=getSharedPreferences("event",0)
        val token=pref.getString("access_token","")
        if(token!="")
        {
            startActivity(intentFor<EventsActivity>())
            finish()
        }
    }
    fun doLogin(view:View)
    {
        if (username.text.toString().isEmpty() || password.text.toString().isEmpty())
        {

           toast("Invalid Username or Password")
        }
        else
        {
           login()
        }
    }
    fun login()
    {
progressBar.visibility=View.VISIBLE
        doAsync {

            //implementation 'com.squareup.okhttp3:okhttp:3.11.0'
            val body=FormBody.Builder()
                    .add("username",username.text.toString())
                    .add("password",password.text.toString())
                    .build()

            val request=Request.Builder()
                    .url("https://test3.htycoons.in/api/login")   // webservice api
                    .post(body)
                    .build()

            val client=OkHttpClient()
            val response=client.newCall(request).execute()
            uiThread {
               // toast(response.body()!!.string())
                when(response.code())
                {
                    200 -> {
                        if (response.body()!=null) {
                            val jsonResponse = JSONObject(response.body()!!.string())
                            val accessToken=jsonResponse.getString("access_token")
                            //Log.d("ACCESS",accessToken)
                            val pref=getSharedPreferences("event",0)
                            val editor=pref.edit()
                            editor.putString("access_token",accessToken)
                            editor.apply()
                            startActivity(intentFor<EventsActivity>())
                            finish()
                        }
                    }
                    400 -> {
                        AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Error")
                                .setMessage("An error Occured")
                                .setNeutralButton("OK"){dialog, which ->dialog.dismiss()  }.show()

                    }
                }
            }
        }

    }
}
