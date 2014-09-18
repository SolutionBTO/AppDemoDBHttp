package com.example.view;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import com.example.control.ConexaoHttpClient;
import com.example.control.DataSource;
import com.example.model.Usuario;
import com.example.model.UsuarioDao;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * classe MAIN principal
 * Exemplo de persistencia com SQLITE e chamadas HTTP POST, GET para web service ou servidor web
 * 
 * class main from APP
 * Sample of persistence of SQLITE and too call HTTP POST, GET for the web service or Server Web
 * @author Roberto Silva
 * criado em: 18/09/2014
 */
public class MainActivity extends Activity {
	
	/**
	 * concatena para acessar serviços
	 * @param local
	 * @return
	 */
	public String getWebServiceGoogleAPIS(String local){
		return "http://maps.googleapis.com/".concat(local);
	}
	
	private EditText editText;
	private Button button;
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//criando objetos da view
		editText=(EditText)findViewById(R.id.edit_text);
		button=(Button)findViewById(R.id.btn_action);
		spinner=(Spinner)findViewById(R.id.spinner);
		
		
		/*
		 * setando configuração do spinner "modelo"
		 * no SPINNER "combobox" irá escolher a operação
		 */
		ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, 
						android.R.layout.simple_spinner_item, new String[]{
				"INSERT SQLITE",
				"TESTE CONEXÂO > GOOGLE.COM",
				"HTTP GET > API MAPs GOOGLE ",
				"HTTP POST 1 > API MAPs GOOGLE ",
				"HTTP POST 2 > API MAPs GOOGLE "});
		adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
		spinner.setAdapter(adapter);
		
		/**
		 * evento onClick do botão
		 * evento executa a ação selecionada no SPINNER
		 */
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
				//conforme a combobox SPinner executa uma ação	
				switch (spinner.getSelectedItemPosition()) {
				case 0:
					exemploInsertSqlite();
					break;
				case 1:
					exemploTesteConexaoHTTP();
					break;
				case 2:
					exemploChamadaHttpGet();
					break;
				case 3:
					exemploChamadaHttpPostNormal();
					break;
				case 4:
					exemploChamadaHttpPostMultipart();
					break;
				}
				
				if(spinner.getSelectedItemPosition() > 0){
					Toast.makeText(MainActivity.this, "testando chamadas HTTP ", Toast.LENGTH_LONG).show();
				}
				
				}catch(Exception e){
					Toast.makeText(MainActivity.this, "Ocorreu um erro, causa: "+e.getMessage(),  Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * insert no sqlite
	 */
	private void exemploInsertSqlite() {
		//Exemplo de persistencia com banco de dados
		
				//crio acesso do banco de dados e passo no construtor da classe dao
				DataSource dataSource=new DataSource(this);
				UsuarioDao dao=new UsuarioDao(dataSource);
				long result=-1L;
				
				List<Usuario> usuarios=null;
				//objeto usuario
				Usuario usuario=new Usuario();
				try {
					//abrindo o banco de dados
					dataSource.open();
					usuario.setNome("Jose Reginaldo");
					usuario.setUsuario("jose");
					usuario.setSenha("123");
					usuario.setGrupo(1);
					usuario.setDataCad(new Date(new java.util.Date().getTime()));
					result=dao.inserir(usuario);//INSERT
					
					if(result > 0)
						usuarios=dao.selectAll();//SELECT
				
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					dataSource.close();
				}
				
				if(usuarios != null)
					editText.setText("Registros:\n"+usuarios);//exibe graças ao toString no objeto USUARIOS
	}

	/**
	 * teste de conexão "ping" no endereço passado
	 * @throws Exception
	 */
	private void exemploTesteConexaoHTTP() throws Exception{
		Toast.makeText(this, "testando conexão com www.google.com ", Toast.LENGTH_LONG).show();
		editText.setText("Conexão está:"+
				ConexaoHttpClient.isConnectionServer(this, "http://www.google.com/"));
	}

	/**
	 * chamda HTTP GET
	 * @throws Exception
	 */
	private void exemploChamadaHttpGet() throws Exception{
		try{
			String jsonRetorno=
					ConexaoHttpClient.executaHttpGet(
							getWebServiceGoogleAPIS(
									"maps/api/geocode/json?address=UNINOVE%20Vila%20Maria-SP&sensor=false"));
			editText.setText(jsonRetorno);
		}finally{
			ConexaoHttpClient.shutdownConnection();
		}
	}
	
	/* 
	 estes 2 ultimos metodos não funcionam para esta chamada HTTP POST, 
	 só para HTTP GET é somente um exemplo
	 
	 These last two methods do not work for this call HTTP POST, 
	 HTTP GET is only for just one example
	 */
	
	/**
	 * chamada POST 1
	 * @throws Exception
	 */
	private void exemploChamadaHttpPostNormal() throws Exception{
		try{
			ArrayList<NameValuePair> parametrosPost = new ArrayList<NameValuePair>();
			parametrosPost.add(new BasicNameValuePair("address","address=UNINOVE%20Vila%20Maria-SP"));
			parametrosPost.add(new BasicNameValuePair("sensor","false"));
			editText.setText(
					ConexaoHttpClient.executaHttpPost(
					getWebServiceGoogleAPIS("maps/api/geocode/json"),
					parametrosPost));
		}finally{
			ConexaoHttpClient.shutdownConnection();
		}
	}
	
	/**
	 * chamada POST 2
	 * @throws Exception
	 */
	private void exemploChamadaHttpPostMultipart() throws Exception{
		try{
			MultipartEntity reqEntity=new MultipartEntity();
			reqEntity.addPart("address",new StringBody("address=UNINOVE%20Vila%20Maria-SP"));
			reqEntity.addPart("sensor",new StringBody("false"));
			/*
			//caso houvesse arquivo "java.io.File"
			if(imagem_foto.isFile()){
				reqEntity.addPart("foto_cheque",new FileBody(imagem_foto));
			}	
			*/	
			editText.setText(
					ConexaoHttpClient.executaHttpPostMultipart(
							getWebServiceGoogleAPIS("maps/api/geocode/json"), 
							reqEntity));
		}finally{
			ConexaoHttpClient.shutdownConnection();
		}
	}
}
