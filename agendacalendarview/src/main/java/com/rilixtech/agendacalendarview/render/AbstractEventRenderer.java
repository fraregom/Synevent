package com.rilixtech.agendacalendarview.render;

import android.view.View;
import android.view.ViewGroup;
import com.rilixtech.agendacalendarview.models.CalendarEvent;
import java.lang.reflect.ParameterizedType;

/**
 * Base class for helping layout rendering
 * Used in {@link com.rilixtech.agendacalendarview.agenda.AgendaAdapter} for the view renderer
 */
public abstract class AbstractEventRenderer<T extends CalendarEvent> {

  /**
   * Render the Event by using the view.
   * Always use ViewHolder pattern for improving the performance
   * @param parent ViewGroup of the view
   * @param convertView view
   * @param event event to show
   * @return View after convertView is created with ViewHolder
   */
  public abstract View render(ViewGroup parent, View convertView, final T event);

  @SuppressWarnings({"unused", "unchecked"})
  public Class<T> getRenderType() {
    ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    return (Class<T>) type.getActualTypeArguments()[0];
  }

  public abstract AbstractEventRenderer<?> copy();

}
