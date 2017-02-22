package aws.vav.com.androidawsconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static String MY_BUCKET     = "vaibhavtestbucket";
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPostExecute(Object o) {
                dialog.dismiss();
                super.onPostExecute(o);
            }

            protected void onPreExecute() {
                dialog = ProgressDialog.show(MainActivity.this,
                        "refreshing",
                        "please wait");
            }
            @Override
            protected Object doInBackground(Object[] params) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.txt");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),    /* get the context for the application */
                        "us-east-1:7ef74323-6dfb-42bf-946e-8e572f1934b7",    /* Identity Pool ID */
                        Regions.US_EAST_1);      /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/

                AmazonS3Client s3  = new AmazonS3Client(credentialsProvider);
                TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
                TransferObserver observer = transferUtility.upload(
                        MY_BUCKET,     /* The bucket to upload to */
                        file.getName(),file    /* The key for the uploaded object */
                               /* The file where the data to upload exists */
                );
                Log.d("Test", observer.getId() + " " + observer.getBytesTransferred());
                observer.setTransferListener(new TransferListener(){

                    @Override
                    public void onStateChanged(int id, TransferState state) {

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        //int percentage = (int) (bytesCurrent/bytesTotal * 100);
                        //Display percentage transfered to user
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        ex.printStackTrace();
                    }

                });
                return null;
            }



        };
        asyncTask.execute();
        setContentView(R.layout.activity_main);




    }
}
