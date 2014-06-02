package org.devops.proyectofinal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.devops.proyectofinal.entidades.Profile;
import org.devops.proyectofinal.imageLoader.ImageLoader;
import org.devops.proyectofinal.json.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	private int SELECT_IMAGE = 237487;
	private int TAKE_PICTURE = 829038;

	private UserLoginTask mAuthTask = null;
	private updateUserTask uAuthTask = null;

	JsonParser jParser = new JsonParser();
	private static String url_getProfile = Utils.baseurl
			+ "/android/profileUsuario";
	private static String url_updateProfile = Utils.baseurl
			+ "/android/updateProfileUsuario";
	private static final String TAG_SUCCESS = "success";
	private ImageView imgPhoto;
	private TextView txtPhoto;
	private Button btnPhoto, uploadButton;
	private String imagepath = null;
	private TextView messageText;
	private TextView usuario;
	private EditText email;
	private int serverResponseCode = 0;
	private ProgressDialog dialog = null;
	private String upLoadServerUri = null;
	private EditText desc;
	Profile profile = new Profile();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camara);
		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
		profile();
		usuario = (TextView) findViewById(R.id.textView1);
		email = (EditText) findViewById(R.id.editText2);
		desc = (EditText) findViewById(R.id.editText1);
		imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
		// imgPhoto.setImageURI(uri);
		txtPhoto = (TextView) findViewById(R.id.txtPhoto);
		messageText = (TextView) findViewById(R.id.messageText);

		btnPhoto = (Button) findViewById(R.id.btnPhoto);
		uploadButton = (Button) findViewById(R.id.Guardar);
		btnPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogPhoto();
			}
		});

		// btnPhoto.setOnClickListener(this);
		uploadButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				profile.setDescripcion(desc.getText().toString());
				profile.setEmail(email.getText().toString());
				updateProfile();

			}
		});

		upLoadServerUri = Utils.baseurl + "/android/addPhotosAndroid";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (requestCode == SELECT_IMAGE)
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImageUri = data.getData();
					imagepath = getPath(selectedImageUri);
					Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
					imgPhoto.setImageBitmap(bitmap);
					// imgPhoto.setImageURI(selectedImage);

					dialog = ProgressDialog.show(ProfileActivity.this, "",
							"Uploading file...", true);
					messageText.setText("uploading started.....");
					new Thread(new Runnable() {
						public void run() {

							uploadFile(imagepath);

						}
					}).start();
					uploadFile(imagepath);
				}
			if (requestCode == TAKE_PICTURE)
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImageUri = data.getData();
					imagepath = getPath(selectedImageUri);
					Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
					// txtPhoto.setText(getPath(selectedImage));
					// imgPhoto.setImageURI(selectedImage);
					imgPhoto.setImageBitmap(bitmap);

					dialog = ProgressDialog.show(ProfileActivity.this, "",
							"Uploading file...", true);
					messageText.setText("uploading started.....");
					new Thread(new Runnable() {
						public void run() {

							uploadFile(imagepath);

						}
					}).start();

					uploadFile(imagepath);
				}
		} catch (Exception e) {
		}
	}

	private void dialogPhoto() {
		try {
			final CharSequence[] items = { "Seleccionar de la galeria",
					"Hacer una foto" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Seleccionar una foto");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					case 0:
						Intent intent = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(intent, SELECT_IMAGE);
						break;
					case 1:
						startActivityForResult(
								new Intent(
										android.provider.MediaStore.ACTION_IMAGE_CAPTURE),
								TAKE_PICTURE);
						break;
					}

				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public int uploadFile(String sourceFileUri) {

		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {

			dialog.dismiss();

			Log.e("uploadFile", "Source File not exist :" + imagepath);

			runOnUiThread(new Runnable() {
				public void run() {
					messageText.setText("Source File not exist :" + imagepath);
				}
			});

			return 0;

		} else {

			try {
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				URL url = new URL(upLoadServerUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");

				// paramns

				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				dos = new DataOutputStream(conn.getOutputStream());

				// send paramter #1
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"param1\""
						+ lineEnd + lineEnd);
				dos.writeBytes(Integer.parseInt(Utils.idUser) + lineEnd);

				// send image

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
						+ fileName + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					runOnUiThread(new Runnable() {
						public void run() {
							String msg = "File Upload Completed";
							messageText.setText(msg);
							Toast.makeText(ProfileActivity.this,
									"File Upload Complete.", Toast.LENGTH_SHORT)
									.show();
						}
					});
				}

				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (MalformedURLException ex) {

				dialog.dismiss();
				ex.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {
						messageText
								.setText("MalformedURLException Exception : check script url.");
						Toast.makeText(ProfileActivity.this,
								"MalformedURLException", Toast.LENGTH_SHORT)
								.show();
					}
				});

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {

				dialog.dismiss();
				e.printStackTrace();

				runOnUiThread(new Runnable() {
					public void run() {

					}
				});
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
			}
			dialog.dismiss();
			return serverResponseCode;

		} // End else block

	}

	public void profile() {
		if (mAuthTask != null) {
			return;
		}
		mAuthTask = new UserLoginTask();
		mAuthTask.execute((Void) null);
	}

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));

			try {

				JSONObject json = jParser.makeHttpRequest(url_getProfile,
						"POST", props);
				System.out.println(json.getInt(TAG_SUCCESS));
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					JSONArray JsonProfile = json.getJSONArray("userProfile");
					JSONArray JsonFoto = json.getJSONArray("photo");
					try {
						JSONObject jsonFoto = JsonFoto.getJSONObject(0);
						for (int i = 0; i < JsonProfile.length(); i++) {
							JSONObject jsonPost = JsonProfile.getJSONObject(i);
							profile.setNombre(jsonPost.getString("nombre"));
							profile.setNickname(jsonPost.getString("nickname"));
							profile.setEmail(jsonPost.getString("email"));
							profile.setDescripcion(jsonPost
									.getString("descripcion"));

							profile.setPathFoto(jsonFoto.getString("location"));

						}
					} catch (JSONException e) {
						for (int i = 0; i < JsonProfile.length(); i++) {
							JSONObject jsonPost = JsonProfile.getJSONObject(i);
							profile.setNombre(jsonPost.getString("nombre"));
							profile.setNickname(jsonPost.getString("nickname"));
							profile.setEmail(jsonPost.getString("email"));
							profile.setDescripcion(jsonPost
									.getString("descripcion"));
							profile.setPathFoto("photos/unknown.png");

						}
					}
					return true;
				} else {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				usuario.setText(profile.getNombre());
				email.setText(profile.getEmail());
				desc.setText(profile.getDescripcion());
				int loader = R.drawable.ic_launcher;
				String image_url = Utils.baseurl + "/" + profile.getPathFoto();
				ImageLoader imgLoader = new ImageLoader(getApplicationContext());
				imgLoader.DisplayImage(image_url, loader, imgPhoto);

			} else {
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			// showProgress(false);
		}
	}

	public void updateProfile() {
		if (uAuthTask != null) {
			return;
		}
		uAuthTask = new updateUserTask();
		uAuthTask.execute((Void) null);
	}

	public class updateUserTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("email", profile.getEmail()));
			props.add(new BasicNameValuePair("descripcion", profile
					.getDescripcion()));
			props.add(new BasicNameValuePair("idUser", Utils.idUser));

			try {
				JSONObject json = jParser.makeHttpRequest(url_updateProfile,
						"POST", props);
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

					return true;
				} else {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				Toast.makeText(getApplicationContext(), "Perfil actualizado!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			uAuthTask = null;
			// showProgress(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
