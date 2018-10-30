package com.rilixtech.agendacalendarview.agenda;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.render.DefaultEventRenderer;
import com.rilixtech.agendacalendarview.render.AbstractEventRenderer;
import com.rilixtech.stickylistheaders.StickyListHeadersAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the agenda, implements StickyListHeadersAdapter.
 * Days as sections and CalendarEvents as list items.
 */
public class AgendaAdapter extends BaseAdapter implements StickyListHeadersAdapter {
  private List<CalendarEvent> mEvents;
  private AbstractEventRenderer mAbstractEventRenderer;
  private int mCurrentDayColor;

  public AgendaAdapter(int currentDayTextColor, AbstractEventRenderer<?> eventRenderer) {
    mEvents = new ArrayList<>();
    mCurrentDayColor = currentDayTextColor;
    mAbstractEventRenderer = eventRenderer == null ? new DefaultEventRenderer() : eventRenderer;
  }

  public void swapEvents(List<CalendarEvent> events) {
    this.mEvents.clear();
    this.mEvents.addAll(events);
    notifyDataSetChanged();
  }

  @Override public View getHeaderView(int position, View convertView, ViewGroup parent) {
    HeaderViewHolder holder;
    if (convertView == null) {
      holder = new HeaderViewHolder();
      AgendaHeaderView agendaHeaderView = new AgendaHeaderView(parent.getContext());
      agendaHeaderView.setCurrentDayTextColor(mCurrentDayColor);
      holder.agendaHeaderView = agendaHeaderView;
      convertView = agendaHeaderView;
      convertView.setTag(holder);
    } else {
      holder = (HeaderViewHolder) convertView.getTag();
    }

    holder.agendaHeaderView.setDay(getItem(position).getInstanceDay());
    return convertView;
  }

  private static class HeaderViewHolder {
    private AgendaHeaderView agendaHeaderView;
  }

  @Override public long getHeaderId(int position) {
    return mEvents.get(position).getInstanceDay().getTimeInMillis();
  }

  @Override public int getCount() {
    return mEvents.size();
  }

  @Override public CalendarEvent getItem(int position) {
    return mEvents.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @SuppressWarnings("unchecked") @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final CalendarEvent event = getItem(position);
    return mAbstractEventRenderer.render(parent, convertView, event);
  }
}
