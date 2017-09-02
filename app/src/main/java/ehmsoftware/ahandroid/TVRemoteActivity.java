package ehmsoftware.ahandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.realtime.CompletionListener;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ErrorInfo;
import io.ably.lib.types.Message;

public class TVRemoteActivity extends AppCompatActivity {

    private static final String API_KEY = "NtYGvg.lADLCg:_YcwQxp-lniZ_xWY";
    private AblyRealtime realtime;
    public static String CHAN_STR = "AH_Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvremote);

        try {
            initAbly();
        } catch(AblyException e) {
            e.printStackTrace();
        }
    }

    private void initAbly() throws AblyException {
        this.realtime = new AblyRealtime(API_KEY);

        Channel channel = realtime.channels.get(CHAN_STR);
        channel.subscribe(new Channel.MessageListener() {
            @Override
            public void onMessage(Message message) {
                Log.v("ABLY", "Message received: " + message.data);
            }
        });
    }

    private static class ButtonClick implements View.OnClickListener {

        private String toastString;
        private Button button;
        private Context context;
        private Channel channel;

        private static Toast currToast = null;

        public ButtonClick(String toastString, Button button, Context context,
                           AblyRealtime realtime) {
            this.toastString = toastString;
            this.button = button;
            this.context = context;
            this.channel = realtime.channels.get(TVRemoteActivity.CHAN_STR);


            this.button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(currToast != null) {
                currToast.cancel();
            }

            try {
                this.channel.publish("update", toastString, new CompletionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("ABLY", "Message Sent");
                    }

                    @Override
                    public void onError(ErrorInfo reason) {
                        Log.d("ABLY", "Error sending message.");
                    }
                });
            } catch (AblyException e) {
                e.printStackTrace();
            }

            currToast = Toast.makeText(this.context, this.toastString, Toast.LENGTH_SHORT);
            currToast.show();
        }
    }
}
