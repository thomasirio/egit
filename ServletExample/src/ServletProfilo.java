import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.sql.*;

public class ServletProfilo extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private ConnessioneDB connect=new ConnessioneDB();
	  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs;
		PrintWriter writer = response.getWriter();
		
		// Recupero dei dati dalla Json in ingresso
		try {
			System.out.println("servlet profilo");
			StringBuilder sb = new StringBuilder();
		    String s;
		    while ((s = request.getReader().readLine()) != null) {
		    	sb.append(s);
		    }
		    
		    JSONObject jObj = new JSONObject(sb.toString());
		    String tiporichiesta= jObj.get("tiporichiesta").toString();
		    
		    
		    String password,nome,cognome,nickname,citta,sesso,ruolo,livello;
		    Integer eta,idcredenziali,idprofilo;
		    
		    
		    conn = connect.openConnection();
			stmt = conn.createStatement();
			
			// Esegui una query SQL, ottieni un ResultSet
			
			if(tiporichiesta.equals("crea"))
			{
				nome= jObj.get("nome").toString();
			    cognome= jObj.get("cognome").toString();
			    nickname= jObj.get("nickname").toString();
			    citta= jObj.get("citta").toString();
			    eta= Integer.parseInt(jObj.get("eta").toString());
			    sesso= jObj.get("sesso").toString();
			    ruolo= jObj.get("ruolo").toString();
			    livello= jObj.get("livello").toString();
			    idcredenziali= Integer.parseInt(jObj.get("idcredenziali").toString());
			    idprofilo=0;
			    
				//memorizzare profilo
				String sql = "INSERT INTO profilo (nome, cognome, nickname, sesso, citta, eta, livello, ruolo, idcredenziali) VALUES ('"+nome+"', '"+cognome+"', '"+nickname+"', '"+sesso+"', '"+citta+"', '"+eta+"', '"+livello+"', '"+ruolo+"', '"+idcredenziali+"')";
				stmt.executeUpdate(sql);
			
				sql = "SELECT idprofilo FROM profilo WHERE idcredenziali='"+idcredenziali+"'";
				rs = stmt.executeQuery(sql);
				if(rs.next()) {
					idprofilo=Integer.parseInt(rs.getString("idprofilo"));
					sql = "UPDATE credenziali SET idprofilo = '"+idprofilo+"' WHERE idcredenziali='"+idcredenziali+"'";
					stmt.executeUpdate(sql);				
				}
				//writer.println(idcredenziali.toString().toJson());
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("idprofilo", idprofilo);
				writer.write(jsonObj.toString());
			}
			//GET
			if(tiporichiesta.equals("leggi"))
			{
				idprofilo= Integer.parseInt(jObj.get("idprofilo").toString());
				
				String sql = "SELECT * FROM profilo WHERE idprofilo='"+idprofilo+"'";
				rs = stmt.executeQuery(sql);
				
				JSONObject jsonObj = new JSONObject();
				if(rs.next()) {
					eta=(Integer) rs.getObject("eta");
					nome=rs.getString("nome");
					cognome=rs.getString("cognome");
					nickname=rs.getString("nickname");
					citta=rs.getString("citta");
					sesso=rs.getString("sesso");
					ruolo=rs.getString("ruolo");
					livello=rs.getString("livello");
					
					jsonObj.put("eta", eta);
					jsonObj.put("nome", nome);
					jsonObj.put("cognome", cognome);
					jsonObj.put("nickname", nickname);
					jsonObj.put("citta", citta);
					jsonObj.put("sesso", sesso);
					jsonObj.put("ruolo", ruolo);
					jsonObj.put("livello", livello);
				}
				//writer.println(idcredenziali.toString().toJson());
				jsonObj.put("idprofilo", idprofilo);
				
				writer.write(jsonObj.toString());
			}
			
			//PUT -update
			if(tiporichiesta.equals("aggiorna"))
			{
				//per modificaProfilo ho anche password da aggiornare solo se non nullo
			    password= jObj.get("password").toString();
			    nome= jObj.get("nome").toString();
			    cognome= jObj.get("cognome").toString();
			    nickname= jObj.get("nickname").toString();
			    citta= jObj.get("citta").toString();
			    eta= Integer.parseInt(jObj.get("eta").toString());
			    sesso= jObj.get("sesso").toString();
			    ruolo= jObj.get("ruolo").toString();
			    livello= jObj.get("livello").toString();
			    idprofilo= Integer.parseInt(jObj.get("idprofilo").toString());
			    
				//memorizzare profilo
				String sql = "UPDATE profilo SET nome='"+nome+"', cognome='"+cognome+"', nickname='"+nickname+"', sesso='"+sesso+"', citta='"+citta+"', eta='"+eta
						+"', livello='"+livello+"', ruolo='"+ruolo+"' WHERE idprofilo='"+idprofilo+"'";
				stmt.executeUpdate(sql);
				System.out.println("psw:"+password);
				if(!password.equals(null)||password!=" "){
					if(!password.isEmpty())
					{sql = "UPDATE credenziali SET password='"+password+"' WHERE idprofilo='"+idprofilo+"'";
					stmt.executeUpdate(sql);	}			
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("idprofilo", idprofilo);
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
