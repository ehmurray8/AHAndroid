package ehmsoftware.ahandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmLabel;
    private static boolean timerSet = false;
    private TimePicker timePicker;
    private static String timeStr = "";
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button setButton = (Button) findViewById(R.id.alarm_btn);
        setButton.setOnClickListener(new SetAlarm());

        this.alarmLabel = (TextView) findViewById(R.id.time_lbl);
        this.alarmLabel.setText(timeStr);
        this.timePicker = (TimePicker) findViewById(R.id.time_picker);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
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

            if(timerSet) {
                Intent alarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, alarmIntent, 0);
                manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);
            }

            String ampm = "AM";
            if(setHour > 12) {
                ampm = "PM";
                setHour -= 12;
            }

            timeStr = setHour + ":" + String.format("%02d", setMin) + " " + ampm;
            alarmLabel.setText(timeStr);

            manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            manager.set(AlarmManager.RTC_WAKEUP, timeToSetMillis, pendingIntent);

            timerSet = true;
        }
    }

    public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new PostMessage().execute();
            timerSet = false;
            timeStr = "";
        }
    }

    private static class PostMessage extends AsyncTask<Void, Void, HttpResponse<String>> {


        protected HttpResponse<String> doInBackground(Void... socketInfo) {

            HttpResponse<String> response = null;
            try {
                response = Unirest.post("https://rest.ably.io/channels/" + AblyConstants.CHAN_STR
                        + "/messages")
                        .header("content-type", "application/json")
                        .header("authorization", AblyConstants.ABLY_AUTH)
                        .header("cache-control", "no-cache")
                        .body("{\n\t\"name\":\"Test\",\n\t\"data\": {\"Function Name\":\"Awake\"}\n}")
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
