package com.kelsos.mbrc.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.dbdata.Genre;

public class GenreCursorAdapter extends CursorAdapter {

    public GenreCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.ui_list_single, viewGroup, false);
    }

    @Override public void bindView(final View view, final Context context, Cursor cursor) {
        final Genre genre = new Genre(cursor);
        ((TextView) view.findViewById(R.id.line_one)).setText(genre.getGenreName());
        view.findViewById(R.id.ui_item_context_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.showContextMenu();
            }
        });
    }
}
