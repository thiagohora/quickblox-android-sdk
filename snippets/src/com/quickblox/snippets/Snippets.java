package com.quickblox.snippets;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.quickblox.core.result.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Oleg Soroka
 * Date: 02.10.12
 * Time: 11:02
 */
public class Snippets {

    private static final String TAG = Snippet.class.getSimpleName();
    protected Context context;
    protected ArrayList<Snippet> snippets = new ArrayList<Snippet>();

    public void printResultToConsole(Result result) {
        String message = "";
        if (result.isSuccess()) {
            message = "[OK] Result is successful! You can cast result to specific result and extract data.";
            Toast.makeText(context, "[OK] Result is successful!", Toast.LENGTH_SHORT).show();
        } else {
            message = String.format("[ERROR %s] Request has been completed with errors: %s",
                    result.getStatusCode(), result.getErrors());
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, message);
    }

    public void handleErrors(Result result) {
        String message = String.format("[ERROR %s] Request has been completed with errors: %s",
                result.getStatusCode(), result.getErrors());
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // print
        Log.i(TAG,message);
    }

    public void handleErrors(List<String> errors) {
        String message = String.format("[ERROR] Request has been completed with errors: %s", errors);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // print
        Log.i(TAG, message);
    }


    public Snippets(Context context) {
        this.context = context;
    }

    public ArrayList<Snippet> getSnippets() {
        return snippets;
    }

    public void setSnippets(ArrayList<Snippet> snippets) {
        this.snippets = snippets;
    }

}