package org.devops.proyectofinal.users;

import java.util.Vector;

import org.devops.proyectofinal.R;
import org.devops.proyectofinal.entidades.Usuario;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListaFollowing extends BaseAdapter {

	private final Activity actividad;
	/*
	 * Lista que contiene quien realizo el post, la fecha y el contenido del
	 * post
	 */
	private final Vector<Usuario> lista;

	public ListaFollowing(Activity actividad, Vector<Usuario> lista) {
		super();
		this.actividad = actividad;
		this.lista = lista;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = actividad.getLayoutInflater();
		View view = inflater.inflate(R.layout.lista_following, null, true);

		TextView textViewPerfil = (TextView) view.findViewById(R.id.txtPerfil);

		/* Lista que contiene todos los post en el twitter */
		textViewPerfil.setText(lista.elementAt(position).getNombre());

		/* Foto del contacto que realizo el post */
		//ImageView imageView = (ImageView) view.findViewById(R.id.imgPersona);

		/* Nombre del contacto que ha realizado el post */
		TextView textViewInfo = (TextView) view.findViewById(R.id.txtInfo);
		textViewInfo.setText(lista.elementAt(position).getInfo());

		/* Fecha en la cual el contacto que ha realizado el post */
		TextView textViewSeguidores = (TextView) view
				.findViewById(R.id.txtSeguidores);
		textViewSeguidores.setText(lista.elementAt(position).getNickName());

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

}
