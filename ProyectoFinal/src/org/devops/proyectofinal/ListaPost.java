package org.devops.proyectofinal;

import java.util.Vector;

import org.devops.proyectofinal.entidades.Post;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListaPost extends BaseAdapter {

	private final Activity actividad;
	/*
	 * Lista que contiene quien realizo el post, la fecha y el contenido del
	 * post
	 */
	private final Vector<Post> lista;

	public ListaPost(Activity actividad, Vector<Post> lista) {
		super();
		this.actividad = actividad;
		this.lista = lista;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = actividad.getLayoutInflater();
		View view = inflater.inflate(R.layout.lista_post, null, true);
		TextView textViewContenido = (TextView) view
				.findViewById(R.id.txtContenido);
		System.out.println(view.getClass().getName());
		/* Lista que contiene todos los post en el twitter */
		textViewContenido.setText(lista.elementAt(position).getContenido());

		/* Foto del contacto que realizo el post */
		//ImageView imageView = (ImageView) view.findViewById(R.id.imgContacto);

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
	public void notifyDataSetChanged() // Create this function in your adapter
										// class
	{
		System.out.println("Entra data set changed");
		super.notifyDataSetChanged();
	}

}
