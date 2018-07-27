package `in`.quadleo.attendance.Event

import `in`.quadleo.attendance.LoginActivity
import `in`.quadleo.attendance.R
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_events.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import org.json.JSONObject

class EventsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        setSupportActionBar(toolbar)
        doAsync {

            val pref = getSharedPreferences("event", 0)
            val token = pref.getString("access_token", "")
            val request = Request.Builder()
                    .url("https://test3.htycoons.in/api/list_events")   // webservice api
                    .header("Authorization", "Bearer $token")
                    .post(FormBody.Builder().build())
                    .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            // Log.d("EVENT",response.body()!!.string())
            if (response.body() != null) {
                val responseString = response.body()!!.string()
                val json = JSONObject(responseString)
                val eventJson = json.getJSONArray("events")
                val events = ArrayList<Event>()

                for (i in 0..eventJson.length() - 1) {
                    val event = Event(
                            eventJson.getJSONObject(i).getString("_id"),
                            eventJson.getJSONObject(i).getString("event_name"),
                            eventJson.getJSONObject(i).getString("date"),
                            eventJson.getJSONObject(i).getString("venue"),
                            eventJson.getJSONObject(i).getInt("days"),
                            eventJson.getJSONObject(i).getInt("no_of_participants")
                    )
                    events.add(event)

                }

                uiThread {
                    recyclerview.layoutManager = LinearLayoutManager(this@EventsActivity, LinearLayoutManager.VERTICAL, false)
                    val adapter = EventAdapter(events)
                    recyclerview.adapter = adapter
                }
            }

        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.logout -> {
                val pref = getSharedPreferences("event", 0)
                val editor = pref.edit()
                editor.putString("access_token", "")
                editor.apply()
                startActivity(intentFor<LoginActivity>())
                finish()
            }

        }

        return super.onOptionsItemSelected(item)
    }
}
