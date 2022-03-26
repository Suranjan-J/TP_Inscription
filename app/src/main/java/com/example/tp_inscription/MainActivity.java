package com.example.tp_inscription;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    ResultSet rst = null;
    public static Connection conn = null;
    private EditText ajpseudo, ajmdp, ajcmdp, ajcommentaire;
    private Button ok;
    private RadioButton lundi, mardi, mercredi, jeudi, vendredi, easy, medium, hard, selected;
    private RadioGroup radioGroup_diffLevel;
    private Spinner spinner;
    private String pass, cpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);


        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog() // Enregistre un message à logcat
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath() //l'application se bloque, fonctionne à //la fin de toutes les sanctions permises
                .build());

        //appel de la connexion
        MysqlConnexion();

//Déclarations
        ajpseudo = (EditText) findViewById(R.id.ajpseudo);
        ajmdp = (EditText) findViewById(R.id.ajmdp);
        ajcmdp = (EditText) findViewById(R.id.ajcmdp);
        ajcommentaire = (EditText) findViewById(R.id.ajcommentaire);

        ok = (Button) findViewById(R.id.ok);

        lundi = (RadioButton) findViewById(R.id.lundi);
        mardi = (RadioButton) findViewById(R.id.mardi);
        mercredi = (RadioButton) findViewById(R.id.mercredi);
        jeudi = (RadioButton) findViewById(R.id.jeudi);
        vendredi = (RadioButton) findViewById(R.id.vendredi);
        easy = (RadioButton) findViewById(R.id.easy);
        medium = (RadioButton) findViewById(R.id.medium);
        hard = (RadioButton) findViewById(R.id.hard);


        spinner = (Spinner) findViewById(R.id.spinner);

        radioGroup_diffLevel = (RadioGroup) findViewById(R.id.radioGroup_diffLevel);
    }

    private void MysqlConnexion() {
        String jdbcURL = "jdbc:mysql://127.0.0.1:9548/android";
        String user = "root";
        String passwd = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, user, passwd);

        } catch (ClassNotFoundException e) {
            Toast.makeText(MainActivity.this, "Driver manquant." + e.getMessage().toString(), Toast.LENGTH_LONG).show();

        } catch (java.sql.SQLException ex) {
            Toast.makeText(MainActivity.this, "Connexion au serveur impossible." + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            Log.d("error", "SQLException: " + ex.getMessage());
            Log.d("error", "SQLState: " + ex.getSQLState());
            Log.d("error", "VendorError: " + ex.getErrorCode());
        }
    } // fin de MysqlConnection

    public void addListenerOnButton() {

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                // get the selected RadioButton of the group
                selected  = (RadioButton)findViewById(radioGroup_diffLevel.getCheckedRadioButtonId());

                //get RadioButton text
                String difficulte = selected.getText().toString();

                // display it as Toast to the user
                Toast.makeText(MainActivity.this, "Selected Radio Button is:" + difficulte , Toast.LENGTH_LONG).show();

                String pass=ajmdp.getText().toString();
                String cpass=ajcmdp.getText().toString();

              if (!ajmdp.equals(ajcmdp)){
                  Toast.makeText(MainActivity.this, "Mot de passe non identique" , Toast.LENGTH_LONG).show();
              }

              else{
                try {
                    String sqlins = "insert into circuits (idCircuit, villeDepart,villeArrivee, prix, duree) values (?,?,?,?)";
                    PreparedStatement pstmins = conn.prepareStatement(sqlins);
                    pstmins.setString(1, ajpseudo.getText().toString());
                    pstmins.setString(2, ajmdp.getText().toString());
                    pstmins.setString(3, ajcommentaire.getText().toString());
                    pstmins.executeUpdate();
                    videTexte();

                } catch (SQLException seinst) {
                    Toast.makeText(MainActivity.this, "liste." + seinst.toString(), Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", seinst.getMessage());
                }
              }
            }//fin de méthode onclick

            public void videTexte() {
                ajpseudo.setText("");
                ajmdp.setText("");
                ajcmdp.setText("");
                ajcommentaire.setText("");
            }
        });

    }//Fin de classe

}