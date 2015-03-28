package br.com.joaomsneto.breadcrumb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapaActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GPSTracker gps;
    ArrayList<Location> localizacoesAtualizadas = new ArrayList<Location>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        double latitude = 0;
        double longitude = 0;
        LatLng latLng;
        latLng = new LatLng(latitude, longitude);

        gps = new GPSTracker(MapaActivity.this, mMap, localizacoesAtualizadas);
        boolean canGetLocation = gps.canGetLocation();
        if( canGetLocation ) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(MapaActivity.this, latitude+" , "+longitude, Toast.LENGTH_LONG);
            latLng = new LatLng(latitude, longitude);

        } else {
            gps.showSettingsAlert();
            startActivity(MapaActivity.this.getIntent());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        Button botaoInicio = (Button) findViewById(R.id.inicio_rota_botao);
        final LatLng finalLatLng = latLng;
        botaoInicio.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapaActivity.this);
                alertDialog.setTitle(R.string.dialog_inicio_titulo);
                LayoutInflater layoutInflater = MapaActivity.this.getLayoutInflater();
                final View inflater = layoutInflater.inflate(R.layout.dialog_inicio_rota, null);
                alertDialog.setView(inflater);
                AlertDialog dialog = alertDialog.create();
                final EditText inputInicioTitulo = (EditText) inflater.findViewById(R.id.dialog_inicio_titulo);
                final EditText inputInicioDescricao = (EditText) inflater.findViewById(R.id.dialog_inicio_descricao);

                alertDialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which ) {

                        String textoInputInicioTitulo = inputInicioTitulo.getText().toString();
                        String textoInputInicioDescricao = inputInicioDescricao.getText().toString();
                        if( textoInputInicioTitulo.isEmpty() || textoInputInicioDescricao.isEmpty() ) {
                            Toast.makeText(MapaActivity.this, "Por favor, preencha os campos", Toast.LENGTH_LONG).show();
                        } else {
                            mMap.addMarker(new MarkerOptions().position(finalLatLng).title(textoInputInicioTitulo).snippet(textoInputInicioDescricao));
                        }

                        //mMap.addMarker(new MarkerOptions().position().title("A caixa é aqui!").snippet());
                    }
                });

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.create().show();

            }
        });
    }
}
