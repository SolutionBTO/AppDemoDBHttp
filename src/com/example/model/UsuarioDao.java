package com.example.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.example.control.DataSource;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Classe DAO - responsavel pela persistencia de dados do Bean Usuario
 * Class DAO from Object Usuarios
 * @author Roberto Silva
 * Alterado em: 18/09/2014
 */
public class UsuarioDao {

	public DataSource dataSource;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat fmtYYYYMMDD=new SimpleDateFormat("yyyy-MM-dd");
	
	
	public UsuarioDao(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	/**
	 * seleciona usuario
	 * @param codigoMsg
	 * @return
	 */
	public Usuario selecionarMsg(String codigoMsg) {
		Usuario usuario=new Usuario();
		Cursor cursor=null;

		try{		
			cursor=dataSource.getConnection().query(
					DataSource.TAB_USUARIOS, new String[]{"ID", "USUARIO","SENHA","NOME"}, 
					" ID = "+codigoMsg, null, null, null,null);

			if(cursor.moveToFirst()){
				
				usuario.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				usuario.setUsuario(cursor.getString(cursor.getColumnIndex("USUARIO")));
				usuario.setSenha(cursor.getString(cursor.getColumnIndex("SENHA")));
				usuario.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
		   }
		}finally{
			cursor.close();
		}

		return usuario;
	}

	/**
	 * inseri um novo {@link Usuario}
	 * @param usuario
	 */
	public long inserir(Usuario usuario){
		SQLiteDatabase mDb=dataSource.getConnection();
		
		ContentValues values = new ContentValues();
		
		if(usuario.getId() != 0)
		values.put("ID", usuario.getId());
		else
			values.putNull("ID");
		
		values.put("USUARIO",usuario.getUsuario());
		values.put("SENHA", usuario.getSenha());
		values.put("NOME",usuario.getNome());
		values.put("GRUPO", usuario.getGrupo());
		values.put("EMAIL", usuario.getEmail());
		values.put("DATA_CAD", fmtYYYYMMDD.format(usuario.getDataCad()));
		
		long result=-1l;
		
		mDb.beginTransaction();
		try{
			result=mDb.insert(DataSource.TAB_USUARIOS, null, values);
			mDb.setTransactionSuccessful();
		}finally{
			mDb.endTransaction();
		}
		
		return result;
	}

	/**
	 * autentica o usuario
	 * @param usuario
	 * @param senha
	 * @return {@link String} [nome_completo]
	 */
	public String logarMsg(String usuario,String senha){
		String name=null;
		Cursor cursor = null;

		try{
			cursor = 
					dataSource.getConnection().query(
							DataSource.TAB_USUARIOS, new String[]{"NOME"}, 
							"USUARIO = '"+usuario+"' AND SENHA = '"+senha+"'", 
							null, null, null, null );
			//pega 1° ocorrencia;
			cursor.moveToFirst();
			if(cursor.getCount() > 0) {
				name=cursor.getString(0);
			}
		}finally{
			cursor.close();
		}

		return name;
	}	
	
	/**
	 * apaga registro do usuario, evita login
	 * @param id
	 * @return
	 */
	public int apagar(String id){
		SQLiteDatabase mDb=dataSource.getConnection();
		mDb.beginTransaction();
		int result=-1;
		try{
			result=mDb.delete(DataSource.TAB_USUARIOS, " ID=? ", new String[]{id});
			mDb.setTransactionSuccessful();
		}finally{
			mDb.endTransaction();
		}		
		return result;
	}
	
	
	public List<Usuario> selectAll() {
		List<Usuario> usuarios=new ArrayList<Usuario>();
		Usuario usuario;
		Cursor cursor=null;

		try{											//dml e condição
			cursor=dataSource.getConnection().rawQuery("SELECT * FROM "+DataSource.TAB_USUARIOS,null);
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()){
				usuario=new Usuario();
				usuario.setId(cursor.getInt(cursor.getColumnIndex("ID")));
				usuario.setUsuario(cursor.getString(cursor.getColumnIndex("USUARIO")));
				usuario.setSenha(cursor.getString(cursor.getColumnIndex("SENHA")));
				usuario.setNome(cursor.getString(cursor.getColumnIndex("NOME")));
				usuario.setGrupo(cursor.getInt(cursor.getColumnIndex("GRUPO")));
				usuario.setEmail(cursor.getString(cursor.getColumnIndex("EMAIL")));

				try{
					usuario.setDataCad(new Date(fmtYYYYMMDD.parse(cursor.getString(cursor.getColumnIndex("DATA_CAD"))).getTime()));
				}catch(Exception e){}
				
				usuarios.add(usuario);
				
				cursor.moveToNext();
		   }
		}finally{
			cursor.close();
		}

		return usuarios;
	}
}
