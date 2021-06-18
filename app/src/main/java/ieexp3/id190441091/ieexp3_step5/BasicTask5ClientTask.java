package ieexp3.id190441091.ieexp3_step5;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * サンプルTCPクライアントタスク（Sample5用）
 *
 * @author Hidekazu Suzuki
 * @version 1.3 - 2019/05/22
 */
public class BasicTask5ClientTask extends AsyncTask<Void, String, Void> {
    /** サーバのIPアドレス */
    private String addr;
    /** サーバのポート番号 */
    private int port;
    /** ソケット */
    private Socket socket;
    /** データ入力用ストリーム */
    private DataInputStream dis;
    /** データ出力用ストリーム */
    private DataOutputStream dos;
    /** ソケットの受信ループフラグ */
    private boolean isLoop;
    /** ホール番号 */
    private String[] holes;

    private BasicTask5ClientCallback callback;

    public BasicTask5ClientTask(String[] holes,String addr, int port, BasicTask5ClientCallback callback) {
        for (String hole : holes) {
            if (Integer.parseInt(hole) <= 0 || Integer.parseInt(hole) > 6)
                throw new IllegalArgumentException("1から6の数字を入力してください。");
        }

        if(addr.equals("")) {
            throw new IllegalArgumentException("IPアドレスが空です");
        }

        if(port==0) {
            throw new IllegalArgumentException("ポート番号が空です");
        }

        this.holes = holes;
        this.addr = addr;
        this.port = port;
        this.callback = callback;
    }

    public BasicTask5ClientTask() { }

    /**
     * バックグラウンド処理
     * （アクティビティのUI操作は不可能）
     * @param params    未使用（Void型の配列）
     * @return  null
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            // サーバへ接続（3秒でタイムアウト）
            socket = new Socket();
            socket.connect(new InetSocketAddress(addr, port), 3000);

            try {
                // コネクションが確立したらソケットの入出力ストリームにバッファ付ストリームと
                // データ入出力ストリームを連結
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

//                // UIを操作したい場合はpublishProgress()を実行してonProgressUpdate()をコールバック
//                publishProgress("established a connection with "
//                        + socket.getInetAddress().toString() + ":" + socket.getPort() + "!");
                String cmd=null;

                if (BasicTask5Activity.operation == "connect") {
                    cmd = createCmd(BasicTask5Activity.operation);

                    // データの送信
                    dos.writeUTF(cmd);
                    dos.flush();

                    isLoop = true;
                    BasicTask5Activity.operation = "no operation";
                    System.out.println("Connected completely.");
                    publishProgress("State:Connected.");
                }

                // サーバからの応答受信ループ（ノンブロッキング）
                while (isLoop) {
                    switch (BasicTask5Activity.operation) {
                        case "run":
                            System.out.println("Start!");
                            publishProgress("State:Running.");
                            cmd = createCmd(BasicTask5Activity.operation);

                            // データの送信
                            dos.writeUTF(cmd);
                            dos.flush();

                            BasicTask5Activity.operation = "no operation";
                            publishProgress("State:Run.");
                            break;

                        case "disconnect":
                            cmd = createCmd(BasicTask5Activity.operation);

                            // データの送信
                            dos.writeUTF(cmd);
                            dos.flush();

                            isLoop = false;
                            System.out.println("Disconnected completely.");
                            BasicTask5Activity.operation = "no operation";
                            publishProgress("State:Disonnected.");
                            break;

                        default:
                            break;
                    }

//                    // データ入力ストリームに読み込み可能なデータがあるか確認
//                    if (dis.available() > 0) {
//                        // データを受信
//                        int length = dis.readInt();
//                        publishProgress("received length = " + length);
//                    } else {
//                        // 100ms待機（過負荷対策）
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException ie) {
//                            Log.e("Sample5", "error", ie);
//                        }
//                    }
                }
            } catch (IOException e) {
                Log.e("Sample5", "error", e);
                publishProgress("[ERROR] " + e.getMessage());
            } finally {
                // ソケットを閉じる
                close();
            }
        } catch (Exception e) {
            Log.e("Sample5", "error", e);
            publishProgress("[ERROR] " + e.getMessage());
        }

        return null;
    }

    /**
     * バックグラウンド処理を行う前の事前処理
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null)
            callback.onPreExecute();
    }

    /**
     * doInBackground()の処理が終了したときに呼び出されるメソッド
     * @param aVoid doInBackground()の戻り値
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (callback != null)
            callback.onPostExecute(aVoid);
    }

    /**
     * doInBackground()内でpublishProgress()が呼ばれたときに呼び出されるメソッド
     * @param values   Logに出力するメッセージ
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (callback != null)
            callback.onProgressUpdate(values);
    }

    /**
     * メッセージをサーバへ送信するメソッド
     * @param message   メッセージ
     */
    public void sendMessage(String message) {
        if (message != null) {
            try {
                // メッセージをサーバへ送信
                dos.writeUTF(message);
                dos.flush();

                publishProgress("send the message '" + message + "' to the server.");
            } catch (IOException e) {
                Log.e("Sample5", "error", e);
            }
        }
    }

    /**
     * 受信処理ループを停止するメソッド
     */
    public void stop() {
        this.isLoop = false;
    }

    //コマンドの生成
    private String createCmd(String operation) {
        String cmd = operation;

        if (operation == "connect") {
            for (String hole : this.holes) {
                cmd += " ";
                cmd += hole;
            }
        }

        return cmd;
    }

    /**
     * ソケット及びストリームを閉じるメソッド
     */
    private void close() {
        // ソケットを閉じる
        if (socket != null && socket.isConnected())
            try {
                socket.close();
                //publishProgress("socket is closed.");
            } catch (IOException e) {
                Log.e("Sample5", "error", e);
            }

        // データ入力ストリームを閉じる
        if (dis != null)
            try {
                dis.close();
            } catch (IOException e) {
                Log.e("Sample5", "error", e);
            }

        // データ出力ストリームを閉じる
        if (dos != null)
            try {
                dos.flush();
                dos.close();
            } catch (IOException e) {
                Log.e("Sample5", "error", e);
            }
    }

}
