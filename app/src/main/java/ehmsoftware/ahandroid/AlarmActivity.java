package ehmsoftware.ahandroid;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlarmActivity extends AppCompatActivity {

    private static TextView alarmLabel;
    private TimePicker timePicker;
    private EditText musicEt;
    private static String timeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button setButton = (Button) findViewById(R.id.alarm_btn);
        setButton.setOnClickListener(new SetAlarm());

        alarmLabel = (TextView) findViewById(R.id.time_lbl);
        alarmLabel.setText(timeStr);

        musicEt = (EditText) findViewById(R.id.music_txt);

        this.timePicker = (TimePicker) findViewById(R.id.time_picker);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences sp = getSharedPreferences("timer_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("time_set", timeStr);
        editor.apply();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("timer_prefs", Activity.MODE_PRIVATE);
        timeStr = sp.getString("time_set", "");
        alarmLabel.setText(timeStr);
    }

    private class SetAlarm implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            int setHour = timePicker.getHour();
            int setMin = timePicker.getMinute();

            Date date = new Date();
            Calendar calendarNow = GregorianCalendar.getInstance();
            calendarNow.setTime(date);
            int currHour = calendarNow.get(Calendar.HOUR_OF_DAY);


            Calendar calendarSet = Calendar.getInstance();
            calendarSet.set(calendarNow.get(Calendar.YEAR), calendarNow.get(Calendar.MONTH),
                    calendarNow.get(Calendar.DAY_OF_MONTH), setHour, setMin);

            if(currHour > setHour) {
                //Set for tomorrow
                calendarSet.add(Calendar.DATE, 1);
            }

            long timeToSetMillis = calendarSet.getTimeInMillis();

            PostMessage post = new PostMessage();
            post.setTime(timeToSetMillis);
            post.setMusic(musicEt.getText().toString());
            post.execute();

            String ampm = "AM";
            if(setHour > 12) {
                ampm = "PM";
                setHour -= 12;
            }

            if(setHour == 0) {
                setHour = 12;
            }

            timeStr = setHour + ":" + String.format("%02d", setMin) + " " + ampm;
            alarmLabel.setText(timeStr);


        }
    }

    private static class PostMessage extends AsyncTask<Void, Void, HttpResponse<String>> {

        private static long time = 0;
        private static String music = "";

        protected void setTime(long t) {
            time = t;
        }

        protected void setMusic(String m) {
            music = m;
        }

        protected HttpResponse<String> doInBackground(Void... socketInfo) {

            HttpResponse<String> response = null;
            try {
                Log.v("ALARM SET", "Time: " + time);
                String body = "{\n\t\"name\":\"Test\",\n\t\"data\": {\"Alarm\":" + time;
                if(music != "") {
                    body += ", \"Music\": \"" + music + "\"";
                }
                body += "}\n}";

                response = Unirest.post("https://rest.ably.io/channels/" + AblyConstants.ALARM_CHAN_STR
                        + "/messages")
                        .header("content-type", "application/json")
                        .header("authorization", AblyConstants.ABLY_AUTH)
                        .header("cache-control", "no-cache")
                        .body(body)
                        .asString();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(HttpResponse<String> response) {

        }
    }
}
