package Utils;

import Http.ConnectionHandler;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public final class BearerToken {
    
    private static final String TOKEN_RESOURCE_URL = "https://api.twitter.com/oauth2/token";
    private static final String CONSUMER_KEY = "RgQf9dNuoBUEKDpneR0VVxoKX";
    private static final String CONSUMER_SECRET = "twJtLeHTBDxhgqnNO656nUNBuCz6EOAE0uENzjtfJDSWBEF7vK";
    
    private final String bearerToken;
    
    public BearerToken() throws IOException{
        bearerToken = requestBearerToken();
    }
    
    private String encodeKeys(String consumerKey, String consumerSecret){
        
        try{
            String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
            String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
            String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
            
            byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
            
            return new String(encodedBytes);
        }
        catch(UnsupportedEncodingException e){
            throw new AssertionError("Invalid encoding!", e);
        }
    }
    
    private String requestBearerToken() throws IOException{
        
        HttpURLConnection bearerCon = null;
        String encodedCredentials = encodeKeys(CONSUMER_KEY, CONSUMER_SECRET);

        try{
            URL url = new URL(TOKEN_RESOURCE_URL);
            bearerCon = (HttpURLConnection) url.openConnection();
            bearerCon.setDoOutput(true);
            bearerCon.setDoInput(true);
            bearerCon.setInstanceFollowRedirects(false);
            bearerCon.setRequestMethod("POST");
            bearerCon.setRequestProperty("Host", "api.twitter.com");
            bearerCon.setRequestProperty("User-Agent", "QualiTweeter");
            bearerCon.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            bearerCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            bearerCon.setRequestProperty("Content-Length", "29");
            bearerCon.setUseCaches(false);
            
            writeRequest(bearerCon, "grant_type=client_credentials");
            
            ConnectionHandler con = new ConnectionHandler(bearerCon);
            
            JSONObject obj = (JSONObject)JSONValue.parse(con.getResponse());
            
            if(obj != null){
                String tokenType = (String)obj.get("token_type");
                String token = (String)obj.get("access_token");
                
                return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
            }
            return new String();
        }

        finally{
            if(bearerCon != null){
                bearerCon.disconnect();
            }
        }
    }
    
    private boolean writeRequest(HttpURLConnection con, String bodyParameters) throws IOException{
        
        try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()))) {
            wr.write(bodyParameters);
            wr.flush();
        }
            
        return true;
    }
    
    public String getBearerToken(){
        return bearerToken;
    }
}
