package com.navn.safeshop.requests;

import android.content.Context;
import android.util.Log;
import android.util.Base64;

import com.navn.safeshop.GlobalAppClass;
import com.navn.safeshop.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import static com.navn.safeshop.GlobalAppClass.context;


public class APIRequest {
    public boolean isSuccessful() {
        return successful;
    }

    private  boolean successful;
    public   InputStream connectToMiddleTier(String accessUrl) throws MalformedURLException, ProtocolException,IOException {
        return this.connectToMiddleTier(accessUrl, null);
    }


    public   InputStream connectToMiddleTierBasicAuthTest(String accessUrl, String requestMethod , String  basicAuth) throws MalformedURLException, ProtocolException,IOException {
        String response = "Default Response";


       // String auth = GlobalAppClass.context.getString(R.string.service1)+":"+GlobalAppClass.context.getString(R.string.service2);


        if(requestMethod==null || requestMethod.isEmpty())
        {
            requestMethod = "GET";
        }
        else{
            requestMethod=requestMethod.trim();
        }
        InputStream in = null;
        try {
            URL url = new URL(accessUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String encodedAuth = basicAuth; 

            // Build the header String "Basic [Base64 encoded String]"
            String authHeader = "Basic " + new String(encodedAuth);


            // Set the created header string as actual header in your request


            conn.setRequestProperty("Authorization", authHeader);
            conn.setRequestMethod(requestMethod);

            System.out.println("Response Code: " + conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                successful = true;
            } else {
                successful = false;
            }
            in = new BufferedInputStream(conn.getInputStream());
            return in;
        } catch (MalformedURLException | ProtocolException e) {
            System.out.println("Exception in APIRequestconnectToMid.connectToMiddleTier: "+e.getMessage());
            throw e;
        } catch (IOException e) {
            System.out.println("Exception in APIRequestconnectToMid.connectToMiddleTier: "+e.getMessage());
            throw e;
        }
    }


    public   InputStream connectToMiddleTier(String accessUrl, String requestMethod ) throws MalformedURLException, ProtocolException,IOException {
        String response = "Default Response";

        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);  // Using null here initialises the TMF with the default trust store.
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Get hold of the default trust manager
        X509TrustManager defaultTm = null;
        TrustManager [] tms= tmf.getTrustManagers();
        Log.e("DEFAULT TRUST MANAGERS", String.valueOf(tms.length));
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                defaultTm = (X509TrustManager) tm;
                break;
            }
        }

        // GEtting self signed cert keystore based trust mgr
        InputStream caInput = context.getResources().openRawResource(R.raw.covidsafeshop);

        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        Certificate ca = null;
        KeyStore keyStore = null;

        try {
            ca = cf.generateCertificate(caInput);
            Log.e("CERTIFICATE=", ((X509Certificate) ca).getSubjectDN().toString());
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);

            keyStore.load(null, null);
            keyStore.setCertificateEntry("covidsafeshop", ca);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
      // Get hold of the default trust manager
        X509TrustManager myTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                myTm = (X509TrustManager) tm;
                break;
            }
        }

        // Wrap it in your own class.
        final X509TrustManager finalDefaultTm = defaultTm;
        final X509TrustManager finalMyTm = myTm;
        X509TrustManager customTm = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // If you're planning to use client-cert auth,
                // merge results from "defaultTm" and "myTm".
                return finalDefaultTm.getAcceptedIssuers();
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                try {
                    Log.e("CHECKING MY TRUST MGR", "authType : "+authType);
                    finalMyTm.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    // This will throw another CertificateException if this fails too.
                    Log.e("CHECKING MY DEFLT MGR", "authType : "+authType);
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                // If you're planning to use client-cert auth,
                // do the same as checking the server.
                finalDefaultTm.checkClientTrusted(chain, authType);
            }

        };

        try {
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL"); // Add in try catch block if you get error.

            sc.init(null, new TrustManager [] {customTm}, new java.security.SecureRandom()); // Add in try catch block if you get error.

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create all-trusting host name verifier
        HostnameVerifier trustedHostsVerifier = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                Log.i("HOST NAME : ", hostname);
                Log.i("PEER HOST NAME : ", session.getPeerHost());

                String peerHost = session.getPeerHost();
                if (context.getString(R.string.host_OLD).equals(peerHost))
                {
                    return true;
                }else if(context.getString(R.string.host).equals(peerHost)){
                    return true;
                }


                if (peerHost != null && ( peerHost.contains("google.com")|| peerHost.contains("android.com") ||  peerHost.contains("gstatic.com") ||  peerHost.contains("googleapis.com")))
                {
                    Log.i("HOST IS ", "google.com , android.com, gstatic.com, googleapis.com" );
                    return true;
                }
                if(peerHost.equals("192.168.1.14"))
                {
                    return true;
                }
                return false;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(trustedHostsVerifier);


        String auth = context.getString(R.string.service1)+":"+ context.getString(R.string.service2);
        if(requestMethod==null || requestMethod.isEmpty())
        {
            requestMethod = "GET";
        }
        else{
            requestMethod=requestMethod.trim();
        }
        InputStream in = null;
        try {
            URL url = new URL(accessUrl);
//            String newQuery  = URLEncoder.encode(url.getQuery(), StandardCharsets.UTF_8.toString());
            Log.d("Encoded URL", accessUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // use android.util.Base62 as we are using API version 21
            String encodedAuth = Base64.encodeToString(auth.getBytes(), android.util.Base64.DEFAULT);

            String authHeader = "Basic " + new String(encodedAuth);

            // Set the created header string as actual header in your request

            conn.setRequestProperty("Authorization", authHeader);

            conn.setRequestMethod(requestMethod);

            Log.i("Response Code: ", String.valueOf(conn.getResponseCode()));
            if (conn.getResponseCode() == 200) {
                successful = true;
            } else {
                successful = false;
            }
            in = new BufferedInputStream(conn.getInputStream());
            return in;
        } catch (MalformedURLException | ProtocolException e) {
            System.out.println("Exception in APIRequestconnectToMid.connectToMiddleTier: "+e.getMessage());
                throw e;

        } catch (IOException e) {
            System.out.println("Exception in APIRequestconnectToMid.connectToMiddleTier: "+e.getMessage());
            throw e;
        }

    }

}
