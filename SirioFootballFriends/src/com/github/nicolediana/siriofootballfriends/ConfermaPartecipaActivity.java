package com.github.nicolediana.siriofootballfriends;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ConfermaPartecipaActivity extends Activity {
	
	private String nomeServlet="/ServletExample/ServletPartita";
		
	private String idpartita;
	private String idprofilo;
	private String tiporichiesta;
	private Integer nmancanti;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conferma_partecipa);
		
		//Prelevo dati dalla activity precedente
		Intent i = getIntent();
		idpartita= i.getStringExtra("idpartita");
		idprofilo= i.getStringExtra("idprofilo");
		//Toast.makeText(getApplicationContext(), idpartita, Toast.LENGTH_LONG).show();
		
		// Legge i campi della tabella 'partita'
		tiporichiesta = "leggi";
		try{
			JSONObject myjson = new JSONObject();		
			myjson.put("tiporichiesta", tiporichiesta);
			myjson.put("idpartita", idpartita);
			myjson.put("citta", "null");
			myjson.put("provincia", "null");
			
			// Richiesta HTTP
			StringEntity entity = new StringEntity(myjson.toString());		
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(MainActivity.urlServlet+nomeServlet);
			entity.setContentType("application/json;charset=UTF-8");
			httppostreq.setEntity(entity);
			HttpResponse response = httpclient.execute(httppostreq);
			
			// Recupero dati dalla ServletPartita
			if(response!=null){
    		 // Settaggio dei Textview
    		 String line = "";
		     InputStream inputstream = response.getEntity().getContent();
		     line = convertStreamToString(inputstream);
		     //Toast.makeText(getApplicationContext(), line, Toast.LENGTH_LONG).show();
		     JSONObject myjson1 = new JSONObject(line);
		     TextView nomedig=(TextView)findViewById(R.id.nomecampo);
		     nomedig.setText(myjson1.get("nomecampo").toString());
		     TextView indirizzodig=(TextView)findViewById(R.id.indirizzocampo);
		     indirizzodig.setText(myjson1.get("indirizzocampo").toString());
		     TextView cittadig=(TextView)findViewById(R.id.citta);
		     cittadig.setText(myjson1.get("citta").toString());
		     TextView provinciadig=(TextView)findViewById(R.id.provincia);
		     provinciadig.setText(myjson1.get("provincia").toString());
		     TextView oradig=(TextView)findViewById(R.id.ora);
		     oradig.setText(myjson1.get("ora").toString());
		     TextView datadig=(TextView)findViewById(R.id.data);
		     datadig.setText(myjson1.get("data").toString());
			 TextView costodig=(TextView)findViewById(R.id.costo);
			 costodig.setText(myjson1.get("costo").toString());
			 TextView amministratoredig=(TextView)findViewById(R.id.amministratore);
			 amministratoredig.setText(myjson1.get("amministratore").toString());
			 nmancanti = Integer.parseInt(myjson1.get("nmancanti").toString());	
			 //Toast.makeText(getApplicationContext(), giocatoriMancanti.toString(), Toast.LENGTH_LONG).show();
			}
		} catch(Exception e) {
    		//Toast.makeText(getApplicationContext(), ""+e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
        }	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conferma_partecipa, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			Intent intent=new Intent(this,MainActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_info) {
			Intent intent=new Intent(this,InfoActivity.class);
			Intent i=getIntent();
			idprofilo= i.getStringExtra("idprofilo");
			Bundle b=new Bundle();
			b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
			intent.putExtras(b); //intent x passaggio parametri
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String convertStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    try {
	        while ((line = rd.readLine()) != null) {
	            total.append(line);
	        }
	    } catch (Exception e) {
	    Toast.makeText(this, "Stream Exception", Toast.LENGTH_SHORT).show();
	    }
	return total.toString();
	}
	
	public void onClickSalva(View v) {
		Toast.makeText(getApplicationContext(), "Prenotazione effettuata! Vai a giocare ora", Toast.LENGTH_LONG).show();
		//Toast.makeText(getApplicationContext(), giocatoriMancanti.toString(), Toast.LENGTH_LONG).show();
		// Aggiorna il n° di giocatori mancanti
		try{
			//creazione oggetto Json
			//nmancanti = nmancanti - 1;
			tiporichiesta = "aggiorna";
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("tiporichiesta", tiporichiesta);
			jsonobj.put("nmancanti", nmancanti);
			jsonobj.put("idpartita", idpartita);
			jsonobj.put("citta", "");
			jsonobj.put("provincia", "");
			
			//creazione pacchetto post
			StringEntity entity = new StringEntity(jsonobj.toString());
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(MainActivity.urlServlet+nomeServlet);
			entity.setContentType("application/json;charset=UTF-8");
			httppostreq.setEntity(entity);
			HttpResponse response = httpclient.execute(httppostreq);
			
			// Risposta della servlet
			if(response!=null){
	    		 String line = "";
			     InputStream inputstream = response.getEntity().getContent();
			     line = convertStreamToString(inputstream);
			     JSONObject myjson1 = new JSONObject(line);
			     Toast.makeText(getApplicationContext(), "Giocatori mancanti: " + myjson1.get("nmancanti").toString(), Toast.LENGTH_LONG).show();
			}
		
			// Ritorno alla Homepage
			Intent intent=new Intent(this,HomeActivity.class);
			Bundle b=new Bundle();
		    b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
		    intent.putExtras(b); //intent x passaggio parametri
		    startActivity(intent);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public void onClickAnnulla(View v) {
		Intent intent=new Intent(this,HomeActivity.class);
		Bundle b=new Bundle();
	    b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
	    intent.putExtras(b); //intent x passaggio parametri
	    startActivity(intent);
	}
}