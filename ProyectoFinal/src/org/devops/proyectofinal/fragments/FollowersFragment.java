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
import org.devops.proyectofinal.users.ListaFollowers;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FollowersFragment extends ListFragment {

	private ListaFollowers lista;

	Vector<Usuario> usuarios;

	private LoadFollowersTask mLoadFollowersTask = null;
	private JsonParser jParser = new JsonParser();
	private static String url_load_followers = Utils.baseurl
			+ "/android/userFollower";
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
		if (mLoadFollowersTask != null) {
			return;
		}
		mLoadFollowersTask = new LoadFollowersTask();
		mLoadFollowersTask.execute((Void) null);
	}

	public class LoadFollowersTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			List<NameValuePair> props = new ArrayList<NameValuePair>();
			props.add(new BasicNameValuePair("idUser", Utils.idUser));

			try {
				JSONObject json = jParser.makeHttpRequest(url_load_followers,
						"POST", props);

				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					usuarios.removeAllElements();
					JSONArray JsonPosts = json.getJSONArray("usersFollower");
					for (int i = 0; i < JsonPosts.length(); i++) {
						JSONObject jsonPost = JsonPosts.getJSONObject(i);
						Usuario usuario = new Usuario(
								jsonPost.getString("nombre") + " "
										+ jsonPost.getString("apellido"),
								"descripcion usuario", "("
										+ jsonPost.getString("nickname") + ")",
								jsonPost.getString("idUser"));
						usuarios.add(usuario);
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
			mLoadFollowersTask = null;

			if (success) {
				lista = new ListaFollowers(getActivity(), usuarios);
				setListAdapter(lista);
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						"Ha ocurrido un error!.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			mLoadFollowersTask = null;
		}
	}
}
