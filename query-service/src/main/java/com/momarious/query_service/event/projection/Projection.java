package com.momarious.query_service.event.projection;

import com.momarious.query_service.event.InboxEvent;

public interface Projection {
  void when(InboxEvent event);
}
