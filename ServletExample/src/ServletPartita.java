import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Vector;

public class ServletPartita extends HttpServlet {
	
	
	private String nomecampo;
	private String indirizzocampo;
	private String citta;
	private String provincia;
	private Float costo;
	private Integer nmancanti;
	private String data;
	private String ora;
	private String amministratore;
	private String tiporichiesta;
	private Integer idpartita;
	private static final long serialVersionUID = 1L;
	
	private ConnessioneDB connect=new ConnessioneDB();
	  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs;
		PrintWriter writer = response.getWriter();

		// Recupero i dati dalla richiesta
		try {
			System.out.println("Servlet partita");
			StringBuilder sb = new StringBuilder();
		    String s;
		    while ((s = request.getReader().readLine()) != null) {
		    	sb.append(s);
		    }		    
		    JSONObject jObj = new JSONObject(sb.toString());
		    tiporichiesta= jObj.get("tiporichiesta").toString();	
			citta= jObj.get("citta").toString();
		    provincia= jObj.get("provincia").toString();
		    		    		    
		    conn = connect.openConnection();
			stmt = conn.createStatement();			
								
			//PUT
			if(tiporichiesta.equals("crea"))
			{
				nomecampo= jObj.get("nomecampo").toString();
			    indirizzocampo= jObj.get("indirizzocampo").toString();
			    costo= Float.parseFloat(jObj.get("costo").toString());
			    nmancanti= Integer.parseInt(jObj.get("nmancanti").toString());			    
			    data= jObj.get("data").toString();
			    ora= jObj.get("ora").toString();
			    amministratore = jObj.get("amministratore").toString();
			    
				//Memorizza parita
				String sql = "INSERT INTO partita (nomecampo, indirizzocampo, citta, provincia, costo, nmancanti, data, ora, amministratore) VALUES ('"+nomecampo+"', '"+indirizzocampo+"', '"+citta.toUpperCase()+"', '"+provincia.toUpperCase()+"', '"+costo+"', '"+nmancanti+"', '"+data+"', '"+ora+"', '"+amministratore+"')";
				stmt.executeUpdate(sql);
			}
			/*writer.println(idcredenziali.toString().toJson());
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("idprofilo", idprofilo);
			writer.write(jsonObj.toString()); */
			
			//GET
			if(tiporichiesta.equals("leggi")&&(!citta.equals("null") || !provincia.equals("null")))
			{
				Vector<Partita> v = new Vector<Partita>();
				
			    String sql="";
			    if(citta.equals("null")){
			    	System.out.println("Ricerca per provincia");
			    	sql = "SELECT * FROM partita WHERE provincia='"+provincia.toUpperCase()+"'";
			    }
			    if(provincia.equals("null")){
			    	System.out.println("Ricerca per città");
			    	sql = "SELECT * FROM partita WHERE citta='"+citta.toUpperCase()+"'";
				}
			    if(!provincia.equals("null")&&!citta.equals("null")){
			    	System.out.println("Ricerca per provincia e citta");
			    	sql = "SELECT * FROM partita WHERE provincia='"+provincia.toUpperCase()+"' and citta='"+citta.toUpperCase()+"'" ;
			    }
				rs = stmt.executeQuery(sql);
				while(rs.next()) {
			    	Partita part = new Partita();
					part.setIdpartita(Integer.parseInt(rs.getString("idpartita")));					
					part.setNomecampo(rs.getString("nomecampo"));
					part.setIndirizzocampo(rs.getString("indirizzocampo"));
					part.setCosto(Float.parseFloat(rs.getString("costo")));
					part.setNmancanti(Integer.parseInt(rs.getString("nmancanti")));
					part.setData(rs.getString("data"));
					part.setOra(rs.getString("ora"));
					part.setAmministratore(rs.getString("amministratore"));
					part.setCitta(rs.getString("citta"));
					part.setProvincia(rs.getString("provincia"));
					if (part.getNmancanti()>0)
						v.add(part);
				}
				
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("jsonVector", v);
				writer.write(jsonObj.toString());
			}
			
			if(tiporichiesta.equals("leggi")&& citta.equals("null") && provincia.equals("null"))
			{
				idpartita = Integer.parseInt(jObj.get("idpartita").toString());
				System.out.println("Leggi Partita per Id");
				// Prelevo dati dal database
				String sql = "SELECT * FROM partita WHERE idpartita='"+idpartita+"'";
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					nomecampo = rs.getString("nomecampo");
					indirizzocampo=rs.getString("indirizzocampo");
					citta=rs.getString("citta");
					provincia=rs.getString("provincia");
					data=rs.getString("data");
					ora=rs.getString("ora");
					costo=Float.parseFloat(rs.getString("costo"));
					amministratore=rs.getString("amministratore");
				    nmancanti= Integer.parseInt(rs.getString("nmancanti"));	
				    sql = "SELECT email FROM credenziali WHERE idprofilo='"+amministratore+"'";
					rs = stmt.executeQuery(sql);
					if(rs.next()) amministratore = rs.getString("email");
					
					// Costruisco la Json di risposta
					jObj.put("nomecampo", nomecampo);
					jObj.put("indirizzocampo", indirizzocampo);
					jObj.put("citta", citta);
					jObj.put("provincia", provincia);
					jObj.put("data", data);
					jObj.put("ora", ora);
					jObj.put("costo", costo);
					jObj.put("amministratore", amministratore);
					jObj.put("nmancanti", nmancanti.toString());
					writer.write(jObj.toString()); 
				}
			}
			
			//POST -update
			if(tiporichiesta.equals("aggiorna"))
			{
				System.out.println("Aggiorna numero giocatori mancanti per partita");
				idpartita = Integer.parseInt(jObj.get("idpartita").toString());
				nmancanti = Integer.parseInt(jObj.get("nmancanti").toString())-1;
				String sql = "UPDATE partita SET nmancanti='"+nmancanti+"' WHERE idpartita='"+idpartita+"'";
				stmt.executeUpdate(sql);
				// Ritorna a ConfermaPartecipaActivity il n° di giocatori che mancano ancora
				sql = "SELECT nmancanti FROM partita WHERE idpartita='"+idpartita+"'";
				rs = stmt.executeQuery(sql);
				if(rs.next()) 
					nmancanti = Integer.parseInt(rs.getString("nmancanti"));
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("nmancanti", nmancanti);
				writer.write(jsonObj.toString());
			}
			//fine put
		
		} catch (SQLException | JSONException e) {
			  e.printStackTrace();
	  }
	  finally {
	  // Chiudere sempre la connessione alla base di dati
	  if (conn != null)
	  connect.closeConnection(conn);
	  }
	 } 
	

}
