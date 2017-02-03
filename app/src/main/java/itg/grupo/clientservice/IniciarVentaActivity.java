package itg.grupo.clientservice;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import itg.grupo.clientservice.model.AudioChannel;
import itg.grupo.clientservice.model.AudioSampleRate;
import itg.grupo.clientservice.model.AudioSource;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class IniciarVentaActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "LoginPrefs";
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/audio_venta.wav";
   // Spinner _spinner_talla_ropa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_venta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logut) {

            //Reiniciar Shared Preferences
          /*  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("logged");
            editor.apply();
            Singleton_ClientService.id_usuario = 0;

            finish();*/

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();
                seleccionarResultado();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Audio was not recorded", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void seleccionarResultado() {
        LayoutInflater layoutInflater = LayoutInflater.from(IniciarVentaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.eleccion_venta, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IniciarVentaActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        Button buttonVenta = (Button) promptView.findViewById(R.id.button_venta);
        Button buttonNoVenta = (Button) promptView.findViewById(R.id.button_noventa);
        final AlertDialog alert = alertDialogBuilder.create();

        buttonVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VentaRopa();
                alert.dismiss();
            }
        });

        buttonNoVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionesNoVentaRopa();
                alert.dismiss();
            }
        });

        alert.show();
    }

    public void opcionesNoVentaRopa() {
        LayoutInflater layoutInflater = LayoutInflater.from(IniciarVentaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.noventa_ropa, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IniciarVentaActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);


        final Spinner spinner_tipo_prenda= (Spinner) promptView.findViewById(R.id.spinner_tipo_de_prenda);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.tipo_de_prenda_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner_tipo_prenda.setAdapter(adapter1);

       final Spinner spinner_talla_ropa = (Spinner) promptView.findViewById(R.id.spinner_talla_ropa);
        final Spinner spinner_colores = (Spinner) promptView.findViewById(R.id.spinner_color);

        CheckBox checkBoxTalla = (CheckBox) promptView.findViewById(R.id.checkBox_talla);
        CheckBox checkBoxColor= (CheckBox) promptView.findViewById(R.id.checkBox_color);

        alertDialogBuilder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNeutralButton("Regresar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                seleccionarResultado();

            }
        });


        final AlertDialog alert = alertDialogBuilder.create();

        spinner_tipo_prenda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             ArrayAdapter<CharSequence> adapterColores;
            ArrayAdapter<CharSequence> adapterTallas;


            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position==1){ //primera opcion es una blusa, cargamos las opciones de blusa
                    adapterTallas = ArrayAdapter.createFromResource(IniciarVentaActivity.this,
                            R.array.tallas_blusa_array, android.R.layout.simple_spinner_item);
                    adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_talla_ropa.setAdapter(adapterTallas);

                    adapterColores = ArrayAdapter.createFromResource(IniciarVentaActivity.this,
                            R.array.colores_blusa_array, android.R.layout.simple_spinner_item);
                    adapterColores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_colores.setAdapter(adapterColores);
                }else{
                    spinner_colores.setAdapter(null);
                    spinner_talla_ropa.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        checkBoxTalla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner_talla_ropa.setVisibility(View.VISIBLE);
                }else {
                    spinner_talla_ropa.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner_colores.setVisibility(View.VISIBLE);
                }else {
                    spinner_colores.setVisibility(View.INVISIBLE);
                }
            }
        });


        alert.show();
    }

    public void VentaRopa() {
        LayoutInflater layoutInflater = LayoutInflater.from(IniciarVentaActivity.this);
        View promptView = layoutInflater.inflate(R.layout.venta_ropa, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IniciarVentaActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNeutralButton("Regresar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                seleccionarResultado();

            }
        });

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void recordAudio(View v) {
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(AUDIO_FILE_PATH)
                // .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(REQUEST_RECORD_AUDIO)
                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_11025)
                .setAutoStart(false)
                .setKeepDisplayOn(true)
                // Start recording
                .record();
    }


}