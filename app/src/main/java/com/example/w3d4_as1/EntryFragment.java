package com.example.w3d4_as1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.w3d4_as1.entities.Name;
import com.example.w3d4_as1.entities.UserDatabase;
import com.example.w3d4_as1.entities.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = EntryFragment.class.getSimpleName()+"_TAG";
    private ListButtonListener listener;

    private TextView nameTV;
    private TextView seedTV;
    private ProgressBar progressBar;
    private Button generateBTN;
    private Button saveBTN;
    private Button listBTN;

    private RandomUserAPI userAPI;
    private UserDatabase userDatabase;

    public EntryFragment() {
        // Required empty public constructor
    }

    //We'll attach a listener here for communication with MainActivity.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (ListButtonListener) getActivity();
        }
        catch (ClassCastException e){
            Log.d(TAG, "onAttach: Implement ListButtonListener in MainActivity you goober");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view){
        //Bind all the UI elements.
        nameTV = view.findViewById(R.id.nameTV);
        seedTV = view.findViewById(R.id.seedTV);
        progressBar = view.findViewById(R.id.progress);
        generateBTN = view.findViewById(R.id.generateBTN);
        saveBTN = view.findViewById(R.id.saveBTN);
        listBTN = view.findViewById(R.id.listBTN);

        generateBTN.setOnClickListener(this);
        saveBTN.setOnClickListener(this);
        listBTN.setOnClickListener(this);

        saveBTN.setEnabled(false);
    }

    //Interface for telling MainActivity to switch between fragments.
    public interface ListButtonListener{
        public void switchToList();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.generateBTN:
                generateUser();
                break;
            case R.id.saveBTN:
                saveUser();
                break;
            case R.id.listBTN:
                listener.switchToList();
                break;
        }
    }

    private void generateUser(){
        showProgress(true);
        getRandomUserAPI().getRandomUser().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    UserResponse userRes = response.body();
                    Name rawName = userRes.getResults().get(0).getName();
                    String name = rawName.getTitle() + " " + rawName.getFirst() + " " + rawName.getLast();
                    nameTV.setText(name);
                    seedTV.setText(userRes.getInfo().getSeed());
                    saveBTN.setEnabled(true);
                }else{
                    Log.e(TAG, "onResponse: server error");
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: no network access");
                showProgress(false);
            }
        });
    }

    private void showProgress(boolean isEnabled){
        if(isEnabled){
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private Retrofit prepareRetrofitClient(){
        return new Retrofit.Builder()
                .baseUrl(RandomUserAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private RandomUserAPI getRandomUserAPI(){
        if(userAPI == null){
            userAPI = prepareRetrofitClient().create(RandomUserAPI.class);
        }
        return userAPI;
    }

    private void saveUser(){
        userDatabase = new UserDatabase(getContext(),"USERS",null,1);
        SQLiteDatabase sqLiteDatabase = userDatabase.getWritableDatabase();

        if(sqLiteDatabase !=null){
            String SQL_INSERT =
                    "INSERT INTO " + UserDatabase.getTableName() + " VALUES " +
                            " ( " + "'" + seedTV.getText().toString() + "',"
                                  + "'" + nameTV.getText().toString() + "'" + " ) ";

            try{
                sqLiteDatabase.execSQL(SQL_INSERT);
                Toast.makeText(getContext(), "User saved!", Toast.LENGTH_SHORT).show();
                saveBTN.setEnabled(false);
            }catch (Exception e){
                Log.d(TAG, "saveUser: A WHOOPSIE HAS OCCURRED");
                saveBTN.setEnabled(false);
            }finally {
                sqLiteDatabase.close();
            }
        }else{
            Log.d(TAG, "saveUser: No DB to be found here.");
        }
    }
}