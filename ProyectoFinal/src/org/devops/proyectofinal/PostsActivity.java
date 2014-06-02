package org.devops.proyectofinal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.devops.proyectofinal.entidades.Post;
import org.devops.proyectofinal.json.JsonParser;
import org.devops.pulltorefresh.PullToRefreshListView;
import org.devops.pulltorefresh.PullToRefreshListView.OnRefreshListener;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.Toast;

public class PostsActivity extends ListActivity {

	private ListaPost lista;

	private LoadPostsTask mLoadPostTask = null;
	private PostTask mPostTask = null;
	JsonParser jParser = new JsonParser();
	Vector<Post> posts;

	String postNuevo = "";

	private static String url_load_posts = Utils.baseurl + "/android/posts";

	private static String url_create_post = Utils.baseurl
			+ "/android/nuevoPost";
	private static String url_load_last_posts = Utils.baseurl
			+ "/android/postUltimos";
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posts);
		ActionBar actionBar = getActionBar();
		setTitle("Posts");
		actionBar.show();
		
		getOverflowMenu();
		posts = new Vector<Post>();
		attemptLoadPosts();

		// Set a listener to be invoked when the list should be refreshed.
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});
	}

	private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));
			props.add(new BasicNameValuePair("time", posts.get(0)
					.getPostingTime()));
			try {
				JSONObject json = jParser.makeHttpRequest(url_load_last_posts,
						"POST", props);
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					JSONArray JsonPosts = json.getJSONArray("posts");

					for (int i = 0; i < JsonPosts.length(); i++) {
						JSONObject jsonPost = JsonPosts.getJSONObject(i);
						Post post = new Post(jsonPost.getString("nickname"),
								jsonPost.getString("postingTime"),
								jsonPost.getString("Contenido"));
						posts.add(post);
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
			if (success) {
				attemptLoadPosts();
				lista.notifyDataSetChanged();
				((PullToRefreshListView) getListView()).onRefreshComplete();
			} else {
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void attemptLoadPosts() {
		if (mLoadPostTask != null) {
			return;
		}
		mLoadPostTask = new LoadPostsTask();
		mLoadPostTask.execute((Void) null);
	}

	public class LoadPostsTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));

			try {
				JSONObject json = jParser.makeHttpRequest(url_load_posts,
						"POST", props);

				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					posts.removeAllElements();
					JSONArray JsonPosts = json.getJSONArray("posts");

					for (int i = 0; i < JsonPosts.length(); i++) {
						JSONObject jsonPost = JsonPosts.getJSONObject(i);
						Post post = new Post(jsonPost.getString("nickname"),
								jsonPost.getString("postingTime"),
								jsonPost.getString("Contenido"));
						posts.add(post);
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
			mLoadPostTask = null;

			if (success) {
				lista = new ListaPost(PostsActivity.this, posts);
				setListAdapter(lista);
			} else {
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mLoadPostTask = null;
		}
	}

	public void post() {
		if (mLoadPostTask != null) {
			return;
		}
		mPostTask = new PostTask();
		mPostTask.execute((Void) null);
	}

	public class PostTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));
			props.add(new BasicNameValuePair("content", postNuevo));

			try {
				JSONObject json = jParser.makeHttpRequest(url_create_post,
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
			mPostTask = null;

			if (success) {

				Toast.makeText(getApplicationContext(), "Posteo existoso!",
						Toast.LENGTH_SHORT).show();
				attemptLoadPosts();
				lista.notifyDataSetChanged();

			} else {
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mPostTask = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.posts, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			lanzarUsuarios();
			return true;
		case R.id.action_compose:
			showDialogPost();
			return true;
		case R.id.action_profile:
			lanzarProfile();
			return true;
		case R.id.action_logout:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showDialogPost() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Nuevo Post");
		builder.setMessage("Escrbe tu post:");
		final EditText input = new EditText(this);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				postNuevo = input.getText().toString();
				post();
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		builder.show();
	}

	private void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lanzarUsuarios() {
		Intent i = new Intent(getApplicationContext(), UsuariosActivity.class);
		startActivity(i);
	}

	public void lanzarProfile() {
		Intent i = new Intent(getApplicationContext(), Camara.class);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		attemptLoadPosts();
	}

}
