package com.momarious.query_service.service;

import com.momarious.query_service.dto.PretResponse;
import com.momarious.query_service.handler.PretQueryHandler;
import com.momarious.query_service.query.GetAllPretsQuery;
import com.momarious.query_service.query.GetPretByIdQuery;
import com.momarious.query_service.query.GetPretsByStatutQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PretQueryService {

  private final PretQueryHandler handler;

  public Page<PretResponse> getAllPrets(int page, int size) {
    GetAllPretsQuery query = new GetAllPretsQuery(page, size);
    return handler.handle(query);
  }

  public PretResponse getPretById(UUID id) {
    GetPretByIdQuery query = new GetPretByIdQuery(id);
    return handler.handle(query);
  }

  public List<PretResponse> getPretsByStatut(String status) {
    GetPretsByStatutQuery query = new GetPretsByStatutQuery(status);
    return handler.handle(query);
  }
}
