package codedreamer.apps.acnedetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    Context context;
    ArrayList<userHistory> userHistoryArrayList;

    public MyAdapter(Context context, ArrayList<userHistory> userHistoryArrayList)
    {
        this.context = context;
        this.userHistoryArrayList = userHistoryArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position)
    {
        userHistory userHistory = userHistoryArrayList.get(position);
        holder.date.setText(userHistory.date);
        holder.time.setText(userHistory.time);
        holder.area.setText(userHistory.area);
        holder.advice.setText(userHistory.advice);
        holder.medicine.setText(userHistory.medication);
        holder.acneType.setText(userHistory.acneType);
        holder.gender.setText(userHistory.gender);
        holder.sleeping_hours.setText(userHistory.sleepingHours);
        holder.age.setText(userHistory.age);
        holder.skinType.setText(userHistory.skinType);
        holder.spicyFood.setText(userHistory.spicyFood);
    }

    @Override
    public int getItemCount()
    {
        return userHistoryArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date,time,area,advice,medicine,acneType,gender,sleeping_hours,age,skinType,spicyFood;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            date = itemView.findViewById(R.id.tv_date);
            time = itemView.findViewById(R.id.tv_time);
            area = itemView.findViewById(R.id.tv_area);
            advice = itemView.findViewById(R.id.tv_advice);
            medicine = itemView.findViewById(R.id.tv_medicine);
            acneType = itemView.findViewById(R.id.tv_acneType);
            gender = itemView.findViewById(R.id.tv_gender);
            sleeping_hours = itemView.findViewById(R.id.tv_sleeping_hours);
            age = itemView.findViewById(R.id.tv_age);
            skinType = itemView.findViewById(R.id.tv_skinType);
            spicyFood = itemView.findViewById(R.id.tv_spicyFood);
        }
    }
}
