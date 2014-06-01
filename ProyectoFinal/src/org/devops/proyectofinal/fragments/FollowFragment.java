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
import org.devops.proyectofinal.users.ListaFollow;
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

public class FollowFragment extends ListFragment {

	private ListaFollow lista;

	Vector<Usuario> usuarios;

	private String idUsuarioPasar = "";
	private String nickName = "";

	private LoadPostsTask mLoadToFollowTask = null;
	private LoadFollowTask mFollowTask = null;
	private JsonParser jParser = new JsonParser();
	private static String url_load_follow = "http://192.168.0.105:80/micronott/micronott/android/allUser";
	private static String url_follow = "http://192.168.0.105:80/micronott/micronott/android/seguir";
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
					JSONArray JsonPosts = json.getJSONArray("allUser");
					System.out.println("json: " + JsonPosts);
					for (int i = 0; i < JsonPosts.length(); i++) {
						JSONObject jsonPost = JsonPosts.getJSONObject(i);
						Usuario usuario = new Usuario(
								jsonPost.getString("nombre") + " "
										+ jsonPost.getString("apellido"),
								"descripcion usuario",
										jsonPost.getString("nickname"),
								jsonPost.getString("idUser"));
						System.out.println("Usuario: " + usuario.getNombre() + " - " + usuario.getIdUser());
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
				lista = new ListaFollow(getActivity(), usuarios);
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
		nickName = usuarios.get(position).getNickName();
		idUsuarioPasar = usuarios.get(position).getIdUser();
		showDialogPost();

		super.onListItemClick(l, v, position, id);
	}

	public void showDialogPost() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Seguir");
		builder.setMessage("¿Desea seguir a este usuario?");

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				System.out.println("entra a intentar followear!");
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
		if (mFollowTask != null) {
			return;
		}
		mFollowTask = new LoadFollowTask();
		mFollowTask.execute((Void) null);
	}

	public class LoadFollowTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));
			props.add(new BasicNameValuePair("idUserAmigo", idUsuarioPasar));
			props.add(new BasicNameValuePair("nicknameAmigo", nickName));
			JSONObject json = jParser
					.makeHttpRequest(url_follow, "POST", props);
			Log.w("Response follow: ", json.toString());

			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {

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
				attemptLoadToFollow();
			} else {
				System.out.println("incorrect");
			}
		}

		@Override
		protected void onCancelled() {
			mLoadToFollowTask = null;
		}
	}
}