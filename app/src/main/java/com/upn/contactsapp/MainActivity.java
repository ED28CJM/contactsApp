package com.upn.contactsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.upn.contactsapp.activities.CreateContactActivity;
import com.upn.contactsapp.activities.LoginActivity;
import com.upn.contactsapp.adapters.ContactAdaptar;
import com.upn.contactsapp.daos.ContactDAO;
import com.upn.contactsapp.entities.Contact;
import com.upn.contactsapp.services.ContactService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    List<Contact> elementos = new ArrayList<>();
    ContactAdaptar adaptar;
    int limit = 10;  // Número de contactos por página
    int currentPage = 1;  // Página actual
    boolean isLoading = false;  // Control para evitar múltiples llamadas al cargar
    private ProgressBar progressBar;  // Spinner de carga

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Configura RecyclerView y adaptador
        setUpRecyclerView();

        // Carga la primera página de contactos
        loadContacts(currentPage);
    }

    private void setUpRecyclerView() {
        RecyclerView rvContacts = findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        adaptar = new ContactAdaptar(elementos);
        rvContacts.setAdapter(adaptar);

        // Agrega un detector de scroll para cargar más al llegar al final
        rvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // Verifica si se está desplazando hacia abajo
                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) rvContacts.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    // Si llegó al final de la lista y no está cargando, carga la siguiente página
                    if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        currentPage++;  // Avanza a la siguiente página
                        loadContacts(currentPage);  // Carga más contactos
                    }
                }
            }
        });
    }

    private void loadContacts(int page) {
        isLoading = true;  // Marca como cargando
        progressBar.setVisibility(View.VISIBLE);  // Muestra el ProgressBar

        // Configura Retrofit para realizar la petición
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66d5b903f5859a7042673752.mockapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ContactService service = retrofit.create(ContactService.class);

        // Llama al servicio para obtener los contactos de la página especificada
        service.getAll(limit, page).enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contact> newContacts = response.body();
                    elementos.addAll(newContacts);
                    adaptar.notifyDataSetChanged();
                }
                isLoading = false;  // Marca como no cargando
                progressBar.setVisibility(View.GONE);  // Oculta el ProgressBar
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable throwable) {
                Log.e("MAIN_APP", "Error al cargar contactos: " + throwable.getMessage());
                isLoading = false;  // Marca como no cargando en caso de error
                progressBar.setVisibility(View.GONE);  // Oculta el ProgressBar en caso de error
            }
        });
    }
}


