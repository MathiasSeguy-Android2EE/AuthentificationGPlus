package com.android2ee.formation.authentification.gplus;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.moments.ItemScope;
//import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.people.Person;

/**
 * Good resources to read also for the new Api (post december 2014):
 * http://www.androidhive.info/2014/02/android-login-with-google-plus-account-1/
 * http://www.riskcompletefailure.com/2014/02/migrating-from-plusclient-to.html
 * http://android-developers.blogspot.fr/2014/02/new-client-api-model-in-google-play.html
 */
public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    /******************************************************************************************/
    /**
     * The Tag for log
     */
    private static final String TAG = "MainActivity";
    /******************************************************************************************/
    /** Managing the Google+ SignIn **************************************************************************/
    /******************************************************************************************/
    /**
     * The PlusClient
     */
    private GoogleApiClient mPlusClient;
    /**
     * The connectionResult returned by the last connection try
     */
    private ConnectionResult mConnectionResult;
    /**
     * The SignIn native button
     */
    private SignInButton signInButton;
    /**
     * The request code for the Intent connection launch by startActivityForResult
     */
    private final static int requestCodeResolverError = 12354;
    /**
     * A boolean to know is the user asked for the connection or if it has be done by code (ex: in
     * onStart)
     */
    private boolean connectionAskedByUser = false;
    /******************************************************************************************/
    /** Managing Button **************************************************************************/
    /******************************************************************************************/
    /**
     * The button disconect
     */
    private Button btnDisconnect;
    /**
     * The button revoke
     */
    private Button btnRevoke;
    /**
     * The button clear
     */
    private Button btnClear;
    /**
     * The string representation of the g+ profile of the owner
     */
    private TextView txvPersonPresentation;

    /******************************************************************************************/
    /** Managing life cycle **************************************************************************/
    /**
     * **************************************************************************************
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Build the Option
        Plus.PlusOptions plusOptions = new Plus.PlusOptions.Builder()
                .addActivityTypes("http://schemas.google.com/AddActivity",
                        "http://schemas.google.com/ReviewActivity")
                .build();
        //Build the client
        mPlusClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, plusOptions)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        txvPersonPresentation = (TextView) findViewById(R.id.txvPersonPresentation);
        // .setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE).build();
        // Add a listener to the SignInButton and link it to the connection
        signInButton = (SignInButton) findViewById(R.id.btnSignIn);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gPlusSignInClicked();

            }
        });

        // Add a listener to the SignOutButton and link it to the disconnection
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        // Add a listener to the SignOutButton and link it to the disconnection
        btnClear = (Button) findViewById(R.id.btnClearDefaultAccount);
        btnClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        // Add a listener to the revoke and link it to the disconnection
        btnRevoke = (Button) findViewById(R.id.btnRevoke);
        btnRevoke.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                revoke();
            }
        });

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        // Ensure calling that method in onStart not in onResume (because onResume is called to
        // often and to late in the
        // activity life cycle.
        // Begin the service connection: retrieve if the user is already connected (throug the web
        // or something else)
        // and/or just connect the user
        // It has to be done at the beginning of your activity/application to ensure an user is in
        if (!mPlusClient.isConnected() && !mPlusClient.isConnecting()) {
            // Be sure you'are not attempting to connect when you are connected or when you are
            // connecting
            // This method will try to connect, it succeeds only if the user has already grants the
            // applications' right
            // using the G+SignIn flow.
            // Else a ErrorIntent will be received
            mPlusClient.connect();
            // When the result is coming back, it will be handle by onConnected or
            // onConnectionFailed
        }
        // If is connecting or connected, also disable the signin button
        if (mPlusClient.isConnected() || mPlusClient.isConnecting()) {
            // manage buttons state
            manageButtonState(true);
        }

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        // sure to not leak, close the connection. Each time you call connect you should call
        // disconnect

        // Last case: Just disconnect the user, next call on connect, will connect him again
        // smoothly
        // -------------------------------------------------------------------------------------------
        // He will have the same account and the same grants as before
        // disconnect the user
        mPlusClient.disconnect();
        // Kill the connectionResult
        mConnectionResult = null;
        // manage buttons state
        manageButtonState(false);
    }

    /**
     * Th write moment mehod
	 * Never worked
	 * Now deprecated
	 * So just do nothing
     */
    private void writeMoment() {
//    ItemScope target = new ItemScope.Builder()
//            .setUrl("https://developers.google.com/+/web/snippet/examples/thing")
//            .build();
//    Moment moment = new Moment.Builder()
//            .setType("http://schemas.google.com/AddActivity")
//            .setTarget(target)
//            .build();
//    Plus.MomentsApi.write(mPlusClient,moment);
}
	/******************************************************************************************/
	/** Managing connection **************************************************************************/
	/******************************************************************************************/
	/**
	 * Called when the plus button is hit
	 */
	private void gPlusSignInClicked() {
		Log.e(TAG, "gPlusSignInClicked");
		Log.e(TAG, "mPlusClient.isConnected(): " + mPlusClient.isConnected() + " mConnectionResult==null "
				+ (mConnectionResult == null) + " => " + (!mPlusClient.isConnected() && mConnectionResult != null));
		connectionAskedByUser = true;
		if (!mPlusClient.isConnected() && !mPlusClient.isConnecting() && mConnectionResult != null) {
			// In this case you have already called mPlusClient.connect (because mConnRes!=null) but
			// it failed
			// So you try to launch the resolution of that failure by calling startResolutionFR
			// which you should display
			// the G+SignIn flow
			try {
				mConnectionResult.startResolutionForResult(this, requestCodeResolverError);
			} catch (SendIntentException e) {
				Log.e(TAG, "SendIntentException");
				// It also failed, you' re a lucky guy today,
				// So you restart the connection process again.
				// (trick:at this step you should know if there is a web-connection to stop trying
				// to connect)
				mConnectionResult = null;
				mPlusClient.connect();

			}
		} else if (!mPlusClient.isConnected() && !mPlusClient.isConnecting() && mConnectionResult == null) {
			// This case is called when you haven't already try to connect (meaning you don't have
			// the
			// mPlusClient.connect in your onResume or onStart method)
			// In our case, we will never go through this code
			// just try to connect
			mPlusClient.connect();
		} else {
			// Something forgotten or what ?
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This method is called when the activity of connection started by
		// mConnectionResult.startResolutionForResult(this, requestCodeResolverError); has finished
		// It has displayed the connection activity made by Google Fellow.
		Log.e(TAG, "onActivityResult");
		if (requestCode == requestCodeResolverError && resultCode == RESULT_OK) {
			Log.e(TAG, " onActivityResult RESULT_OK");
			// hey now you have the rights granted the call to connect will work
			mConnectionResult = null;
			mPlusClient.connect();
			// manage the buttons states
			manageButtonState(true);
		} else {
			// ensure the connection is reset, else you won't be able to log your user in
			// because of the condition if (!mPlusClient.isConnected() && mConnectionResult != null)
			// {
			// in the ClickListener of the SignInButton
			mConnectionResult = null;
			Log.e(TAG, "onActivityResult SendIntentException");
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/******************************************************************************************/
	/** Managing disconnection **************************************************************************/
	/******************************************************************************************/

	/**
	 * Called when the button disconnect is clicked
	 */
	private void clear() {
		if (mPlusClient.isConnected()) {
			// First case: Give your user the ability to choose an another account at the next
			// connection
			// -------------------------------------------------------------------------------------------
			// First reset the G+ account bound to the application
			// Then when the user connect again it can choose an already existing account
			Plus.AccountApi.clearDefaultAccount(mPlusClient);
			// You should call disconnect, else nothing will happens (an exception in fact)
			// disconnect the user
			mPlusClient.disconnect();
			// Kill the connectionResult
			mConnectionResult = null;
			// manage the buttons states
			manageButtonState(false);
		}
	}

	/**
	 * This method is called when the user click on the revoke button
	 */
	private void revoke() {
		Log.e(TAG, "gPlusSignOutClicked");
		if (mPlusClient.isConnected()) {

			// Second case: Give your user the ability to reset its grants for your application
			// -------------------------------------------------------------------------------------------
			// If you want to delete the authorizations the user gave to you
			// you should call that method. At the next connection, the user will give you the new
			// grants
			// (The activity G+Connection will be launched again and the user will grant your
			// application to access to
			// circles, data as if he was connecting for the first time to your app)
			// You should always give your user the ability to do such a reset on the way your
			// application acts in its
			// G+ account
			// It also call disconnect on mPlusClient. If you disconnect the client before the call
			// of onAccessRevoked,
			// the listener below will never work.
            Plus.AccountApi.revokeAccessAndDisconnect(mPlusClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Log.d(TAG, "Disconnected");
                }
            });

		}
	}

	/**
	 * This method is called when the user has is access revoked
	 * 
	 * @param status
	 *            of the revocation
	 */
	private void userAccessRevoked(ConnectionResult status) {
		Log.e(TAG, "userAccessRevoked returns " + status);
		// manage the buttons states
		manageButtonState(false);
	}

	/**
	 * Called when the button disconnect is clicked
	 */
	private void disconnect() {
		Log.e(TAG, "disconnect called and  mPlusClient.isConnected() " + mPlusClient.isConnected());
		if (mPlusClient.isConnected()) {
			// Last case: Just disconnect the user, next call on connect, will connect him again
			// smoothly
			// -------------------------------------------------------------------------------------------
			// He will have the same account and the same grants as before
			// disconnect the user
			mPlusClient.disconnect();
			// Kill the connectionResult
			mConnectionResult = null;

			// Tips: If you disconnect before clearing account, you'll have the
			// "java.lang.IllegalStateException: Not
			// connected. Call connect() and wait for onConnected() to be called." thrown in your
			// face.

			// manage the buttons states
			manageButtonState(false);
		}
	}

	/**
	 * Change the button state (enable or disable) according to the connection state
	 * 
	 * @param isConnected
	 */
	private void manageButtonState(boolean isConnected) {
		signInButton.setEnabled(!isConnected);
		btnClear.setEnabled(isConnected);
		btnDisconnect.setEnabled(isConnected);
		btnRevoke.setEnabled(isConnected);
		if (!isConnected) {
			txvPersonPresentation.setText("");
		}
	}

	/******************************************************************************************/
	/** Implementing GooglePlayServicesClient.ConnectionCallbacks **************************************************************************/
	/******************************************************************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onConnected(android
	 * .os.Bundle)
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		Person accountOwner = Plus.PeopleApi.getCurrentPerson(mPlusClient);
        txvPersonPresentation.setText("Connected "+mPlusClient.isConnected()+" and account is "+Plus.AccountApi.getAccountName(mPlusClient));

		if (null != accountOwner) {
			// The connection with the user is ok, you can explore him it
			// retrieve the user
			txvPersonPresentation.setText("Connected with " + accountOwner.getDisplayName() + " token "
					+ accountOwner.getId());
			// manage buttons state
			manageButtonState(true);
		}
//        Plus.PeopleApi.loadVisible(mPlusClient, People.OrderBy.ALPHABETICAL,null).setResultCallback(
//                new ResultCallback<People.LoadPeopleResult>() {
//                    @Override
//                    public void onResult(People.LoadPeopleResult loadPeopleResult) {
//                        loadPeopleResult.getNextPageToken();
//                        loadPeopleResult.getStatus().isSuccess();
//                        loadPeopleResult.getPersonBuffer();
//                    }
//                }
//        );

	}

    @Override
    public void onConnectionSuspended(int i) {
        //Don't know what to do with that
    }

	/******************************************************************************************/
	/** Implementing GooglePlayServicesClient.OnConnectionFailedListener **************************************************************************/
	/******************************************************************************************/
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener#
	 * onConnectionFailed(com.google
	 * .android.gms.common.ConnectionResult)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG,
				"onConnectionFailed " + result + " hasResolution: " + result.hashCode() + " errorCode: "
						+ result.getErrorCode());
		signInButton.setEnabled(true);
		// You can just set the mConnectionResult for your user to be able to connect using the
		// signInButton
		// @see gPlusSignInClicked()
		if (!connectionAskedByUser) {
			Log.e(TAG, "!connectionAskedByUser " + !connectionAskedByUser);
			// This case is when you want your user to click the g+connection button to connect
			// and not hide it by an automatic connection
			mConnectionResult = result;
		} else {
			// Or you want to automagicly display him the solution to its problem either by showing
			// the native SignIn
			// Activity or the createAccount one. It's handle by the google team throught the
			// GoogleService Api

			try {
				// if auto-connection failed then ensure to display the SignIn Activity to the user
				// for him to be able
				// to sign in if it's a Sign_In_Required else just try to find a solution
				Log.e(TAG, "result.hasResolution() " + result.hasResolution());
				if (result.hasResolution()) {
					result.startResolutionForResult(this, requestCodeResolverError);
				}
			} catch (SendIntentException e) {
				Log.e(TAG, "onConnectionFailed " + result, e);
			} finally {
				connectionAskedByUser = false;
			}
		}
	}
}
