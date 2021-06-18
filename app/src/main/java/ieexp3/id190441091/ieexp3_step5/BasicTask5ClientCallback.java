package ieexp3.id190441091.ieexp3_step5;

/**
 * サンプルTCPクライアントコールバックインタフェース（Sample5用）
 *
 * @author Hidekazu Suzuki
 * @version 1.2 - 2019/05/22
 */
public interface BasicTask5ClientCallback {
    /**
     * doInBackground()の処理を開始する前に呼び出されるコールバックメソッド
     */
    void onPreExecute();

    /**
     * doInBackground()内でpublishProgress()が呼ばれたときに呼び出されるコールバックメソッド
     * @param values   Logに出力するメッセージ
     */
    void onProgressUpdate(String... values);

    /**
     * doInBackground()の処理が終了したときに呼び出されるコールバックメソッド
     * @param aVoid doInBackground()の戻り値
     */
    void onPostExecute(Void aVoid);
}
