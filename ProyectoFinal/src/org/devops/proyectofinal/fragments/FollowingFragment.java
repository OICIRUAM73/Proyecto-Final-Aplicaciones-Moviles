package org.devops.proyectofinal.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.devops.proyectofinal.R;
import org.devops.proyectofinal.Utils;
import org.devops.proyectofinal.entidades.Usuario;
import org.devops.proyectofinal.json.JsonParser;
import org.devops.proyectofinal.users.ListaFollowing;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FollowingFragment extends ListFragment {
	private ListaFollowing lista;

	Vector<Usuario> usuarios;

	private LoadPostsTask mLoadToFollowTask = null;
	private LoadFollowingTask mFollowingTask = null;
	private String idUsuarioPasar = "";

	private JsonParser jParser = new JsonParser();
	private static String url_load_follow = Utils.baseurl
			+ "/android/userFollowing";
	private static String url_unFollow = Utils.baseurl + "/android/stopFollow";
	private static final String TAG_SUCCESS = "success";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		usuarios = new Vector<Usuario>();
		attemptLoadToFollow();

		View rootView = inflater.inflate(R.layout.fragment_follow, container,
				false);

		return rootView;
	}

	public void attemptLoadToFollow() {
		if (mLoadToFollowTask != null) {
			return;
		}
		mLoadToFollowTask = new LoadPostsTask();
		mLoadToFollowTask.execute((Void) null);
	}

	public class LoadPostsTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));

			JSONObject json = jParser.makeHttpRequest(url_load_follow, "POST",
					props);
			Log.w("Response follow: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					usuarios.removeAllElements();
					JSONArray JsonPosts = json.getJSONArray("usersFollowing");
					for (int i = 0; i < JsonPosts.length(); i++) {
						JSONObject jsonPost = JsonPosts.getJSONObject(i);
						Usuario usuario = new Usuario(
								jsonPost.getString("nombre") + " "
										+ jsonPost.getString("apellido"),
								"descripcion usuario",
								jsonPost.getString("nickname"),
								jsonPost.getString("idUser"));
						usuarios.add(usuario);
					}
					System.out.println("USUARIOS: \n" + usuarios.toString());
					return true;
				} else {
					Log.w("Ingreso", "se produjo un error");
					Log.w("Ingreso", Integer.toString(success));
					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mLoadToFollowTask = null;

			if (success) {
				System.out.println("success");
				lista = new ListaFollowing(getActivity(), usuarios);
				setListAdapter(lista);
			} else {
				System.out.println("incorrect");
			}
		}

		@Override
		protected void onCancelled() {
			mLoadToFollowTask = null;
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		idUsuarioPasar = usuarios.get(position).getIdUser();
		showDialogPost();

		super.onListItemClick(l, v, position, id);
	}

	public void showDialogPost() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Seguir");
		builder.setMessage("Desea dejar de seguir a este usuario?");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				attemptFollowUser();
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

	public void attemptFollowUser() {
		if (mFollowingTask != null) {
			return;
		}
		mFollowingTask = new LoadFollowingTask();
		mFollowingTask.execute((Void) null);
	}

	public class LoadFollowingTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));
			props.add(new BasicNameValuePair("idUserAmigo", idUsuarioPasar));

			try {
				JSONObject json = jParser.makeHttpRequest(url_unFollow, "POST",
						props);

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
			mLoadToFollowTask = null;

			if (success) {
				System.out.println("success");
				attemptLoadToFollow();
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mLoadToFollowTask = null;
		}
	}
}