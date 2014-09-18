
package com.example.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

/**
 * classe responsável por executar as chamadas HTTP ao servidor web ou web service REST
 * class responsible for runs HTTP calls to the web server or REST web service
 * Use library http://hc.apache.org/
 * @author Roberto Silva
 * Alterado em: 18/09/2014
 */
public class ConexaoHttpClient {

	public static int HTTP_TIMEOUT = 10000;//10 segundos, default
	
	private static HttpClient httpClient;
	
	/**
	 * depois de usar a sessão HTTP desconecta
	 */
	public static void shutdownConnection(){
		if(httpClient != null){
		   httpClient.getConnectionManager().closeExpiredConnections();
		   httpClient.getConnectionManager().shutdown();
		   httpClient=null;
		}
	}
	
    /**
     * criando e configurando o objeto HTTPCliente
     * Socket time out, especifica o tempo da sessão
     * @return {@link HttpClient}
     */
	private static HttpClient getHttpClient() {
		if (httpClient == null) {
			System.out.println(">>>>>>>>>>>>>> Abrindo conexão HTTPClient <<<<<<<<<<<<<");
		    httpClient = new DefaultHttpClient();
			final HttpParams httpParamns = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParamns, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParamns, HTTP_TIMEOUT * 2);
			ConnManagerParams.setTimeout(httpParamns, HTTP_TIMEOUT);
		}
		return httpClient;
	}
	
	/**
	 * faz chamada ao servidor web no formato HTTP POST, com parametros que podem ser arquivos "multipart"
	 * @param url [endereço do servidor web ou web service REST]
	 * @param reqEntity [parametros]
	 * @return
	 * @throws Exception
	 */
	public static String executaHttpPostMultipart(String url,MultipartEntity reqEntity)throws Exception{
		BufferedReader bufferedReader = null;
		HttpEntity entity=null;
		
		try {
			HttpClient client = getHttpClient();
            HttpPost httppost = new HttpPost(url);

            httppost.setEntity(reqEntity);
            System.out.println("executing request " + httppost.getRequestLine());
            
            HttpResponse response = client.execute(httppost);
            
            System.out.println("HTTP: "+response.getStatusLine().getStatusCode());
            
            entity = response.getEntity();
            
            bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));//dá p/setar o buffer size
			StringBuffer stringBuffer = new StringBuffer("");
			
			String line; 
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				line=null;
			}
			
			bufferedReader.close();

            String retorno=stringBuffer.toString();
			
			return (retorno != null && retorno.equals("null")?"":retorno);
			
		} finally {
			//fecha a sessão e leitura
			
			if(entity != null)
				entity.consumeContent();
			
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * faz chamada ao servidor web no formato HTTP POST
	 * @param url [local do servidor web ou web service]
	 * @param parametrosPost 
	 * @return String
	 * @throws Exception
	 */
	public static String executaHttpPost(String url, ArrayList<NameValuePair> parametrosPost) throws Exception {
		BufferedReader bufferedReader = null;
		HttpEntity entity=null;
		try {
			
			HttpClient client = getHttpClient();
			HttpPost httpPost = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parametrosPost);
			httpPost.setEntity(formEntity);
			
			HttpResponse httpResponse = client.execute(httpPost);
			entity=httpResponse.getEntity();
			bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));//dá p/setar o buffer size
			StringBuffer stringBuffer = new StringBuffer("");
			
			String line; 
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				line=null;
			}
			
			bufferedReader.close();
			
			String retorno=stringBuffer.toString();
			
			return 
					(retorno != null && retorno.equals("null")?"":retorno);
			
		} finally {
			if(entity != null)
				entity.consumeContent();
			
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}			
	}
	
	/**
	 * faz chamada ao servidor web no formato HTTP GET, atenção os paramatros são expostos e há limitações
	 * @param url
	 * @return String
	 * @throws Exception
	 */
	public static String executaHttpGet(String url) throws Exception {
		BufferedReader bufferedReader = null;
		try {
			HttpClient client = getHttpClient();
			HttpGet httpGet = new HttpGet(url);			
			httpGet.setURI(new URI(url));
			HttpResponse httpResponse = client.execute(httpGet);
			bufferedReader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuffer stringBuffer = new StringBuffer("");
	
			String line = "";		
			String LS = System.getProperty("line.separator");
			
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line + LS);
			}
			bufferedReader.close();

			String retorno = stringBuffer.toString();
			
			return  
					(retorno != null && retorno.equals("null")?"":retorno);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}			
	}
	
	/**
	 * verifica se há conexão com url passada
	 * @param ctx
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean isConnectionServer(Context ctx,String url)throws Exception{
																//IP			porta "aplicação web, servidor"
		    								//exemplo > http://178.xxx.xxx.xxx:8020/		
			java.net.URL urlGoogle = new java.net.URL(url);
            java.net.URLConnection conn = urlGoogle.openConnection();

            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) conn;
            
            try{
            	httpConn.setConnectTimeout(2000);//2 segundos
            	httpConn.connect();

            	System.out.println(httpConn.getURL().toExternalForm());

            	int x = httpConn.getResponseCode();

            	if(x == 200){
            		System.out.println("Conectado");
            		return true;
            	}else{
            		return false;
            	}
            }finally{
            	httpConn.disconnect();
            }
	}
}
























