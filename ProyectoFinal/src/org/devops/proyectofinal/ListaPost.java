package org.devops.proyectofinal;

import java.util.Vector;

import org.devops.proyectofinal.entidades.Post;
import org.devops.proyectofinal.imageLoader.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListaPost extends BaseAdapter {

	private final Activity actividad;
	private final Context mcontext;

	/*
	 * Lista que contiene quien realizo el post, la fecha y el contenido del
	 * post
	 */
	private final Vector<Post> lista;

	public ListaPost(Activity actividad, Vector<Post> lista, Context c) {
		super();
		this.actividad = actividad;
		this.lista = lista;
		this.mcontext = c;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		String image_url = Utils.baseurl + "/"
				+ lista.elementAt(position).getLocation();
		ImageLoader imgLoader = new ImageLoader(
				mcontext.getApplicationContext());
		int loader = R.drawable.ic_launcher;
		LayoutInflater inflater = actividad.getLayoutInflater();
		View view = inflater.inflate(R.layout.lista_post, null, true);
		TextView textViewContenido = (TextView) view
				.findViewById(R.id.txtContenido);

		/* Lista que contiene todos los post en el twitter */
		textViewContenido.setText(lista.elementAt(position).getContenido());

		/* Foto del contacto que realizo el post */
		ImageView imageView = (ImageView) view.findViewById(R.id.imgContacto);
		imgLoader.DisplayImage(image_url, loader, imageView);

		/* Nombre del contacto que ha realizado el post */
		TextView textViewContacto = (TextView) view
				.findViewById(R.id.txtUserName);
		textViewContacto.setText(lista.elementAt(position).getNickname());

		/* Fecha en la cual el contacto que ha realizado el post */
		TextView textViewFecha = (TextView) view.findViewById(R.id.txtFecha);
		textViewFecha.setText(lista.elementAt(position).getPostingTime());
		return view;
	}

	public int getCount() {
		return lista.size();
	}

	public Object getItem(int arg0) {
		return lista.elementAt(arg0);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
