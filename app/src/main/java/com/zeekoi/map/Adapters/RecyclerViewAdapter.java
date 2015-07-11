package com.zeekoi.map.Adapters;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.zeekoi.map.Managers.DBController;
import com.zeekoi.map.R;


import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView nameAddress, phone, idForDelete;
        Button buttonDelete;
        ImageView callView;


        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            nameAddress = (TextView) itemView.findViewById(R.id.name);
            phone = (TextView) itemView.findViewById(R.id.phone);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);
            callView = (ImageView) itemView.findViewById(R.id.callImg);
            idForDelete = (TextView) itemView.findViewById(R.id.idTemp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + nameAddress.getText().toString());
                }
            });
        }
    }

    private Context mContext;
    ArrayList<HashMap<String, String>> mDataset = new ArrayList<HashMap<String, String>>();
    private static String ID = "id";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";


    public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> entries) {
        this.mContext = context;
        this.mDataset = new ArrayList<HashMap<String, String>>(entries);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_list, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final DBController controller = new DBController(mContext);
        String name = mDataset.get(position).get(NAME);
        String phone = mDataset.get(position).get(PHONE);
        final String latitude = mDataset.get(position).get(LATITUDE);
        final String longitude = mDataset.get(position).get(LONGITUDE);
        String id_temp = mDataset.get(position).get(ID);
        viewHolder.idForDelete.setVisibility(View.INVISIBLE);



        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {

            }
        });

        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                SQLiteDatabase db = controller.getWritableDatabase();
                db.execSQL("DELETE from favourites WHERE _id = '" + viewHolder.idForDelete.getText().toString() + "'");
                Toast.makeText(mContext, "Deleted..!", Toast.LENGTH_SHORT).show();
                mItemManger.closeAllItems();
            }
        });

        viewHolder.callView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Swing).duration(500).delay(100).playOn(v.findViewById(R.id.callImg));

                Toast.makeText(mContext, "Calling...", Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mDataset.get(position).get(PHONE).toString()));
                mContext.startActivity(callIntent);
            }
        });
        viewHolder.nameAddress.setText(String.valueOf(Html.fromHtml(name)));
        viewHolder.phone.setText(phone);
        viewHolder.idForDelete.setText(id_temp);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}