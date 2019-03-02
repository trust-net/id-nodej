package org.idnode.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.idnode.android.provider.DbProvider;
import org.idnode.api.client.IdClient;
import org.idnode.api.dto.Endorsement;
import org.idnode.api.dto.Registration;
import org.idnode.attributes.StandardAttributes;
import org.idnode.identity.Owner;
import org.idnode.identity.SequenceHistory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.trustnet.util.Submitter;

public class MainActivity extends AppCompatActivity {
    final static private String TAG = "MainActivity";

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
        switch (activity) {
            case LOGIN: {
                Log.d(TAG, "Login result = " + (code == Activity.RESULT_OK ? "success" : "failed"));
                if (code == Activity.RESULT_OK) {
                    String user = result.getStringExtra(LoginActivity.USER);
                    Log.d(TAG, "User = " + user);

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

            // TODO: remove this after idnode starts persisting
            Owner.instance().resetHistory();

            // connect with Idnode app
            Log.d(TAG, "Connection status: " + IdClient.setBaseUrl("http://192.168.1.15:1080"));

            // check for registration of standard attributes, register if not present
            if (!Owner.instance().isEncKeyRegistered()) {
                if (!Owner.instance().registerEncKey()) {
                    Log.e(TAG, "Failed to register encryption key");
                }
            }
            Log.d(TAG, "Encryption key registered: " + Owner.instance().isEncKeyRegistered());

            if (Owner.instance().getRegsiteredFirstName() == null) {
                if (!Owner.instance().registerFirstName("Foo")) {
                    Log.e(TAG, "Failed to register first name");
                }
            }
            Log.d(TAG, "Firstname: " + Owner.instance().getRegsiteredFirstName());

            if (Owner.instance().getRegisteredLastName() == null) {
                if (!Owner.instance().registerLastName("Bar")) {
                    Log.e(TAG, "Failed to register last name");
                }
            }
            Log.d(TAG, "Lastname: " + Owner.instance().getRegisteredLastName());

            ResponseEntity<? extends Object> result = IdClient.instance().getEndorsement(Owner.instance().getPublicId(), StandardAttributes.PreferredEmail);
            Log.d(TAG, "PreferredEmail is " + (result.getStatusCode() == HttpStatus.OK ? ((Endorsement) result.getBody()).getEncValue() : result.getBody().toString()));
            return null;
        }
    }
}
