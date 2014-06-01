package org.devops.proyectofinal.entidades;

public class Usuario {
	private String nombre;
	private String info;
	private String nickName;
	private String idUser;
 
	public Usuario(String nombre, String info, String followers,String idUser) {
		super();
		this.nombre = nombre;
		this.info = info;
		this.nickName = followers;
		this.idUser = idUser;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String followers) {
		this.nickName = followers;
	}

}
