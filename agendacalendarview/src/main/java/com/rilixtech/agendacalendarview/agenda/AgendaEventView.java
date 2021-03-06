package com.rilixtech.agendacalendarview.agenda;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.rilixtech.agendacalendarview.R;

/**
 * List item view for the StickyHeaderListView of the agenda view
 */
public class AgendaEventView extends LinearLayout {
  public static AgendaEventView inflate(ViewGroup parent) {
    return (AgendaEventView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.view_agenda_event, parent, false);
  }

  public AgendaEventView(Context context) {
    this(context, null);
  }

  public AgendaEventView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AgendaEventView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    Resources res  = getResources();
    setPadding(res.getDimensionPixelSize(R.dimen.agenda_event_view_padding_left),
        res.getDimensionPixelSize(R.dimen.agenda_event_view_padding_top),
        res.getDimensionPixelSize(R.dimen.agenda_event_view_padding_right),
        res.getDimensionPixelSize(R.dimen.agenda_event_view_padding_bottom));
  }
}