package com.rilixtech.sample;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rilixtech.agendacalendarview.render.AbstractEventRenderer;

public class DrawableEventRenderer extends AbstractEventRenderer<DrawableCalendarEvent> {

  //public DrawableEventRenderer() {
  //  super;
  //}

  @Override public View render(ViewGroup parent, View convertView, DrawableCalendarEvent event) {
    ImageView imageView = convertView.findViewById(R.id.view_agenda_event_image);
    TextView txtTitle = convertView.findViewById(R.id.view_agenda_event_title);
    TextView txtLocation = convertView.findViewById(R.id.view_agenda_event_location);
    LinearLayout descriptionContainer = convertView.findViewById(R.id.view_agenda_event_description_container);
    LinearLayout locationContainer = convertView.findViewById(R.id.view_agenda_event_location_container);

    descriptionContainer.setVisibility(View.VISIBLE);

    imageView.setImageDrawable(convertView.getContext().getResources().getDrawable(event.getDrawableId()));

    txtTitle.setTextColor(convertView.getResources().getColor(android.R.color.black));

    txtTitle.setText(event.getTitle());
    txtLocation.setText(event.getLocation());
    if (event.getLocation().length() > 0) {
      locationContainer.setVisibility(View.VISIBLE);
      txtLocation.setText(event.getLocation());
    } else {
      locationContainer.setVisibility(View.GONE);
    }

    if (event.getTitle().equals(convertView.getResources().getString(R.string.agenda_event_no_events))) {
      txtTitle.setTextColor(convertView.getResources().getColor(android.R.color.black));
    } else {
      txtTitle.setTextColor(convertView.getResources().getColor(R.color.theme_text_icons));
    }
    descriptionContainer.setBackgroundColor(event.getColor());
    txtLocation.setTextColor(convertView.getResources().getColor(R.color.theme_text_icons));

    return convertView;
  }

  //@Override public int getEventLayout() {
  //  return R.layout.view_agenda_drawable_event;
  //}

  @Override public Class<DrawableCalendarEvent> getRenderType() {
    return DrawableCalendarEvent.class;
  }

  @Override public AbstractEventRenderer<?> copy() {
    return new DrawableEventRenderer();
  }

  // endregion
}
