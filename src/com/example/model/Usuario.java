package com.example.model;

import java.io.Serializable;

/**
 * Objeto Bean representa a entidade USUARIOS
 * Object Bean represent User
 * @author Roberto Silva
 * Alterado em: 18/09/2014
 */
public class Usuario implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id; 
	private String usuario;
	private String senha;
	private String nome;
	private int grupo;
	private String email;
	private java.sql.Date dataCad;
	
	public Usuario(){
    	super();
    }
	
	public Usuario(int id,String usuario,String senha){
    	this.id=id;
    	this.usuario=usuario;
    	this.senha=senha;
    }
	
	public Usuario(String usuario,String senha){
    	this.usuario = usuario;
    	this.senha = senha;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}
	
	public void setUsuario(String usuario)throws IllegalArgumentException {
		if(usuario == null)
			throw new IllegalArgumentException("Usuario é um campo obrigatório.");
			
			this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha)throws IllegalArgumentException {
		if(senha == null)
			throw new IllegalArgumentException("Senha é um campo obrigatório.");
		
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the dataCad
	 */
	public java.sql.Date getDataCad() {
		return dataCad;
	}

	/**
	 * @param dataCad the dataCad to set
	 */
	public void setDataCad(java.sql.Date dataCad) {
		this.dataCad = dataCad;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ID=" + id + ",NOME=" + nome + ",DATA_CAD=" + dataCad;
	}
	
	
}
