package com.example.tp_inscription;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    ResultSet rst = null;
    public static Connection conn = null;
    private EditText ajpseudo, ajmdp, ajcmdp, ajcommentaire;
    private Button ok;
    private RadioButton lundi, mardi, mercredi, jeudi, vendredi, easy, medium, hard, selecteddiff, selectedjour;
    private RadioGroup radioGroup_diffLevel, radioGroup_jour;
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
        ok.setOnClickListener(this);

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
        radioGroup_jour = (RadioGroup) findViewById(R.id.radioGroup_jour);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = spinner.getSelectedItem().toString();
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void MysqlConnexion() {
        String jdbcURL = "jdbc:mysql://192.168.1.20:3306/inscription";
        String user = "username";
        String passwd = "password";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, user, passwd);
            Toast.makeText(MainActivity.this, "Connexion reussie.", Toast.LENGTH_LONG).show();

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM association";
            ResultSet res = stmt.executeQuery(sql);

            ArrayList<String> data = new ArrayList<String>();
            while (res.next()) {
                String id = res.getString("listassociation");
                data.add(id);
            }
            String[] array = data.toArray(new String[0]);
            ArrayAdapter NoCoreAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
            spinner.setAdapter(NoCoreAdapter);

        } catch (ClassNotFoundException e) {
            Toast.makeText(MainActivity.this, "Driver manquant." + e.getMessage().toString(), Toast.LENGTH_LONG).show();

        } catch (java.sql.SQLException ex) {
            Toast.makeText(MainActivity.this, "Connexion au serveur impossible." + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            Log.d("error", "SQLException: " + ex.getMessage());
            Log.d("error", "SQLState: " + ex.getSQLState());
            Log.d("error", "VendorError: " + ex.getErrorCode());
        }
    } // fin de MysqlConnection

    @Override
    public void onClick(View view) {

        //Avoir le bon bouton selectionner ( RadioGroup : Niveau )
        int selecteddiffid = radioGroup_diffLevel.getCheckedRadioButtonId();
        selecteddiff  = (RadioButton) findViewById(radioGroup_diffLevel.getCheckedRadioButtonId());
        selecteddiff  = (RadioButton) findViewById(selecteddiffid);

        //Avoir le bon bouton selectionner ( RadioGroup : Diponibilité de la semaine )
        int selectedjourid = radioGroup_jour.getCheckedRadioButtonId();
        selectedjour = (RadioButton) findViewById(radioGroup_jour.getCheckedRadioButtonId());
        selectedjour  = (RadioButton) findViewById(selectedjourid);

        //Avoir le bon spinner selectionner
        String text = spinner.getSelectedItem().toString();

        //Verification mdp
        String pass=ajmdp.getText().toString();
        String cpass=ajcmdp.getText().toString();

        if (!pass.equals(cpass)){
                  Toast.makeText(MainActivity.this, "Mot de passe non identique" , Toast.LENGTH_LONG).show();
              }
        else{
            if (selecteddiffid  == -1 || selectedjourid == -1) {
                if (selecteddiffid  == -1){
                    Toast.makeText(MainActivity.this, "Veuillez choisir un niveau" , Toast.LENGTH_LONG).show();
                }
                else if (selectedjourid == -1){
                    Toast.makeText(MainActivity.this, "Veuillez choisir un jour" , Toast.LENGTH_LONG).show();
                }
            }
            else{
                try {
                    String sqlins = "insert into informations (pseudo, mdp, commentaire, difficultee, jour, association) values (?,?,?,?,?,?)";
                    PreparedStatement pstmins = conn.prepareStatement(sqlins);
                    pstmins.setString(1, ajpseudo.getText().toString());
                    pstmins.setString(2, ajmdp.getText().toString());
                    pstmins.setString(3, ajcommentaire.getText().toString());
                    pstmins.setString(4, selecteddiff.getText().toString());
                    pstmins.setString(5, selectedjour.getText().toString());
                    pstmins.setString(6, spinner.getSelectedItem().toString());

                    pstmins.executeUpdate();
                    Toast.makeText(MainActivity.this, "Donnée envoyer", Toast.LENGTH_LONG).show();
                    videTexte();

                } catch (SQLException seinst) {
                    Toast.makeText(MainActivity.this, "liste." + seinst.toString(), Toast.LENGTH_LONG).show();
                    Log.d("MainActivity", seinst.getMessage());
                }
            }
        }
    }//fin de méthode onclick

    public void videTexte() {
        ajpseudo.setText("");
        ajmdp.setText("");
        ajcmdp.setText("");
        ajcommentaire.setText("");
        radioGroup_diffLevel.clearCheck();
        radioGroup_jour.clearCheck();
    }

}