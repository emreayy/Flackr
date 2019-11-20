package eay.flackr;

import android.view.View;

public interface RecyclerViewItemClickListener {

    //Tıklama olayında Yakalama
    void onItemClick(View view, int position);

    //Uzun Tıklama Olayında Yakalama
    void onItemLongClick(View view, int position);

}