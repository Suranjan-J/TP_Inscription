package com.example.tp_inscription;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

public class MainActivity extends AppCompatActivity implements OnClickListener {

    ResultSet rst = null;
    public static Connection conn = null;

    private EditText ajpseudo, ajmdp, ajcmdp, ajcommentaire;
    private Button ok;
    private RadioButton easy, medium, hard, selecteddiff, selectedjour;
    private RadioGroup radioGroup_diffLevel, radioGroup_jour;
    private CheckBox lundi, mardi, mercredi, jeudi, vendredi;
    private Spinner spinner;
    private String pass, cpass;
    private int userv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());


        mysqlConnexion();

        ajpseudo = (EditText) findViewById(R.id.ajpseudo);
        ajmdp = (EditText) findViewById(R.id.ajmdp);
        ajcmdp = (EditText) findViewById(R.id.ajcmdp);
        ajcommentaire = (EditText) findViewById(R.id.ajcommentaire);

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);

        lundi = (CheckBox) findViewById(R.id.lundi);
        mardi = (CheckBox) findViewById(R.id.mardi);
        mercredi = (CheckBox) findViewById(R.id.mercredi);
        jeudi = (CheckBox) findViewById(R.id.jeudi);
        vendredi = (CheckBox) findViewById(R.id.vendredi);
        easy = (RadioButton) findViewById(R.id.easy);
        medium = (RadioButton) findViewById(R.id.medium);
        hard = (RadioButton) findViewById(R.id.hard);

        spinner = (Spinner) findViewById(R.id.spinner);

        radioGroup_diffLevel = (RadioGroup) findViewById(R.id.radioGroup_diffLevel);

        FillSpinner();
        VerificationUser();
    }

    @SuppressLint("NewApi")
    public void mysqlConnexion() {
        String jdbcURL = "jdbc:mysql://10.4.253.123:3306/inscription";
        String user = "monty";
        String passwd = "some_pass";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcURL, user, passwd);
            Toast.makeText(MainActivity.this, "Connexion reussie.", Toast.LENGTH_LONG).show();

        } catch (ClassNotFoundException e) {
            Toast.makeText(MainActivity.this, "Driver manquant." + e.getMessage().toString(), Toast.LENGTH_LONG).show();

        } catch (java.sql.SQLException ex) {
            Toast.makeText(MainActivity.this, "Connexion au serveur impossible." + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            Log.d("error", "SQLException: " + ex.getMessage());
            Log.d("error", "SQLState: " + ex.getSQLState());
            Log.d("error", "VendorError: " + ex.getErrorCode());
        }
    }

    @Override
    public void onClick(View view) {

        //Avoir le bon bouton selectionner ( RadioGroup : Niveau )
        int selecteddiffid = radioGroup_diffLevel.getCheckedRadioButtonId();
        selecteddiff = (RadioButton) findViewById(radioGroup_diffLevel.getCheckedRadioButtonId());
        selecteddiff = (RadioButton) findViewById(selecteddiffid);

        //Avoir le message de la box selectionnée ( CheckBox : Diponibilité de la semaine )
        String msg = "";

        if (lundi.isChecked())
            msg = msg + " Lundi ";
        if (mardi.isChecked())
            msg = msg + " Mardi ";
        if (mercredi.isChecked())
            msg = msg + " Mercredi ";
        if (jeudi.isChecked())
            msg = msg + " Jeudi ";
        if (vendredi.isChecked())
            msg = msg + " Vendredi ";


        //Avoir le bon spinner selectionner
        String text = spinner.getSelectedItem().toString();

        //Verification mdp
        String pass = ajmdp.getText().toString();
        String cpass = ajcmdp.getText().toString();

        //Verification si une disponibilité est check
        String dispcheck = "";

        //Si la confirmation de mot de passe n'est pas identique et si le pseudo est déjà pris, l'envoie des données n'est pas possible
        if (!pass.equals(cpass) || userv == 0) {
            //Si la confirmation de mot de passe n'est pas identique, l'envoie des données n'est pas possible
            if (!pass.equals(cpass)){
                Toast.makeText(MainActivity.this, "Mot de passe non identique", Toast.LENGTH_LONG).show();
            }
            //Si le pseudo est déjà pris, l'envoie des données n'est pas possible
            else if (userv == 0){
                Toast.makeText(MainActivity.this, "Le pseudo est déjà pris", Toast.LENGTH_LONG).show();
            }
        } else {
            //Si un niveau ou une disponibilité est pas check, l'envoie des données n'est pas possible
            if (selecteddiffid == -1 || msg == dispcheck) {
                //Si un niveau est pas check, l'envoie des données n'est pas possible
                if (selecteddiffid == -1) {
                    Toast.makeText(MainActivity.this, "Veuillez choisir un niveau", Toast.LENGTH_LONG).show();
                }
                //Si une disponibilité est pas check, l'envoie des données n'est pas possible
                else if (msg == dispcheck) {
                    Toast.makeText(MainActivity.this, "Veuillez choisir un jour de disponibilité", Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    String sqlins = "insert into informations (pseudo, mdp, commentaire, difficultee, jour, association) values (?,?,?,?,?,?)";
                    PreparedStatement pstmins = conn.prepareStatement(sqlins);
                    pstmins.setString(1, ajpseudo.getText().toString());
                    pstmins.setString(2, ajmdp.getText().toString());
                    pstmins.setString(3, ajcommentaire.getText().toString());
                    pstmins.setString(4, selecteddiff.getText().toString());
                    pstmins.setString(5, msg);
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
    }

    public void FillSpinner() {
        try {
            mysqlConnexion();
            String query = "SELECT * FROM listassociation";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> data = new ArrayList<String>();
            while (rs.next()) {
                String association = rs.getString("association");
                data.add(association);
            }
            ArrayAdapter array = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
            spinner.setAdapter(array);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int VerificationUser(){
        try {
            mysqlConnexion();
            //String verif = ajpseudo.getText().toString();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet userv = stmt.executeQuery("SELECT count(*) from informations WHERE pseudo = '"+ajpseudo+"' ");
            if (userv.next())
                return userv.getInt(1);
            else{
                return userv.getInt(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public void videTexte() {
        ajpseudo.setText("");
        ajmdp.setText("");
        ajcmdp.setText("");
        ajcommentaire.setText("");
        radioGroup_diffLevel.clearCheck();
    }
}