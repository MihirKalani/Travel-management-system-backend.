package com.travelManagement.tms.service;

import com.travelManagement.tms.dto.TravelRequestDTO;
import com.travelManagement.tms.dto.TravelRequestResponseDTO;

import java.util.List;

// Service interface for travel request operations
public interface TravelRequestService {
    TravelRequestResponseDTO saveDraft(TravelRequestDTO dto);
    TravelRequestResponseDTO submitRequest(Long id);
    void deleteDraft(Long id);
    List<TravelRequestResponseDTO> getMyRequests(Long userId);
    TravelRequestResponseDTO getById(Long id);
    List<TravelRequestResponseDTO> getAll();
    List<TravelRequestResponseDTO> getPendingForManager(Long managerId);
    List<TravelRequestResponseDTO> getPendingForFinance();
}
