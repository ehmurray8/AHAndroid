package ehmsoftware.ahandroid;

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
import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends AppCompatActivity {

    private TextView alarmLabel;
    private static Timer timer = new Timer();
    private static boolean timerSet = false;
    private TimePicker timePicker;
    private static String timeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button setButton = (Button) findViewById(R.id.alarm_btn);
        setButton.setOnClickListener(new SetAlarm());

        this.alarmLabel = (TextView) findViewById(R.id.time_lbl);
        this.alarmLabel.setText(timeStr);
        this.timePicker = (TimePicker) findViewById(R.id.time_picker);
    }

    private class SetAlarm implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            int setHour = timePicker.getHour();
            int setMin = timePicker.getMinute();

            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            int currHour = calendar.get(Calendar.HOUR_OF_DAY);

            if(currHour > setHour) {
                //Set for tomorrow
                date = new Date();
                date.setHours(setHour);
                date.setMinutes(setMin);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                date = c.getTime();
            } else {
                date.setHours(setHour);
                date.setMinutes(setMin);
            }

            if(!timerSet) {
                timer.cancel();
                timer.purge();
                timer = new Timer();
            }

            String ampm = "AM";
            if(setHour > 12) {
                ampm = "PM";
                setHour -= 12;
            }

            timeStr = setHour + ":" + String.format("%02d", setMin) + " " + ampm;
            alarmLabel.setText(timeStr);

            timer.schedule(new AlarmTask(), date);
            timerSet = true;
        }
    }

    private class AlarmTask extends TimerTask
    {
        public void run()
        {
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
