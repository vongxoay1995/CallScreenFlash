package com.call.colorscreen.ledflash.ui.main.themes;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int horiZonSpaceHeight;
    public SimpleDividerItemDecoration(int horiZonSpaceHeight) {
        this.horiZonSpaceHeight = horiZonSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.top = horiZonSpaceHeight;
        outRect.right = horiZonSpaceHeight;
        Log.e("TAN", "getItemOffsets: "+horiZonSpaceHeight);
    }
}
