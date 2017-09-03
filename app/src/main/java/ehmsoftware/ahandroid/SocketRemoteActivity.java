package ehmsoftware.ahandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SocketRemoteActivity extends AppCompatActivity {

    private enum SockState {
        ON,
        OFF;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private enum SocketInfo {

        SOCK1_ON("Socket 1 On", R.id.sock1_on, 1, SockState.ON),
        SOCK1_OFF("Socket 1 Off", R.id.sock1_off, 1, SockState.OFF),
        SOCK2_ON("Overhead Light On", R.id.sock2_on, 2, SockState.ON),
        SOCK2_OFF("Overhead Light Off", R.id.sock2_off, 2, SockState.OFF),
        SOCK3_ON("Fan On", R.id.sock3_on, 3, SockState.ON),
        SOCK3_OFF("Fan Off", R.id.sock3_off, 3, SockState.OFF),
        SOCK4_ON("AC On", R.id.sock4_on, 4, SockState.ON),
        SOCK4_OFF("AC Off", R.id.sock4_off, 4, SockState.OFF),
        SOCK5_ON("Lamp On", R.id.sock5_on, 5, SockState.ON),
        SOCK5_OFF("Lamp Off", R.id.sock5_off, 5, SockState.OFF);

        private String toastString;
        private int btnId;
        private int sockNum;
        private SockState sockState;

        SocketInfo(String toastString, int btnId, int sockNum, SockState sockState) {
            this.toastString = toastString;
            this.btnId = btnId;
            this.sockNum = sockNum;
            this.sockState = sockState;
        }

        public String getToastString() {
            return this.toastString;
        }

        public int getId() {
            return this.btnId;
        }

        public int getSockNum() {
            return this.sockNum;
        }

        public SockState getSockState() {
            return this.sockState;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_remote);

        for(SocketInfo si : SocketInfo.values()) {
            Button button = (Button) findViewById(si.getId());
            new ButtonClick(si, this, button);
        }
    }

    private static class ButtonClick implements View.OnClickListener {

        private SocketInfo socketInfo;
        private Context context;
        private static Toast currToast = null;

        public ButtonClick(SocketInfo socketInfo, Context context,  Button button) {
            this.socketInfo = socketInfo;
            this.context = context;

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(currToast != null) {
                currToast.cancel();
            }

            new PostMessage().execute(this.socketInfo);

            currToast = Toast.makeText(this.context, this.socketInfo.getToastString(), Toast.LENGTH_SHORT);
            currToast.show();
        }
    }

    private static class PostMessage extends AsyncTask<SocketInfo, Void, HttpResponse<String>> {


        protected HttpResponse<String> doInBackground(SocketInfo... socketInfo) {

            HttpResponse<String> response = null;
            try {
                 response = Unirest.post("https://rest.ably.io/channels/" + AblyConstants.CHAN_STR
                                + "/messages")
                        .header("content-type", "application/json")
                        .header("authorization", AblyConstants.ABLY_AUTH)
                        .header("cache-control", "no-cache")
                        .body("{\n\t\"name\":\"Test\",\n\t\"data\": {\"Function Name\":\"Socket\",\"Socket Type\":\"" +
                                socketInfo[0].getSockNum() +"\",\"Socket State\":\"" +
                                socketInfo[0].getSockState().toString() + "\"}\n}")
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
