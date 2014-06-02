package org.devops.proyectofinal.entidades;

public class Post {
	private String nickname;
	private String postingTime;
	private String contenido;
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Post(String nickname, String postingTime, String contenido,
			String location) {
		this.nickname = nickname;
		this.postingTime = postingTime;
		this.contenido = contenido;
		this.location = location;

	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPostingTime() {
		return postingTime;
	}

	public void setPostingTime(String postingTime) {
		this.postingTime = postingTime;
	}

	public String getContenido() {
		return this.contenido;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
}
