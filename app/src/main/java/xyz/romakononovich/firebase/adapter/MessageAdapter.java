package xyz.romakononovich.firebase.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.romakononovich.firebase.models.Message;
import xyz.romakononovich.firebase.R;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemViewHolder> {
    public List<Message> list;

    public MessageAdapter(List<Message> mList) {
        list =mList;
    }

    public void addItem(Message message) {
        list.add(message);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ItemViewHolder pvh = new ItemViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.message.setText(list.get(position).getMessage());
        holder.time.setText(list.get(position).getTime());
        //holder.title.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        //private TextView title;
        private TextView time;
        private TextView message;
        public ItemViewHolder(View itemView) {
            super(itemView);
            //title = (TextView) itemView.findViewById(R.id.tv_title);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            message = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
