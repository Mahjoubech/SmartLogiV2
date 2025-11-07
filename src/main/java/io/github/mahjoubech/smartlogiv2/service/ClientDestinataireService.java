package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientDestinataireService {
    ClientDestinataireResponse createExpediteur(ClientDestinataireRequest request);
    ClientDestinataireResponse createDestinataire(ClientDestinataireRequest request);

    ClientDestinataireResponse getClientById(String clientId);
    ClientDestinataireResponse updateClient(String clientId, ClientDestinataireRequest request);
    void deleteClient(String clientId);
    Page<ClientDestinataireResponse> getAllClients(Pageable pageable);
    Page<ClientDestinataireResponse> searchClients(String keyword, Pageable pageable);
//
//    Page<ClientDestinataireResponse> searchClients(String keyword, Pageable pageable);
}
