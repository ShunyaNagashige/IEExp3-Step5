package ieexp3.id190441091.ieexp3_step5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class BasicTask5Activity extends AppCompatActivity implements BasicTask5ClientCallback{

    /** IP addressを入力するエディットテキスト */
    protected EditText editIpAddress;
    /** Port numberを入力するエディットテキスト */
    protected EditText editPortNumber;
    /** Messageを入力するエディットテキスト */
    protected EditText editHole1;
    /** Messageを入力するエディットテキスト */
    protected EditText editHole2;
    /** Messageを入力するエディットテキスト */
    protected EditText editHole3;
    /** サーバに接続するボタン */
    protected Button buttonConnect;
    /** ロボットを制御するボタン */
    protected Button buttonRun;
    /** サーバから切断するボタン */
    protected Button buttonDisconnect;
    /** 状態を表すテキストビュー */
    protected TextView labelState;
    /** TCPクライアントタスク */
    private BasicTask5ClientTask task;

    public static String operation;

    private final int holeNum = 3;
    private String[] holes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_task5);

        // GUIコンポーネントを取得してインスタンス変数に設定
        editIpAddress = (EditText)findViewById(R.id.editIpAddress);
        editPortNumber = (EditText)findViewById(R.id.editPortNumber);
        editHole1 = (EditText)findViewById(R.id.editHole1);
        editHole2 = (EditText)findViewById(R.id.editHole2);
        editHole3 = (EditText)findViewById(R.id.editHole3);
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        buttonRun=(Button)findViewById(R.id.buttonRun);
        buttonDisconnect = (Button)findViewById(R.id.buttonDisconnect);
        labelState = (TextView)findViewById(R.id.labelState);

        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonRun.setEnabled(false);
        buttonDisconnect.setEnabled(false);

        // 次の行は消さないこと
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    /**
     * Connectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonConnect(View view) {
        holes = new String[holeNum];
        EditText[] editHoles = { editHole1,editHole2,editHole3 };

        for (int i = 0; i < holeNum; i++) {
            holes[i] = editHoles[i].getText().toString();
        }

        // サーバのIPアドレスとポート番号を取得
        String address = editIpAddress.getText().toString();
        int port = Integer.parseInt(editPortNumber.getText().toString());
        BasicTask5Activity.operation="connect";
        // TCPクライアントタスクを生成してバックグラウンドで実行（非同期処理）
        task = new BasicTask5ClientTask(holes,address, port, this);
        task.execute();
    }

    public void handleBottonRunConnect(View view) {
        try {
            if(task!=null)
                task=null;
            BasicTask5Activity.operation="run";

            // TCPクライアントタスクを生成してバックグラウンドで実行（非同期処理）
            task = new BasicTask5ClientTask();
            task.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Disconnectボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
    public void handleButtonDisconnect(View view) {
        try {
            if(task!=null)
                task=null;

            BasicTask5Activity.operation="disconnect";
            // TCPクライアントタスクを生成してバックグラウンドで実行（非同期処理）
            task = new BasicTask5ClientTask();
            task.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sendボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
//    public void handleButtonSend(View view) {
//        // 入力したメッセージを取得
//        String message = editMessage.getText().toString();
//        task.sendMessage(message);
//
//        editMessage.setText("");
//    }

    /**
     * Clearボタンをクリックした時に呼び出すイベントハンドラ
     * @param view
     */
//    public void handleButtonClear(View view) {
//        editLog.setText("");
//    }

    /**
     * TcpClientTask側のonPreExecute()からコールバックされるメソッド
     */
    @Override
    public void onPreExecute() {
        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(false);
        buttonRun.setEnabled(true);
        buttonDisconnect.setEnabled(true);
//        buttonSend.setEnabled(true);

        // トーストの表示
        Toast.makeText(this, "ClientTask is started!.", Toast.LENGTH_SHORT).show();
    }

    /**
     * TcpClientTask側のonProgressUpdate()からコールバックされるメソッド
     * @param values   Stateに出力するメッセージ
     */
    @Override
    public void onProgressUpdate(String... values) {
        labelState.setText(values[0]);
//        // メインアクティビティのLogにメッセージを設定または追記
//        if (editLog.length() == 0)
//            editLog.setText(values[0]);
//        else
//            editLog.append("\n" + values[0]);
    }

    /**
     * TcpClientTask側のonPostExecute()からコールバックされるメソッド
     * @param aVoid doInBackground()の戻り値
     */
    @Override
    public void onPostExecute(Void aVoid) {
        // トーストの表示
        Toast.makeText(this, "ClientTask is finished.", Toast.LENGTH_SHORT).show();

        // ボタンの有効/無効を設定
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
//        buttonSend.setEnabled(false);
    }

}