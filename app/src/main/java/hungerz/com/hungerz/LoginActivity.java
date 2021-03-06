package hungerz.com.hungerz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    protected Button signIn;
    protected ProgressBar progress;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        initView();
        mAuth = FirebaseAuth.getInstance();
        configureSignIn();
        progress.setVisibility(View.INVISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userReference = database.getReference("users");
        userReference.keepSynced(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in) {
            progress.setVisibility(View.VISIBLE);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void initView() {
        signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(LoginActivity.this);
        progress = (ProgressBar) findViewById(R.id.progress);
    }


    public void configureSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getBaseContext().getResources().getString(R.string.web_client_id2))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this,"Connection Failed", Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("userId", user.getUid());
                            try {
                                userData.put("name", user.getDisplayName());
                            } catch (Exception e) {


                            }
                            try {
                                userData.put("email", user.getEmail());
                            } catch (Exception e) {

                            }
                            try {
                                userData.put("phone", user.getPhoneNumber());
                            } catch (Exception e) {

                            }

                            userReference.child(user.getUid()).updateChildren(userData);
                            Intent intent = new Intent(getBaseContext(), FoodInformationCollection.class);
                            startActivity(intent);
                            progress.setVisibility(View.INVISIBLE);


                        } else {
                            // If sign in fails, display a message to the user.
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        progress.setVisibility(View.INVISIBLE);

        Toast.makeText(this,"Connection Failed", Toast.LENGTH_SHORT).show();

    }
}
