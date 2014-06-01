package org.devops.proyectofinal.entidades;

public class Post {
	private String nickname;
	private String postingTime;
	private String contenido;

	public Post(String nickname, String postingTime, String contenido) {
		this.nickname = nickname;
		this.postingTime = postingTime;
		this.contenido= contenido;
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
