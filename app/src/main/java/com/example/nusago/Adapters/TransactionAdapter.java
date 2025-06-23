package com.example.nusago.Adapters;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nusago.Models.Transaction;
import com.example.nusago.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    private final Context ctx;
    private final List<Transaction> list;

    public TransactionAdapter(Context ctx, List<Transaction> list) {
        this.ctx = ctx; this.list = list;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.card_transaction, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Transaction t = list.get(pos);
        h.inv.setText(t.getInvoice());
        h.date.setText(t.getCreatedAt());
        h.user.setText("User: " + t.getUserName());
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView inv, date, user;
        VH(View v){
            super(v);
            inv  = v.findViewById(R.id.tv_invoice);
            date = v.findViewById(R.id.tv_created);
            user = v.findViewById(R.id.tv_user);
        }
    }
}
