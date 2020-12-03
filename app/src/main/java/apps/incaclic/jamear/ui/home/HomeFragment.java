package apps.incaclic.jamear.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import apps.incaclic.jamear.Fragment.CategoriaPrincipalFragment;
import com.example.jamear.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    boolean dobleatrasparasalir;
    BottomNavigationView navigationboton;


    //private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);*/

        View v = inflater.inflate(R.layout.fragment_home, container, false);


        showSelectedFragment(new CategoriaPrincipalFragment());

        navigationboton = (BottomNavigationView) v.findViewById(R.id.bnvMenu);
        navigationboton.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.tienda){
                    showSelectedFragment(new CategoriaPrincipalFragment());
                }

                /*if(item.getItemId() == R.id.carrito){
                    showSelectedFragment(new CarritoFragment());
                }*/

                return true;
            }
        });

        return v;
    }

    private void showSelectedFragment(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }


}
