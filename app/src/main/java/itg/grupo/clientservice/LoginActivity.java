package itg.grupo.clientservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "LoginPrefs";
    @Bind(R.id.btn_login)
    Button _loginButton;
    EditText usuario;
    EditText password;

    public static String md5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                usuario = (EditText) findViewById(R.id.input_user);
                password = (EditText) findViewById(R.id.input_password);
                variables.usuario = usuario.getText().toString();
                login(variables.usuario, md5(password.getText().toString()));
                // Intent intent = new Intent(getApplicationContext(), IniciarVentaActivity.class);
                //startActivity(intent);

            }
        });


        //Leer SharedPreferences

    /*    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String logueado = settings.getString("logged", "");


        if (logueado.equals("logged")) {
            Toast.makeText(getBaseContext(), "Bienvenido", Toast.LENGTH_LONG).show();
            Singleton_ClientService.id_usuario = settings.getInt("id_usuario", 0);
            Toast.makeText(getBaseContext(), "Bienvenido " + usuario, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), IniciarVentaActivity.class);
            intent.putExtra("username", usuario);
            startActivity(intent);
        }*/


    }

    public void MetodoEjemploSharedPreferences() {
        //SharedPreferences sirve para escribir un archivo en memoria con datos del usuario
        //para mantener logueado al usuario, cuando el usuario hace login podes escribirlo,
        //si queres que la proxima vez que abra la app se mantenga logueado
        //podes usarlo si quieres

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("logged", "logged");
        //  editor.putInt("id_usuario", Response.getId_usuario);
        editor.apply();
    }

    public void login(String usuario, String password) {
        CallWebService llamada = new CallWebService();
        llamada.execute("1", usuario, password, variables.sistema);
    }

    public void cambiarPantalla() {
        Intent intent = new Intent(getApplicationContext(), IniciarVentaActivity.class);
        startActivity(intent);
    }

    public void msjErroneo() {
        //Toast.makeText(this, "Credenciales incorrectas, inicio de sesión fallido", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    class CallWebService extends AsyncTask<String, Void, String> {
        String resultado;

        @Override
        protected void onPostExecute(String s) {
            // text.setText("Square = " + s);
        }

        private String login(String usuario, String token, String sistema) {
            String result = "";
            SoapObject soapObject = new SoapObject(variables.NAMESPACE, "inicio_sesion");
            SoapObject soapObject1 = new SoapObject("", "");
            soapObject1.addProperty("usuario", usuario);
            soapObject1.addProperty("token", token);
            soapObject1.addProperty("sistema", sistema);

            PropertyInfo item = new PropertyInfo();
            item.setType(soapObject1.getClass());
            item.setName("datos_persona_entrada");
            item.setValue(soapObject1);

            soapObject.addProperty(item);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);
            HttpTransportSE httpTransportSE = new HttpTransportSE(variables.URL);
            try {
                httpTransportSE.call(variables.SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.bodyIn;
                SoapObject r2 = (SoapObject) response.getProperty(0);
                result = r2.getProperty(0).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            switch (params[0]) {
                case "1":
                    result = login(params[1], params[2], params[3]);
                    JSONObject obj = null;
                    try {
                        obj = (JSONObject) new JSONTokener(result).nextValue();
                        variables.token = obj.get("token").toString();
                        int permiso = Integer.parseInt(obj.getJSONArray("permisos").getJSONObject(0).get("codigo").toString());
                        if (permiso == 3) {
                            cambiarPantalla();
                        } else {
                            msjErroneo();
                        }

                    } catch (JSONException e) {
                        msjErroneo();
                        e.printStackTrace();
                    }
                    break;
            }

            resultado = result;
            return result;
        }
    }
}

