package com.rilixtech.agendacalendarview.render;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rilixtech.agendacalendarview.R;
import com.rilixtech.agendacalendarview.models.BaseCalendarEvent;

/**
 * Class helping to inflate our default layout in the AgendaAdapter
 */
public class DefaultEventRenderer extends AbstractEventRenderer<BaseCalendarEvent> {

  @Override
  public View render(ViewGroup parent, View convertView, @NonNull BaseCalendarEvent event) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      convertView = inflater.inflate(R.layout.view_agenda_event, parent, false);
      viewHolder.tvTitle = convertView.findViewById(R.id.view_agenda_event_title);
      viewHolder.tvLocation = convertView.findViewById(R.id.view_agenda_event_location);
      viewHolder.descriptionContainer = convertView.findViewById(R.id.view_agenda_event_description_container);
      viewHolder.locationContainer = convertView.findViewById(R.id.view_agenda_event_location_container);
      viewHolder.descriptionContainerLeft = convertView.findViewById(R.id.view_agenda_event_left);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Resources resources = convertView.getResources();
    viewHolder.descriptionContainer.setVisibility(View.VISIBLE);
    viewHolder.tvTitle.setTextColor(resources.getColor(android.R.color.black));

    viewHolder.tvTitle.setText(event.getTitle());
    viewHolder.tvLocation.setText(event.getLocation());
    if (event.getLocation().length() > 0) {
      viewHolder.locationContainer.setVisibility(View.VISIBLE);
      viewHolder.tvLocation.setText(event.getLocation());
    } else {
      viewHolder.locationContainer.setVisibility(View.GONE);
    }

    if (event.getTitle().equals(resources.getString(R.string.agenda_event_no_events))) {
      viewHolder.tvTitle.setTextColor(resources.getColor(R.color.blue_selected));
    } else {
      viewHolder.tvTitle.setTextColor(resources.getColor(R.color.theme_text_icons));
    }
    viewHolder.descriptionContainerLeft.setBackgroundColor(event.getColor());
    viewHolder.tvLocation.setTextColor(resources.getColor(R.color.theme_text_icons));

    return convertView;
  }

  @Override public AbstractEventRenderer<?> copy() {
    return null;
  }

  private static class ViewHolder {
    private TextView tvTitle;
    TextView tvLocation;
    LinearLayout descriptionContainer;
    LinearLayout locationContainer;
    LinearLayout descriptionContainerLeft;
  }
}
