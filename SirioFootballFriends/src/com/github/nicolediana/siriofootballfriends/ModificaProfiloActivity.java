package com.github.nicolediana.siriofootballfriends;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ModificaProfiloActivity extends Activity {
	private String nomeServlet="/ServletExample/ServletProfilo";
		
	String idprofilo;
	private String nome;
	private String cognome;
	private String nickname;
	private String citta;
	private Integer eta;
	private String sesso;
	private String ruolo;
	private String livello;
	private String password;
	String tiporichiesta;
	Spinner spinner_ruolo;
	Spinner spinner_livello;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifica_profilo);
		
		Intent i=getIntent();
		idprofilo= i.getStringExtra("idprofilo");
		
		caricaProfilo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modifica_profilo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent i=getIntent();
		idprofilo= i.getStringExtra("idprofilo");
		
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			Intent intent=new Intent(this,MainActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_info) {
			Intent intent=new Intent(this,InfoActivity.class);
			Bundle b=new Bundle();
			b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
			intent.putExtras(b); //intent x passaggio parametri
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClickSalva(View v) throws UnsupportedEncodingException {
		TextView nomedig=(TextView)findViewById(R.id.nome);
		TextView cognomedig=(TextView)findViewById(R.id.cognome);
		TextView nicknamedig=(TextView)findViewById(R.id.nickname);
		TextView cittadig=(TextView)findViewById(R.id.citta);
		TextView etadig=(TextView)findViewById(R.id.eta);
		TextView pwDigitato=(TextView)findViewById(R.id.psw);
		
		nome= nomedig.getText().toString();
		cognome= cognomedig.getText().toString();
		nickname= nicknamedig.getText().toString();
		citta= cittadig.getText().toString();
		password= (String)pwDigitato.getText().toString();
		
		String etaStr= etadig.getText().toString();
		if(etaStr.equals(null)||etaStr=="")
			eta=0;
		else
			eta=Integer.parseInt(etaStr);

		//if(eta<5||eta>80)
		//	Toast.makeText(getApplicationContext(), "Inserire una eta' valida", Toast.LENGTH_LONG).show();
		
		//città non deve essere nullo
		if(citta.equals("")||citta.equals(null))
			Toast.makeText(getApplicationContext(), "Citta' non valida", Toast.LENGTH_LONG).show();
		else{
			JSONObject jsonobj= new JSONObject();
			try{
				String tiporichiesta="aggiorna";
				//creazione della Json
				jsonobj.put("tiporichiesta", tiporichiesta );
				jsonobj.put("nome", nome );
				jsonobj.put("cognome", cognome );
				jsonobj.put("nickname", nickname );
				jsonobj.put("citta", citta );
				if(!etaStr.equals(null)||etaStr!="")
					jsonobj.put("eta", etaStr );
				jsonobj.put("sesso", sesso );
				jsonobj.put("ruolo", ruolo );
				jsonobj.put("livello", livello );
				jsonobj.put("password", password );
				jsonobj.put("idprofilo", idprofilo );
				
				//creazione pacchetto post
				StringEntity entity = new StringEntity(jsonobj.toString());
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httppostreq = new HttpPost(MainActivity.urlServlet+nomeServlet);
				entity.setContentType("application/json;charset=UTF-8");
				httppostreq.setEntity(entity);
				HttpResponse httpresponse = httpclient.execute(httppostreq);
				
				/*
				 //recupero della risposta
				String line = "";
				InputStream inputstream = httpresponse.getEntity().getContent();
				line = convertStreamToString(inputstream);
				JSONObject myjson = new JSONObject(line);	        	     
				String idprofilo=myjson.get("idprofilo").toString();
				*/
				Intent intent=new Intent(this,HomeActivity.class);
				Bundle b=new Bundle();
			    b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
			    intent.putExtras(b); //intent x passaggio parametri
			    startActivity(intent);
			    }
				catch (JSONException ex) {
					ex.printStackTrace();
				}
				catch(UnsupportedEncodingException e){e.printStackTrace();}
				catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
		}
	}
	
	public void onClickAnnulla(View v){
		Intent intent=new Intent(this,HomeActivity.class);
		Bundle b=new Bundle();
		b.putString("idprofilo", idprofilo); //passa chiave valore a activity_home
		intent.putExtras(b); //intent x passaggio parametri
		startActivity(intent);
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
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean sessochecked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_sessoM:
	            if (sessochecked)
	                sesso="M";
	            break;
	        case R.id.radio_sessoF:
	            if (sessochecked)
	            	sesso="F";
	            break;
	    }
	}
	
		public void caricaProfilo() {
		//Gestione degli spinner ruolo e livello
		spinner_ruolo = (Spinner) findViewById(R.id.spinner_ruolo);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.ruolo, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_ruolo.setAdapter(adapter);
		
		spinner_livello = (Spinner) findViewById(R.id.spinner_livello);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.livello, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_livello.setAdapter(adapter2);
		
		spinner_ruolo.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapter, View view, int pos,long id) 
			{
				ruolo= adapter.getItemAtPosition(pos).toString();	
			}
			public void onNothingSelected(AdapterView<?> arg0) { }});
		
		spinner_livello.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapter2, View view, int pos,long id) 
			{
				livello= adapter2.getItemAtPosition(pos).toString();	
			}
			public void onNothingSelected(AdapterView<?> arg0) { }});

		
		//LEGGI PROFILO
		tiporichiesta="leggi";
		JSONObject myjson= new JSONObject();
		try{
			//creazione della Json
			myjson.put("tiporichiesta", tiporichiesta );
			myjson.put("idprofilo", idprofilo );
			//creazione pacchetto post
			StringEntity entity = new StringEntity(myjson.toString());
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppostreq = new HttpPost(MainActivity.urlServlet+nomeServlet);
			entity.setContentType("application/json;charset=UTF-8");
			httppostreq.setEntity(entity);
			HttpResponse httpresponse = httpclient.execute(httppostreq);
			//risposta
			String line = "";
			InputStream inputstream = httpresponse.getEntity().getContent();
			line = convertStreamToString(inputstream);
			JSONObject myjson2 = new JSONObject(line);	        	     
			
			TextView nomedig=(TextView)findViewById(R.id.nome);
			TextView cognomedig=(TextView)findViewById(R.id.cognome);
			TextView nicknamedig=(TextView)findViewById(R.id.nickname);
			TextView cittadig=(TextView)findViewById(R.id.citta);
			TextView etadig=(TextView)findViewById(R.id.eta);
			
			sesso=myjson2.get("sesso").toString();
			ruolo=myjson2.get("ruolo").toString();
			livello=myjson2.get("livello").toString();
			
			nomedig.setText(myjson2.get("nome").toString());
			cognomedig.setText(myjson2.get("cognome").toString());
			nicknamedig.setText(myjson2.get("nickname").toString());
			cittadig.setText(myjson2.get("citta").toString());
			etadig.setText(myjson2.get("eta").toString());
			
			if(sesso.equals("M")){
				RadioButton radio1 = (RadioButton)findViewById(R.id.radio_sessoM);
				radio1.setChecked(true);
			}
			if(sesso.equals("F")){
				RadioButton radio1 = (RadioButton)findViewById(R.id.radio_sessoF);
				radio1.setChecked(true);
			}
			
			//caricamento degli spinner da DB
			String compareValue=ruolo;
			if (!compareValue.equals(null)) {
		         int spinnerPosition = adapter.getPosition(compareValue);
		         spinner_ruolo.setSelection(spinnerPosition);
		         spinnerPosition = 0;
		     }		     
		     
		     compareValue = livello;
		     if (!compareValue.equals(null)) {
		         int spinnerPosition = adapter2.getPosition(compareValue);
		         spinner_livello.setSelection(spinnerPosition);
		         spinnerPosition = 0;
		     }		     

		}
		catch (JSONException ex) {
			ex.printStackTrace();
		}
		catch(UnsupportedEncodingException e){e.printStackTrace();}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
		
}
