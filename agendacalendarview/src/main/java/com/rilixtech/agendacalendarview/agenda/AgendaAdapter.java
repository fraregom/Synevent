package com.rilixtech.agendacalendarview.agenda;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import com.rilixtech.agendacalendarview.render.DefaultEventRenderer;
import com.rilixtech.agendacalendarview.render.AbstractEventRenderer;
import com.rilixtech.stickylistheaders.StickyListHeadersAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    mEvents.clear();
    mEvents.addAll(events);
    sortList(mEvents);
    notifyDataSetChanged();
  }

  private void sortList(List<CalendarEvent> events) {
    if(events == null || events.isEmpty()) return;

    Collections.sort(events, new Comparator<CalendarEvent>() {
      public int compare(CalendarEvent o1, CalendarEvent o2) {
        Date date1 = o1.getDayReference().getDate();
        Date date2 = o2.getDayReference().getDate();

        if (date1.equals(date2)) return 0;

        return date1.before(date2) ? -1 : 1;
        //if(date1.after(date2)) return 1;
        //return o1.getStartTime().getTimeInMillis() < o2.getStartTime().getTimeInMillis() ? -1 : 1;
      }
    });
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
    return mAbstractEventRenderer.render(parent, convertView, getItem(position));
  }
}
