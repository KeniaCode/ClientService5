package itg.grupo.clientservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.btn_login)
    Button _loginButton;
    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // login();
                Intent intent = new Intent(getApplicationContext(), IniciarVentaActivity.class);
                startActivity(intent);

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

    public void MetodoEjemploSharedPreferences(){
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


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
