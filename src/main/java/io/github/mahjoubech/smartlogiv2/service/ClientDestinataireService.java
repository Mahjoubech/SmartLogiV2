package io.github.mahjoubech.smartlogiv2.service;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.ClientDestinataireResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientDestinataireService {
    /** CRUD Expéditeurs et Destinataires */
    ClientDestinataireResponse createExpediteur(ClientDestinataireRequest request);
    ClientDestinataireResponse createDestinataire(ClientDestinataireRequest request);

    ClientDestinataireResponse getClientById(String clientId);
    ClientDestinataireResponse updateClient(String clientId, ClientDestinataireRequest request);
    void deleteClient(String clientId);

    Page<ClientDestinataireResponse> searchClients(String keyword, Pageable pageable);
}
