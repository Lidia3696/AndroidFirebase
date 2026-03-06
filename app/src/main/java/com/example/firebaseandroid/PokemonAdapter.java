package com.example.firebaseandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Pokemon pokemon, int position);
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(Pokemon pokemon, int position);
    }

    private List<Pokemon> llistaPokemon;
    private final OnDeleteClickListener deleteListener;
    private final OnUpdateClickListener updateListener;

    public PokemonAdapter(List<Pokemon> llistaPokemon, OnDeleteClickListener deleteListener, OnUpdateClickListener updateListener) {
        this.llistaPokemon = llistaPokemon;
        this.deleteListener = deleteListener;
        this.updateListener = updateListener;
    }

    //recyclerview crida aixo per crear la tarjeta amb les dades
    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //llegeix el xml i el convrteix en una view         view_holder es el xml de la card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder, parent, false);
        //retorna la view creada
        return new PokemonViewHolder(view);
    }

    // el recyclerview el crida cada vegada que col emplemar una tarjeta amb dades, es crida er cada tarjeta
    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        //agafa la posicio i la guarda en p
        Pokemon p = llistaPokemon.get(position);

        // fa sets a cada atribut
        //holder es el xml de la tarjeta
        holder.tNom.setText(p.nom);
        holder.tAtac1.setText(p.atac1);
        holder.tAtac2.setText(p.atac2);
        holder.tAtac3.setText(p.atac3);
        holder.tAtac4.setText(p.atac4);
        // si no es shiny el text no apareix
        //si p.shiny es true, View.VISIBLE : si no, View.GONE
        holder.textIsShiny.setVisibility(p.shiny ? View.VISIBLE : View.GONE);

        holder.btnEliminar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                deleteListener.onDeleteClick(p, pos);
            }
        });

        holder.btnActualitzar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                updateListener.onUpdateClick(p, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return llistaPokemon.size();
    }

    // per actualitzar la llista despres de carregar les dades
    public void actualitzarLlista(List<Pokemon> novaLlista) {
        this.llistaPokemon = novaLlista;
        notifyDataSetChanged();
    }

    //classe que representa la tarjerta del reccleview
    static class PokemonViewHolder extends RecyclerView.ViewHolder {
        //referemcies als textviews del xml
        TextView tNom, tAtac1, tAtac2, tAtac3, tAtac4, textIsShiny;
        MaterialButton btnEliminar, btnActualitzar;

        public PokemonViewHolder(@NonNull View itemView) {
            //itemView es la tarjeta
            super(itemView);
            //busca els elements i els guarda en referencia
            tNom = itemView.findViewById(R.id.tNom);
            tAtac1 = itemView.findViewById(R.id.tAtac1);
            tAtac2 = itemView.findViewById(R.id.tAtac2);
            tAtac3 = itemView.findViewById(R.id.tAtac3);
            tAtac4 = itemView.findViewById(R.id.tAtac4);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnActualitzar = itemView.findViewById(R.id.btnActualitzar);
            textIsShiny = itemView.findViewById(R.id.textIsShiny);
        }
    }
}