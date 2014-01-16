package com.quickblox.snippets;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.quickblox.internal.core.exception.QBResponseException;

import java.util.List;

/**
 * Created by vfite on 16.01.14.
 */
public abstract class AsyncSnippet extends Snippet {
    private static final String TAG = AsyncSnippet.class.getSimpleName();
    QBResponseException exception;
    private Context context;

    public AsyncSnippet(String title, Context context) {
        super(title);
        this.context = context;
    }

    public void setException(QBResponseException exception){
         this.exception = exception;
    }

    @Override
    public void execute() {
        exception = null;
        (new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... objects) {
                executeAsync();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if(exception == null){
                    Log.i(TAG, ">>> executed successful");
                    Toast.makeText(context, " executed successful", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i(TAG, ">>> errors:"+exception.getLocalizedMessage());
                }
            }
        }).execute();
    }

    public abstract void executeAsync();
}
