package codedreamer.apps.acnedetection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class History extends AppCompatActivity
{
    FirebaseAuth mAuth;
    FirebaseFirestore mfirebaseFirestore;
    RecyclerView recyclerView;
    ArrayList<userHistory> userHistoryArrayList;
    MyAdapter myAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mAuth = FirebaseAuth.getInstance();
        mfirebaseFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userHistoryArrayList = new ArrayList<userHistory>();
        myAdapter = new MyAdapter(History.this, userHistoryArrayList);
        recyclerView.setAdapter(myAdapter);
        progressDialog = new ProgressDialog(History.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting History...");
        progressDialog.show();
        mfirebaseFirestore.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).collection("diagnosis").orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
                    {
                        if(error != null)
                        {
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.d("eee", String.valueOf(error));
                            return;
                        }

                        for(DocumentChange dc: value.getDocumentChanges())
                        {
                            if(dc.getType() == DocumentChange.Type.ADDED)
                            {
                                Log.d("eee", "1");
                                userHistoryArrayList.add(dc.getDocument().toObject(userHistory.class));
                            }
                            Log.d("eee", "2");
                            myAdapter.notifyDataSetChanged();
                            Log.d("eee", "3");
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                        if(progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(History.this, "There is no Data in History", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}