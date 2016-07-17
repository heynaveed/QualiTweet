package Http;


import Utils.BearerToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;


public class ConnectionHandler {
    
    private String response;
    
    public ConnectionHandler(String getResourceURL, BearerToken bToken){
        try {
            response = readResponse(requestConnection(getResourceURL, bToken.getBearerToken()));
            
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ConnectionHandler(HttpURLConnection con){
        response = readResponse(con);
    }
    
    private HttpsURLConnection requestConnection(String resourceURL, String bearerToken) throws IOException{
        
        HttpsURLConnection con = null;
        try{
            URL url = new URL(resourceURL);
            con = (HttpsURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Host", "api.twitter.com");
            con.setRequestProperty("User-Agent", "QualiTweeter");
            con.setRequestProperty("Authorization", "Bearer " + bearerToken);
            con.setUseCaches(false);
            
            return con;
        }
        catch(MalformedURLException e){
            throw new IOException("Invalid endpoint URL specified" , e);
        }
    }
    
    private String readResponse(HttpURLConnection connection){
        try{
            StringBuilder str = new StringBuilder();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            
            while((line = br.readLine()) != null){
                str.append(line + System.getProperty("line.separator"));
            }
            return str.toString();
        }
        catch (IOException e){
            return new String();
        }
    }
    
    public String getResponse(){
        return response;
    }
}
