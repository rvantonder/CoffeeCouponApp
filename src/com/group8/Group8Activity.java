package com.group8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.AndroidQRCode.AndroidQRCodeActivity;
import com.group8.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Group8Activity extends Activity 
{
	HttpClient client = null;
	String temp_item = "";
	String myNumber = "0837161163"; //hardcoded number of phone running application
	String contents = "";
	
	 final static String urlGoogleChart = "http://chart.apis.google.com/chart";
	 final static String urlQRApi = "?chs=400x400&cht=qr&chl=";
	 
	 ImageView QRCode;
	// TextView MySite;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        main_menu();       
    }
	
	public void main_menu()
	{
		//xml Layout to be displayed
		setContentView(R.layout.main);
	
		//Declaring spinners
		Spinner userSpinner = (Spinner) findViewById(R.id.spinner1);    
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(            
        		this, R.array.users_array, android.R.layout.simple_spinner_item);    
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        userSpinner.setAdapter(adapter);
        userSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
	
        //Declaring buttons
		final Button loginButton = (Button) findViewById(R.id.button1);
        loginButton.setOnClickListener( new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		final EditText userInput = (EditText) findViewById(R.id.editText2);
        		
        		if(userInput.getText().toString().equalsIgnoreCase(""))
				{					
        			alert("Enter Phone Number");
					return;
				} else if(userInput.getText().toString().equalsIgnoreCase("1") && temp_item.equalsIgnoreCase("Customer")) {
        			customer_menu();
        		} else if(userInput.getText().toString().equalsIgnoreCase("2") && temp_item.equalsIgnoreCase("Administrator")) {
        			admin_menu();
        		} else {
        			alert("Invalid User");
        		}
        	}
        });     
          
        //connect to HTTP server
        client = new DefaultHttpClient(); 
	}

	public void admin_menu()
	{
		//xml Layout to be displayed
		setContentView(R.layout.admin);
		
		//Declaring spinners
		Spinner adminSpinner = (Spinner) findViewById(R.id.spinner3);    
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(            
        		this, R.array.admin_array, android.R.layout.simple_spinner_item);    
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        adminSpinner.setAdapter(adapter);
        adminSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
		//Declaring buttons
		final Button backToMenu = (Button) findViewById(R.id.back1);
		final Button adminRequest = (Button) findViewById(R.id.admin_request);
		
		
		backToMenu.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
		        main_menu();     
			}
		});
		
		adminRequest.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
				final EditText adminNumberInput = (EditText) findViewById(R.id.adminText);
				//do HTTP request and display response
				
				if(adminNumberInput.getText().toString().equalsIgnoreCase(""))
				{					
					alert("Enter Phone Number");
					return;
				} else if(temp_item.equalsIgnoreCase("QR Code")) {
					//alert("Number to convert to QR " + codeRequest(adminNumberInput.getText().toString())); code display
					String convertToQR = codeRequest(adminNumberInput.getText().toString());
					generateQR(convertToQR);
				} else if(temp_item.equalsIgnoreCase("Redeem Loyalty Points")) { //if the selection is to redeem points
					String response = redeemPoints(adminNumberInput.getText().toString());
					if (response.equals("SUCCESS")) {
						alert("Transaction successful - User gets free coffee!");
					} else if (response.equals("FAIL")) {
						alert("The transaction failed - User does not have enough points.");
					} else {
						alert("Unknown response from server.");
					}
				}
				
			}
		});
	}
	
	public void customer_menu()
	{
		//xml Layout to be displayed
		setContentView(R.layout.customer);
		
		//Declaring spinner
		Spinner customerSpinner = (Spinner) findViewById(R.id.spinner2);    
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(            
        		this, R.array.customer_array, android.R.layout.simple_spinner_item);    
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
        customerSpinner.setAdapter(adapter2);
        customerSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        //Declaring buttons
        final Button requestQR = (Button) findViewById(R.id.customer_request);
		final Button backToMenu = (Button) findViewById(R.id.back2);
		
		requestQR.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if(temp_item.equalsIgnoreCase("Collect Loyalty Points")) {
					claim_points();
				} else if(temp_item.equalsIgnoreCase("View Balance")) {
					alert("Your balance is: " + balanceRequest(myNumber));
				}
			}
		});
			
		backToMenu.setOnClickListener( new View.OnClickListener()
		{
			public void onClick(View v)
			{
		        main_menu();     
			}
		});
	}
	
	public void admin_succes()
	{
		//xml Layout to be displayed
		setContentView(R.layout.admin_succes);
		
		//Declaring buttons
		final Button backToMain = (Button) findViewById(R.id.back3);
        backToMain.setOnClickListener( new View.OnClickListener()
        {
        	public void onClick(View v)
        	{
        		admin_menu();     
			}
		}); 
	}
	
	public void claim_points() // TODO QR scanner is opened...
	{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
 	   	intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
 	   	startActivityForResult(intent, 0);
 	   	onActivityResult(0,0,intent);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		   if (requestCode == 0) {
		      if (resultCode == RESULT_OK) {
		         contents = intent.getStringExtra("SCAN_RESULT");
		         String response = claimCode(myNumber, contents);
		 	     //alert(response);
		         if (response.equals("SUCCESS")) {
		        	 Toast.makeText(Group8Activity.this, "One Loyalty Point claimed", Toast.LENGTH_LONG).show();
		         } else if (response.equals("FAIL")) {
		        	 Toast.makeText(Group8Activity.this, "Unsuccessful claim", Toast.LENGTH_LONG).show();
		         }
		         // Handle successful scan
		      }
		   }
		}
	
	public void generateQR(String generate)
	{
		QRCode = (ImageView)findViewById(R.id.qrimage);
	      
	    Bitmap bm = loadQRCode(generate);
	    if(bm == null){Toast.makeText(Group8Activity.this, "Problem in loading QR Code1", Toast.LENGTH_LONG).show();}
	    else{QRCode.setImageBitmap(bm);}
	}
	
	private Bitmap loadQRCode(String generate)
	{
		Bitmap bmQR = null;
		InputStream inputStream = null;
	     
		try
			{
			inputStream = OpenHttpConnection(urlGoogleChart + urlQRApi + generate);
			bmQR = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		   } 
		catch (IOException e) {e.printStackTrace();}
		   
		return bmQR;
	   }
	   
	private InputStream OpenHttpConnection(String strURL) throws IOException
	{
		InputStream is = null;
		URL url = new URL(strURL);
		URLConnection urlConnection = url.openConnection();
	     
		try
		{
		HttpURLConnection httpConn = (HttpURLConnection)urlConnection;
		httpConn.setRequestMethod("GET");
		httpConn.connect();
		      
		if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {is = httpConn.getInputStream();}
	}
		catch (Exception ex){}	 
		return is;
	}
	
	public String codeRequest(String username) 
	{
		return queryHttp("/codeRequest?userid=", username);
	}
	public String redeemPoints(String username) 
	{
		return queryHttp("/redeemPoints?userid=", username);
	}
	public String claimCode(String username, String uc) 
	{
		return queryHttp("/claimCode?userid=", username + "Q" + uc);
	}
	
	public String balanceRequest(String username) 
	{
		String response = queryHttp("/balanceRequest?userid=", username);
		return response;
	}
	
	public String queryHttp(String path, String username) 
	{
		String errmsg = "";
		try 
		{
	        HttpGet request = new HttpGet("http://174.129.93.9"+path+username); 
	        HttpResponse responseGet = client.execute(request);  
	        BufferedReader in = new BufferedReader(new InputStreamReader(responseGet.getEntity().getContent()));
	        StringBuffer sb = new StringBuffer("");
	        String line = "";
	        String NL = System.getProperty("line.separator");
	        while ((line = in.readLine()) != null) 
	        {
	        	sb.append(line+NL);
	        }
	        in.close();
	        String response = sb.toString();
     	    //button.setText("result:" + response);
     	    return response.trim();
		  } 
		  catch (Exception e) 
		  {
			//button.setText(e.getMessage());
			e.printStackTrace();
			errmsg = e.getMessage();
		  }
		return errmsg;
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener 
	{   
		public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) 
		{
			temp_item = parent.getItemAtPosition(pos).toString();   
		}    
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// Do nothing.    }}
		}
	}	
	
	public void alert( String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
               .setCancelable(false)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
	}
}







