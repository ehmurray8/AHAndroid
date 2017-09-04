package ehmsoftware.ahandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button tvBtn = (Button) findViewById(R.id.tv_btn);
        Button sockBtn = (Button) findViewById(R.id.sock_btn);
        Button alarmBtn = (Button) findViewById(R.id.alarm_btn);

        tvBtn.setOnClickListener(new TVOnClickListener());
        sockBtn.setOnClickListener(new SockOnClickListener());
        alarmBtn.setOnClickListener(new AlarmListener());
    }

    private class TVOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent launchTv = new Intent(MainActivity.this, TVRemoteActivity.class);
            MainActivity.this.startActivity(launchTv);
        }
    }

    private class SockOnClickListener implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent launchSock = new Intent(MainActivity.this, SocketRemoteActivity.class);
            MainActivity.this.startActivity(launchSock);
        }
    }

    private class AlarmListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent launchAlarm = new Intent(MainActivity.this, AlarmActivity.class);
            MainActivity.this.startActivity(launchAlarm);
        }
    }
}
