package com.github.nicolediana.siriofootballfriends;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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

public class PartitaActivity extends Activity {
	private String nomeServlet="/ServletExampleOld/ServletPartita";
			
	String idprofilo;
	private String nomecampo;
	private String indirizzocampo;
	private String provincia;
	private String citta;
	private Float costo;
	private Integer nmancanti;
	private String data;
	private String ora;
	private String amministratore;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_partita);
		Intent i=getIntent();
		idprofilo= i.getStringExtra("idprofilo");
		Toast.makeText(getApplicationContext(), "Organizza una partita !", Toast.LENGTH_LONG).show();
		amministratore=idprofilo;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.partita, menu);
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

		TextView nomedig=(TextView)findViewById(R.id.nomeCampo);
		nomecampo= nomedig.getText().toString();
		TextView indirizzodig=(TextView)findViewById(R.id.indirizzoCampo);
		indirizzocampo= indirizzodig.getText().toString();
		TextView cittadig=(TextView)findViewById(R.id.citta);
		citta= cittadig.getText().toString();
		TextView provinciadig=(TextView)findViewById(R.id.provincia);
		provincia= provinciadig.getText().toString();
		TextView nmancantidig=(TextView)findViewById(R.id.nmancanti);
		nmancanti= Integer.parseInt(nmancantidig.getText().toString());
		TextView datadig=(TextView)findViewById(R.id.data);
		data= datadig.getText().toString();
		TextView oradig=(TextView)findViewById(R.id.ora);
		ora= oradig.getText().toString();
		TextView costodig=(TextView)findViewById(R.id.costo);
		costo= Float.parseFloat(costodig.getText().toString());
		
		//città non deve essere nullo
		if(citta.equals("")||citta.equals(null)||provincia.equals("")||provincia.equals(null))
			Toast.makeText(getApplicationContext(), "Citta' e/o Provincia non valide", Toast.LENGTH_LONG).show();
		else{
			JSONObject jsonobj= new JSONObject();
			try{
				String tiporichiesta="crea";
				//creazione della Json
				jsonobj.put("tiporichiesta", tiporichiesta );
				jsonobj.put("nomecampo", nomecampo );
				jsonobj.put("indirizzocampo", indirizzocampo );
				jsonobj.put("provincia", provincia );
				jsonobj.put("citta", citta );
				jsonobj.put("nmancanti", nmancanti.toString() );
				jsonobj.put("costo", costo.toString() );
				jsonobj.put("data", data );
				jsonobj.put("ora", ora );
				jsonobj.put("amministratore", amministratore );
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
				String idpartita=myjson.get("idpartita").toString();
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
	
	/*private String convertStreamToString(InputStream is) {
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
	*/

}
