package itg.grupo.clientservice;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import itg.grupo.clientservice.model.AudioChannel;
import itg.grupo.clientservice.model.AudioSampleRate;
import itg.grupo.clientservice.model.AudioSource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class IniciarVentaActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "LoginPrefs";
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath() + "/audio_venta.wav";
    // Spinner _spinner_talla_ropa;
    Spinner spinner_talla_ropa;
    Spinner spinner_colores;
    EditText otras_especificaciones;
    CheckBox checkBoxTalla;
    CheckBox checkBoxColor;

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            // *** DO YOUR STUFF HERE ***
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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
        if (id == R.id.action_logout) {
            createDialogExit();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Crea un diálogo de alerta sencillo
     */
    public void createDialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IniciarVentaActivity.this);

        builder.setTitle("Cerrar Sesión")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallWebService llamada = new CallWebService();
                                llamada.execute("3");
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        final AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RECORD_AUDIO) {

            if (resultCode == RESULT_OK) {
                CallWebService llamada = new CallWebService();
                llamada.execute("2");

                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show();

                uploadFile(requestCode, resultCode, data);

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


        final Spinner spinner_tipo_prenda = (Spinner) promptView.findViewById(R.id.spinner_tipo_de_prenda);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.tipo_de_prenda_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner_tipo_prenda.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter1,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));

        spinner_talla_ropa = (Spinner) promptView.findViewById(R.id.spinner_talla_ropa);
        spinner_colores = (Spinner) promptView.findViewById(R.id.spinner_color);
        otras_especificaciones = (EditText) promptView.findViewById(R.id.editText_comentario);

        checkBoxTalla = (CheckBox) promptView.findViewById(R.id.checkBox_talla);
        checkBoxColor = (CheckBox) promptView.findViewById(R.id.checkBox_color);

        alertDialogBuilder.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String comentario = otras_especificaciones.getText().toString();

                if (checkBoxTalla.isChecked()) {
                    String talla = spinner_talla_ropa.getSelectedItem().toString();
                }

                if (checkBoxTalla.isChecked()) {
                    String color = spinner_colores.getSelectedItem().toString();
                }

                //Método para guardar en la DB los valores de no venta

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
                if (position == 1) { //primera opcion es una blusa, cargamos las opciones de blusa
                    LlenarSpinners(R.array.tallas_blusa_tshirt_array, R.array.colores_array);
                } else if (position == 2) {
                    LlenarSpinners(R.array.tallas_blusa_tshirt_array, R.array.colores_array);
                } else if (position == 3) {
                    LlenarSpinners(R.array.tallas_camisa_array, R.array.colores_array);
                } else if (position == 4) {
                    LlenarSpinners(R.array.tallas_pantalones_array, R.array.colores_array);
                } else if (position == 5) {
                    LlenarSpinners(R.array.tallas_zapatos_array, R.array.colores_array);
                } else if (position == 6) {
                    LlenarSpinners(R.array.tallas_pantalones_array, R.array.colores_array);
                } else if (position == 7) {
                    LlenarSpinners(R.array.tallas_blusa_tshirt_array, R.array.colores_array);
                } else if (position == 8) {
                    LlenarSpinners(R.array.tallas_pantalones_array, R.array.colores_array);
                } else {
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
                } else {
                    spinner_talla_ropa.setVisibility(View.INVISIBLE);
                }
            }
        });


        checkBoxColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner_colores.setVisibility(View.VISIBLE);
                } else {
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


        EditText totalEditText = (EditText) promptView.findViewById(R.id.editText_total);

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

    public void LlenarSpinners(int arrayTallas, int arrayColores) {

        ArrayAdapter<CharSequence> adapterColores;
        ArrayAdapter<CharSequence> adapterTallas;

        adapterTallas = ArrayAdapter.createFromResource(IniciarVentaActivity.this,
                arrayTallas, android.R.layout.simple_spinner_item);
        adapterTallas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_talla_ropa.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapterTallas,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));

        adapterColores = ArrayAdapter.createFromResource(IniciarVentaActivity.this,
                arrayColores, android.R.layout.simple_spinner_item);
        adapterColores.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_colores.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapterColores,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));
    }

    public void recordAudio(View v) {
        CallWebService llamada = new CallWebService();
        llamada.execute("1");

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

    //*********************CODIGO AGREGADO POR KEVIN ******************************************//

    public void uploadFile(int requestCode, int resultCode, final Intent data) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                File f = new File(AUDIO_FILE_PATH);
                String content_type = getMimeType(f.getPath());

                String file_path = f.getAbsolutePath();
                OkHttpClient client = new OkHttpClient();
                RequestBody file_body = RequestBody.create(MediaType.parse(content_type), f);

                RequestBody request_body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", content_type)
                        .addFormDataPart("uploaded_file", file_path.substring(file_path.lastIndexOf("/") + 1), file_body)
                        .build();

                Request request = new Request.Builder()
                        .url("http://67.205.153.106/sitioClientService/subirAudio.php")
                        .post(request_body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Error : " + response);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();
    }

    private String getMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    class CallWebService extends AsyncTask<String, Void, String> {
        String resultado;

        @Override
        protected void onPostExecute(String s) {
            // text.setText("Square = " + s);
        }

        private String iniciarVenta() {
            String result = "";
            SoapObject soapObject = new SoapObject(variables.NAMESPACE, "crearVenta");
            SoapObject soapObject1 = new SoapObject("", "");
            soapObject1.addProperty("token", variables.token);
            soapObject1.addProperty("sistema", variables.sistema);

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

        private String finVenta() {
            String result = "";
            SoapObject soapObject = new SoapObject(variables.NAMESPACE, "finalizarVenta");
            SoapObject soapObject1 = new SoapObject("", "");
            soapObject1.addProperty("mensaje", variables.venta);

            PropertyInfo item = new PropertyInfo();
            item.setType(soapObject1.getClass());
            item.setName("esactivo");
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

        private String cerrarSesion() {
            String result = "";
            SoapObject soapObject = new SoapObject(variables.NAMESPACE, "cerrarSesion");
            SoapObject soapObject1 = new SoapObject("", "");
            soapObject1.addProperty("usuario", variables.usuario);
            soapObject1.addProperty("token", variables.token);
            soapObject1.addProperty("sistema", variables.sistema);

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
                    result = iniciarVenta();
                    try {
                        variables.venta = result;
                       /* AUDIO_FILE_PATH =
                                Environment.getExternalStorageDirectory().getPath() + "/"+variables.venta+".wav";*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "2":
                    result = finVenta();
                    try {
                        System.out.println(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "3":
                    result = cerrarSesion();
                    if (result.equals("1")) {
                        finish();
                    }
                    break;
            }

            resultado = result;
            return result;
        }
    }
}