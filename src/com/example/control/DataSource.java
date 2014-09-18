package com.example.control;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * classe responsavel por conexao com banco de dados SQLite "Fábrica"
 * class responsible for connection with SQLite database "Factory"
 * @author Roberto Silva
 * Alterado em: 18/09/2014
 */
public class DataSource{

	public static final String TAG = "DataSource";

	//constantes dos nomes de tabelas
	public static final String TAB_USUARIOS = "USUARIOS";
	
	
	public static final String DB_NAME = "BANCO_DADOS.db";//escolha o nome do banco de dados .db extensão do sqlite
	public static final int DATABASE_VERSION = 1;//versão do banco de dados

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;


	public DataSource(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Abre conexao com Banco de dados
	 * */
	public void open() throws SQLException {
		//instancio a classe interna e passo o contexto da app
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
	}

	/**
	 * Fecha a conexao com Banco de dados
	 * */
	public void close() {

		if(mDbHelper != null)
			mDbHelper.close();

		if(mDb != null){
			mDb.close();
		}
	}

	/**
	 * Pega a conexao atual c/banco
	 * */
	public SQLiteDatabase getConnection(){
		return mDb;
	}
	

	/**
	 * Funciona como eventos do banco de dados "sqlite listener" 
	 * */
	private class DatabaseHelper extends SQLiteOpenHelper {

		@Override
		public void onOpen(SQLiteDatabase db){
			super.onOpen(db);
			if (!db.isReadOnly()){
				//ativando restrições 
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}

		/**
		 * O construtor necessita do contexto da aplicação
		 */
		public DatabaseHelper(Context context) {
			/* O primeiro argumento é o contexto da aplicacao
			 * O segundo argumento é o nome do banco de dados
			 * O terceiro é um ponTeiro para manipulação de dados, 
			 *   não precisaremos dele.
			 * O quarto é a versão do banco de dados
			 */ 
			super(context, DB_NAME, null, DATABASE_VERSION);
			System.out.println("Construtor :"+this.getClass().getSimpleName());
		}

		/**
		 * cria todas tabelas
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {

			/*
			 REAL = DOUBLE
			 TEXT = STRING
			 INTEGER = IDEM
			 NÃO HÁ TIPO DATA NO SQLITE MAS ELE RECONHECE NA CONSULTA SE GRAVAR NO FORMTATO YYYY-MM-DD
			 consulte sobre mais detalhes no site oficial do sqlite3 sobre como tratar data
			 * */
			
			System.out.print("Criando  tabelas: ");
			db.execSQL(
					"CREATE TABLE IF NOT EXISTS "+TAB_USUARIOS+"(" +
							"ID       INTEGER PRIMARY KEY,"+                  
							"USUARIO  TEXT, "+
							"SENHA    TEXT," +
							"NOME     TEXT," +
							"GRUPO    INTEGER," +
							"EMAIL    TEXT,"+
							"DATA_CAD TEXT);"
					); 

			Log.i(TAG,">>>> CRIANDO TABELA USUARIOS : "+TAB_USUARIOS+" <<<<<<<<<<<<<");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, ">>>>>>> Atualizando o banco de dados da versão " + oldVersion+ " para " + newVersion);
			
			if(newVersion > oldVersion){
				db.beginTransaction();
				boolean success=true;
				
				//CONTROLE DE VERSÃO DO BANCO DE DADOS
				for (int i = oldVersion; i < newVersion; i++){
					int nextVersion=i+1;
					
					switch (nextVersion) {
					case 1:
						success=updateToVersion1(db);
						break;
					case 2:
						success=updateToVersion2(db);
						break;
					case 3:
						success=updateToVersion3(db);
						break;
					}
					
					if(!success){
						Log.i(TAG, "não atualizou o banco de dados");
						break;
					}
				}
				
				if (success) {
					Log.i(TAG, "atualizou o banco de dados");
					db.setTransactionSuccessful();
				}
				
				db.endTransaction();
			}else{
				//drop table em tudo,se retrocedeu a versão do banco de dados
				clearDataBase(db);
			}
			//cria as tabelas caso não existão
			onCreate(db);
		}
		
	}
	
	/*METODO PERMITEM CONTROLE DE VERSÃO PARA CADA VERSÃO EXECUTA AS ALTERAÇÕES NO BANCO DE DADOS*/
	
	private boolean updateToVersion1(SQLiteDatabase db){
		try {
			//EXEMPLO DE DML
			//db.execSQL("ALTER TABLE "+TAB_USUARIOS+" ADD COLUMN FLAG_CANCEL TEXT;");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean updateToVersion2(SQLiteDatabase db){
		try {
			//db.execSQL("DIGITE SUA DDL OU DML NOVA");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean updateToVersion3(SQLiteDatabase db){
		try {
			//db.execSQL("DIGITE SUA DDL OU DML NOVA");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void clearDataBase(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS " + TAB_USUARIOS);
		/*
		 crie seus drop table's
		 * */
	}
}