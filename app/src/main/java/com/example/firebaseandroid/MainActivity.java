package com.example.firebaseandroid;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivitypokemon";
    private static final String dbUrl = "https://fir-android-b2ab0-default-rtdb.europe-west1.firebasedatabase.app";

    TextInputEditText nomPokemon;
    TextInputEditText atac1Pokemon;
    TextInputEditText atac2Pokemon;
    TextInputEditText atac3Pokemon;
    TextInputEditText atac4Pokemon;
    CheckBox shinyPokemon;
    Button afegirPokemon;
    TextView title;
    private RecyclerView rvPokemon;
    private PokemonAdapter adapter;
    private List<Pokemon> llistaPokemon = new ArrayList<>();

    // id del pokemon que esta sent editat
    private String pokemonEditantId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // declarar boton i textviews

         afegirPokemon = findViewById(R.id.buttonAfegir);
         title = findViewById(R.id.title);

         nomPokemon = findViewById(R.id.tbxNom);
         atac1Pokemon = findViewById(R.id.tbxAtac1);
         atac2Pokemon = findViewById(R.id.tbxAtac2);
         atac3Pokemon = findViewById(R.id.tbxAtac3);
         atac4Pokemon = findViewById(R.id.tbxAtac4);
         //checkbox
         shinyPokemon = findViewById(R.id.CBshiny);

         //recyclerview
        rvPokemon = findViewById(R.id.rvPokemon);

        adapter = new PokemonAdapter(llistaPokemon,
                (pokemon, position) -> borrarPokemon(pokemon.id, position),
                (pokemon, position) -> prepararEdicio(pokemon)

        );

        //(pokemon, position) -> actualizarPokemon(pokemon.id, pokemon.nom, pokemon.atac1, pokemon.atac2, pokemon.atac3, pokemon.atac4, pokemon.shiny)

        rvPokemon.setLayoutManager(new LinearLayoutManager(this));
        rvPokemon.setAdapter(adapter);

        //boto afegir
        afegirPokemon.setOnClickListener(v -> crearPokemon());

        llegirPokemon();
    }

    private void crearPokemon() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("pokemon");

        Map<String, Object> pokemon = new HashMap<>();
        pokemon.put("nom", nomPokemon.getText().toString());
        pokemon.put("atac1", atac1Pokemon.getText().toString());
        pokemon.put("atac2", atac2Pokemon.getText().toString());
        pokemon.put("atac3", atac3Pokemon.getText().toString());
        pokemon.put("atac4", atac4Pokemon.getText().toString());
        pokemon.put("shiny", shinyPokemon.isChecked());

        //guardar a la bd
        dbRef.push().setValue(pokemon);

        reiniciarPantalla();

    }


    private void llegirPokemon() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("pokemon");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //buidar llista per no duplciar dades
                llistaPokemon.clear();

                if (snapshot.exists()) {
                    String ultimPokemonId = null;
                    for (DataSnapshot pokemonsnap : snapshot.getChildren()) {
                        String id = pokemonsnap.getKey();
                        String nom = pokemonsnap.child("nom").getValue(String.class);
                        String atac1 = pokemonsnap.child("atac1").getValue(String.class);
                        String atac2 = pokemonsnap.child("atac2").getValue(String.class);
                        String atac3 = pokemonsnap.child("atac3").getValue(String.class);
                        String atac4 = pokemonsnap.child("atac4").getValue(String.class);
                        Boolean shiny = pokemonsnap.child("shiny").getValue(Boolean.class);

                        ultimPokemonId = pokemonsnap.getKey();
                        Log.d(TAG, "pokemon ID: " + ultimPokemonId);
                        Log.d(TAG, "Nom: " + nom);
                        Log.d(TAG, "Atac 1: " + atac1);
                        Log.d(TAG, "Atac 2: " + atac2);
                        Log.d(TAG, "Atac 3: " + atac3);
                        Log.d(TAG, "Atac 4: " + atac4);
                        Log.d(TAG, "Shiny: " + shiny);
                        Log.d(TAG, "-----------------------");

                        //afegir a llista per poder editarlos i eliminarlos
                        llistaPokemon.add(new Pokemon( id, nom, atac1, atac2, atac3, atac4, shiny != null && shiny));
                    }

                    adapter.actualitzarLlista(llistaPokemon);

                    //actualizarpokemon(ultimPokemonId,"nom 1", "atacs ... ", false);
                    //borrarpokemon(ultimPokemonId);
                } else {
                    Log.d(TAG, "No hay pokemon");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error leyendo pokemon: " + error.getMessage());
            }
        });
    }

    private void borrarPokemon(String pokemonId, int position) {
        Log.d(TAG, "Borrar pokemon: " + pokemonId);
        DatabaseReference dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("pokemon");
        if (pokemonId != null) {
            dbRef.child(pokemonId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < llistaPokemon.size()) {
                            llistaPokemon.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, llistaPokemon.size());
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error borrando", e));
        } else {
            Log.e("FirebaseError", "The Pokemon ID is null and cannot be deleted.");
        }

    }

    private void prepararEdicio(Pokemon pokemon) {
        // Guardar el ID del pokemon que se está editando
        pokemonEditantId = pokemon.id;

        // emplenar textinputs amb les dades
        nomPokemon.setText(pokemon.nom);
        atac1Pokemon.setText(pokemon.atac1);
        atac2Pokemon.setText(pokemon.atac2);
        atac3Pokemon.setText(pokemon.atac3);
        atac4Pokemon.setText(pokemon.atac4);
        shinyPokemon.setChecked(pokemon.shiny);

        // canviar boto i titol
        afegirPokemon.setText("Actualitzar Pokémon");
        title.setText("Editar Pokémon");

        // canviar listener del boto per a que actualitzi en comptes de crear
        afegirPokemon.setOnClickListener(v -> confirmarActualitzacio());
    }
    //actulitzar poke
    private void confirmarActualitzacio() {
        if (pokemonEditantId == null) return;

        actualizarPokemon(
                pokemonEditantId,
                nomPokemon.getText().toString(),
                atac1Pokemon.getText().toString(),
                atac2Pokemon.getText().toString(),
                atac3Pokemon.getText().toString(),
                atac4Pokemon.getText().toString(),
                shinyPokemon.isChecked()
        );


        //reinicia id
        pokemonEditantId = null;
    }

    private void actualizarPokemon(String pokemonId, String nounom, String nouatac1, String nouatac2, String nouatac3, String nouatac4, Boolean noushiny) {
        Log.d(TAG, "Actualizar pokemon: " + pokemonId);

        //canvis a firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("pokemon").child(pokemonId);

        Map<String, Object> cambios = new HashMap<>();
        if (nounom != null) cambios.put("nom", nounom);
        if (nouatac1 != null) cambios.put("atac1", nouatac1);
        if (nouatac2 != null) cambios.put("atac2", nouatac2);
        if (nouatac3 != null) cambios.put("atac3", nouatac3);
        if (nouatac4 != null) cambios.put("atac4", nouatac4);
        if (noushiny != null) cambios.put("shiny", noushiny);

        dbRef.updateChildren(cambios)
                .addOnSuccessListener(aVoid -> {
                    llegirPokemon();
                    Log.d(TAG, "Pokémon actualizado correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error actualizando Pokémon", e);
                });

        reiniciarPantalla();
    }

    private void reiniciarPantalla() {

        //reiniciar els valors
        nomPokemon.setText(null);
        atac1Pokemon.setText(null);
        atac2Pokemon.setText(null);
        atac3Pokemon.setText(null);
        atac4Pokemon.setText(null);
        shinyPokemon.setChecked(false);

        afegirPokemon.setText("+ Afegir Pokémon");
        title.setText("Nou Pokémon");

        //restaurar listener al de crear
        afegirPokemon.setOnClickListener(v -> crearPokemon());


        //recarregar llista
        llegirPokemon();
    }

}