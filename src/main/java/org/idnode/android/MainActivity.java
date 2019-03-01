package org.idnode.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.idnode.android.provider.DbProvider;
import org.idnode.identity.Owner;

public class MainActivity extends AppCompatActivity {
    final private static int LOGIN = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, LOGIN);
    }

    @Override
    public void onActivityResult(int activity, int code, Intent result) {
        super.onActivityResult(activity, code, result);
        switch(activity) {
            case LOGIN: {
                Log.d("MainActivity", "Login result = " + (code == Activity.RESULT_OK ? "success" : "failed"));
                if (code == Activity.RESULT_OK) {
                    String user = result.getStringExtra(LoginActivity.USER);
                    Log.d("MainActivity", "User = " + user);

                    // initialize the owner
                    new OwnerInitializer(this).execute(user);

                    // initiate main menu
                    // TODO
                }
            }
            break;
        }
    }

    private class OwnerInitializer extends AsyncTask<String, Void, Void> {
        Context ctx;

        public OwnerInitializer(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(String... strings) {
            // read or generate private key from DB
            Owner.initialize(strings[0], new DbProvider(this.ctx));

            // make sure we have all values we need
            Owner.instance().getIdKey();
            Owner.instance().getEncKey();
            Owner.instance().getHistory("default");
            return null;
        }
    }
}
