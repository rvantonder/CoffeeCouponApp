package com.AndroidQRCode;
 
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
 
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidQRCodeActivity extends Activity
{
  
 final static String urlGoogleChart = "http://chart.apis.google.com/chart";
 final static String urlQRApi = "?chs=400x400&cht=qr&chl=";
 
 ImageView QRCode;
 TextView MySite;
  
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       MySite = (TextView)findViewById(R.id.mysite);
       
       //GENERATE QR CODE
       final Button genbutton = (Button) findViewById(R.id.button1);
       genbutton.setOnClickListener(new View.OnClickListener()
       {
	       @Override
		public void onClick(View v)
	       {
	    	   final EditText textfield = (EditText) findViewById(R.id.editText1);
	    	   if (textfield.getText().length()==0)
	    	   {MySite.setText("You have not entered a valid string");}
	    	   else{generateQR(""+textfield.getText());}
	       }
       }); 
       
       //SCAN QR CODE
       final Button scanbutton = (Button) findViewById(R.id.button2);
       scanbutton.setOnClickListener(new View.OnClickListener()
       {
	       @Override
		public void onClick(View v2)
	       {
	    	   Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    	   intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    	   startActivityForResult(intent, 0);
	    	   onActivityResult(0,0,intent);
	       }
       }); 
   }
   
   @Override
public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	   if (requestCode == 0) {
	      if (resultCode == RESULT_OK) {
	         String contents = intent.getStringExtra("SCAN_RESULT");
	         String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	         MySite.setText("QR Scan Result: "+contents+"\nFormat: "+format);
	         // Handle successful scan
	      } else if (resultCode == RESULT_CANCELED) {
	         // Handle cancel
	      }
	   }
	}

   
   public void generateQR(String generate)
   {
	   QRCode = (ImageView)findViewById(R.id.qrimage);
       
       MySite.setText("QR Code generated for string: "+generate);
       
       Bitmap bm = loadQRCode(generate);
       if(bm == null){Toast.makeText(AndroidQRCodeActivity.this, "Problem in loading QR Code1", Toast.LENGTH_LONG).show();}
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
   
   public final Button.OnClickListener scanQRCode = new Button.OnClickListener() {
	    @Override
		public void onClick(View v) {
	      Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	      intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	      startActivityForResult(intent, 0);
	    }
	  };
	  
	  


   
   
}