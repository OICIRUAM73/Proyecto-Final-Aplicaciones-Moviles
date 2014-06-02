package org.devops.proyectofinal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.devops.proyectofinal.json.JsonParser;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrarActivity extends Activity {

	private UserRegisterTask aRegisterTask = null;

	// Values for email and password at the time of the login attempt.
	private String rNombre;
	private String rApellido;
	private String rNickname;
	private String rEmail;
	private String rPassword;
	private String rRepPassword;

	// UI references.
	private EditText rNombreView;
	private EditText rApellidoView;
	private EditText rNicknameView;
	private EditText rEmailView;
	private EditText rPasswordView;
	private EditText rRepPasswordView;
	private View rRegisterFormView;
	private View rRegisterStatusView;
	private TextView rRegisterStatusMessageView;
	private String mensaje = "";

	JsonParser jParser = new JsonParser();

	private static String url_create_product = Utils.baseurl
			+ "/android/registro";
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registrar);

		// Set up the register form.
		rNombreView = (EditText) findViewById(R.id.rnombre);
		rApellidoView = (EditText) findViewById(R.id.rapellido);
		rNicknameView = (EditText) findViewById(R.id.rnickname);
		rEmailView = (EditText) findViewById(R.id.remail);
		rPasswordView = (EditText) findViewById(R.id.rpassword);
		rRepPasswordView = (EditText) findViewById(R.id.rpassRep);

		rRegisterFormView = findViewById(R.id.register_form);
		rRegisterStatusView = findViewById(R.id.register_status);
		rRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.registrar, menu);
		return true;
	}

	
	public void attemptRegister() {
		if (aRegisterTask != null) {
			return;
		}

		// Reset errors.
		rEmailView.setError(null);
		rApellidoView.setError(null);
		rNicknameView.setError(null);
		rNombreView.setError(null);
		rPasswordView.setError(null);
		rRepPasswordView.setError(null);

		// Store values at the time of the login attempt.
		rNombre = rNombreView.getText().toString();
		rApellido = rApellidoView.getText().toString();
		rNickname = rNicknameView.getText().toString();
		rEmail = rEmailView.getText().toString();
		rPassword = rPasswordView.getText().toString();
		rRepPassword = rRepPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid repPassword.
		if (TextUtils.isEmpty(rPassword)) {
			rPasswordView.setError(getString(R.string.error_field_required));
			focusView = rPasswordView;
			cancel = true;
		} else if (!rPassword.equals(rRepPassword)) {
			rRepPasswordView
					.setError(getString(R.string.error_incorrect_password));
			focusView = rRepPasswordView;
			cancel = true;
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(rPassword)) {
			rPasswordView.setError(getString(R.string.error_field_required));
			focusView = rPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(rEmail)) {
			rEmailView.setError(getString(R.string.error_field_required));
			focusView = rEmailView;
			cancel = true;
		} else if (!rEmail.contains("@")) {
			rEmailView.setError(getString(R.string.error_invalid_email));
			focusView = rEmailView;
			cancel = true;
		}
		// Check for a valid nickname.
		if (TextUtils.isEmpty(rNickname)) {
			rNicknameView.setError(getString(R.string.error_field_required));
			focusView = rNicknameView;
			cancel = true;
		}
		// Check for a valid last name.
		if (TextUtils.isEmpty(rApellido)) {
			rApellidoView.setError(getString(R.string.error_field_required));
			focusView = rApellidoView;
			cancel = true;
		}
		// Check for a valid name.
		if (TextUtils.isEmpty(rNombre)) {
			rNombreView.setError(getString(R.string.error_field_required));
			focusView = rNombreView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			rRegisterStatusMessageView
					.setText("Registrando");
			showProgress(true);
			aRegisterTask = new UserRegisterTask();
			aRegisterTask.execute((Void) null);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			rRegisterStatusView.setVisibility(View.VISIBLE);
			rRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							rRegisterStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			rRegisterFormView.setVisibility(View.VISIBLE);
			rRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							rRegisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			rRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			rRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("name", rNombre));
			props.add(new BasicNameValuePair("lastname", rApellido));
			props.add(new BasicNameValuePair("nickname", rNickname));
			props.add(new BasicNameValuePair("email", rEmail));
			props.add(new BasicNameValuePair("password", rPassword));
			props.add(new BasicNameValuePair("passwordConfirm", rRepPassword));

			try {
				JSONObject json = jParser.makeHttpRequest(url_create_product,
						"POST", props);
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					return true;
				} else {
					mensaje = json.getString("message");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				mensaje = "No tiene una conexion activa";
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			aRegisterTask = null;
			showProgress(false);
			if (success) {
				Toast.makeText(getApplicationContext(), "Registro exitoso.",
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				finish();
				startActivity(i);
			} else {
				Toast.makeText(getApplicationContext(), mensaje,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			aRegisterTask = null;
			showProgress(false);
		}
	}

}
