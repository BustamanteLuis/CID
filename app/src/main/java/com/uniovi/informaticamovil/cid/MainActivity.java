package com.uniovi.informaticamovil.cid;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uniovi.informaticamovil.cid.Circuits.CircuitFragment;
import com.uniovi.informaticamovil.cid.Facilities.FacilitieFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private static final String LAST_FRAGMENT = "last_fragment";
    private static final String PREFERENCES = "SettingsMain";

    private CharSequence mTitle;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private int mCurrentIndex;
    private CircuitFragment mCf;
    private FacilitieFragment mFf;
    private Fragment fragment;
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Inicializa el drawer y toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Establecemos el nombre y el email del usuario en la cabecera del drawer
        View header=navigationView.getHeaderView(0);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        TextView name = (TextView)header.findViewById(R.id.userName);
        TextView email = (TextView)header.findViewById(R.id.userEmail);
        name.setText(SP.getString("userName", ""));
        email.setText(SP.getString("userEmail", ""));

        mSettings = getSharedPreferences(PREFERENCES, 0);

        mCurrentIndex = mSettings.getInt(LAST_FRAGMENT, 0);
        updateContentFragment();

    }

    @Override
    public void onPause() {
        super.onPause();
        // Guarda las preferencias
        mSettings = getSharedPreferences(PREFERENCES, 0);
        mSettings.edit().putInt(LAST_FRAGMENT, mCurrentIndex).commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Implementa la lógica del filtro
        if(fragment instanceof CircuitFragment)
            mCf.updateContent(query);
        else if(fragment instanceof FacilitieFragment)
            mFf.updateContent(query);

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(mCurrentIndex != id) {
            mCurrentIndex = id;

            updateContentFragment();

            mTitle = item.getTitle().toString();
            mToolbar.setTitle(mTitle);
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    public void updateContentFragment(){
        // Actualiza el contenido con el fragmento seleccionado
        if (mCurrentIndex == R.id.nav_running) {
            mCf = CircuitFragment.newInstance();
            fragment = mCf;

        } else if (mCurrentIndex == R.id.nav_facilities) {
            mFf = FacilitieFragment.newInstance();
            fragment = mFf;

        } else if(mCurrentIndex == R.id.nav_locations){
            fragment = MapFragment.newInstance();

        } else if(mCurrentIndex == R.id.nav_suggestion){
            fragment = SuggestionFragment.newInstance();

        }
        else{
            // Por defecto se carga el fragmento de los circuitos
            mCf = CircuitFragment.newInstance();
            fragment = mCf;
        }

        // Almacena el ultimo fragmento accedido
        mSettings.edit().putInt(LAST_FRAGMENT, mCurrentIndex).commit();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        fragmentTransaction.replace(R.id.content_fragment, fragment);
        fragmentTransaction.commit();

    }

}
