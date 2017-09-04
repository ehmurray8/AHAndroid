package ehmsoftware.ahandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TVRemoteActivity extends AppCompatActivity {

    private enum Key {

        POWER("POWER", R.id.power_btn),
        INPUT("MEDIA", R.id.input_btn),
        ONE("1", R.id.btn_1),
        TWO("2", R.id.btn_2),
        THREE("3", R.id.btn_3),
        FOUR("4", R.id.btn_4),
        FIVE("5", R.id.btn_5),
        SIX("6", R.id.btn_6),
        SEVEN("7", R.id.btn_7),
        EIGHT("8", R.id.btn_8),
        NINE("9", R.id.btn_9),
        DOT("DOT", R.id.btn_dot),
        ZERO("0", R.id.btn_0),
        MENU("MENU", R.id.menu_btn),
        UP("UP", R.id.up_arrow_btn),
        HOME("HOME", R.id.home_btn),
        LEFT("LEFT", R.id.left_arrow_btn),
        ENTER("ENTER", R.id.enter_btn),
        RIGHT("RIGHT", R.id.right_arrow_btn),
        EXIT("EXIT", R.id.exit_btn),
        DOWN("DOWN", R.id.down_arrow_btn),
        INFO("INFO", R.id.info_btn),
        VOL_UP("VOLUMEUP", R.id.vol_up_btn),
        CHAN_UP("CHANNELUP", R.id.chan_up_btn),
        MUTE("MUTE", R.id.mute_btn),
        VOL_DOWN("VOLUMEDOWN", R.id.vol_down_btn),
        CHAN_DOWN("CHANNELDOWN", R.id.chan_down_btn),
        PLAY("PLAY", R.id.play_btn),
        PAUSE("PAUSE", R.id.pause_btn),
        STOP("STOP", R.id.stop_btn),
        AUDIO("AUDIO", R.id.audio_btn),
        FFWORD("FASTFORWARD", R.id.ffword_btn),
        RWIND("REWIND", R.id.rwind_btn),
        NEXT("NEXT", R.id.chap_next_btn),
        PREVIOUS("PREVIOUS", R.id.chap_back_btn);

        private String key;
        private int btnId;

        Key(String key, int btnId) {
            this.key = key;
            this.btnId = btnId;
        }

        public int getId() {
            return this.btnId;
        }

        public String getKey() {
            return this.key;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvremote);

        for(Key k : Key.values()) {
            Button button = (Button) findViewById(k.getId());
            new ButtonClick(k, this, button);
        }
    }

    private static class ButtonClick implements View.OnClickListener {

        private Key key;
        private Button button;
        private Context context;

        public ButtonClick(Key key, Context context, Button button) {
            this.key = key;
            this.button = button;
            this.context = context;

            this.button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new PostMessage().execute(this.key);
        }
    }

    private static class PostMessage extends AsyncTask<Key, Void, HttpResponse<String>> {


        protected HttpResponse<String> doInBackground(Key... socketInfo) {

            HttpResponse<String> response = null;
            try {
                response = Unirest.post("https://rest.ably.io/channels/" + AblyConstants.CHAN_STR
                                + "/messages")
                        .header("content-type", "application/json")
                        .header("authorization", AblyConstants.ABLY_AUTH)
                        .header("cache-control", "no-cache")
                        .body("{\n\t\"name\":\"Test\",\n\t\"data\": {\"Function Name\":\"Key\",\"Key Type\":\"" +
                                socketInfo[0].getKey() +"\"}\n}")
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
