package com.karthik.temperature_conversion;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
;
    private Button convertButton;
    private TextView resultTextView;
    private EditText temperatureEditText;
    private static final  String NAMESPACE = "http://www.webserviceX.NET/";
    private static final String URL = "http://www.webserviceX.NET/ConvertTemperature.asmx";
    private static final String SOAP_ACTION = "http://www.webserviceX.NET/ConvertTemp";
    private static final String METHOD = "ConvertTemp";
    private String[] conversion;
    private String input,result;
    private Spinner fromSpinner;
    private Spinner toSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        conversion = new String[]{"degreeCelsius","degreeFahrenheit","degreeRankine","degreeReaumur","kelvin"};
        convertButton = (Button) findViewById(R.id.button_convert);
        resultTextView = (TextView) findViewById(R.id.text_view_result);
        temperatureEditText = (EditText) findViewById(R.id.edit_text_c);
        fromSpinner = (Spinner) findViewById(R.id.spinner_from);
        toSpinner = (Spinner) findViewById(R.id.spinner_to);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,conversion);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               input = temperatureEditText.getText().toString();
               AsyncWS asyncWS = new AsyncWS();
                asyncWS.execute(new String[]{input,fromSpinner.getSelectedItem().toString(),toSpinner.getSelectedItem().toString()});

            }
        });


    }
    public  class AsyncWS extends AsyncTask<String[],Void,String >{

        @Override
        protected String doInBackground(String[]... strings) {
             getResult(strings[0][0],strings[0][1],strings[0][2]);
            return null;
        }

        private void getResult(String degree,String from, String to) {

            SoapObject request = new SoapObject(NAMESPACE,METHOD);

            PropertyInfo input = new PropertyInfo();
            input.setName("Temperature");
            input.setValue(degree);
            input.setType(String.class);

            request.addProperty(input);

            PropertyInfo fromUnit = new PropertyInfo();
            fromUnit.setName("FromUnit");
            fromUnit.setValue(from);
            fromUnit.setType(String.class);

            request.addProperty(fromUnit);

            PropertyInfo toUint = new PropertyInfo();
            toUint.setName("ToUnit");
            toUint.setValue(to);
            toUint.setType(String.class);

            request.addProperty(toUint);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTrans = new HttpTransportSE(URL);
            try {
                httpTrans.call(SOAP_ACTION,envelope);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            SoapPrimitive soapPrimitive = null;
            try {
                soapPrimitive = (SoapPrimitive) envelope.getResponse();
            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
            }

            result = (soapPrimitive==null)? "Error": soapPrimitive.toString();



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            resultTextView.setText(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultTextView.setText("calculating...");
        }
    }


}
