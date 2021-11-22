package mobile.application3DE.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import mobile.application3DE.R;
import mobile.application3DE.models.FinalResult;

public class FinalResultsAdapter extends RecyclerView.Adapter<FinalResultsAdapter.ViewHolder> {

    Context context;
    List<FinalResult> finalResultList;

    public FinalResultsAdapter(Context context, List<FinalResult> finalResultList) {
        this.context = context;
        this.finalResultList = finalResultList;
    }

    @NonNull
    @Override
    public FinalResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_item_layout,parent,false);
        return new FinalResultsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinalResultsAdapter.ViewHolder holder, int position) {
        if(finalResultList != null && finalResultList.size() > 0) {
            FinalResult finalResult = finalResultList.get(position);
            holder.date.setText(finalResult.getDate());
            holder.status.setText(finalResult.getStatus());
        }
        else
            return;
    }

    @Override
    public int getItemCount() {
        return finalResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView date,status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }
    }
}
