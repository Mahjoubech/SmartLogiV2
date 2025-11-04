package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.mapper.ClientDestinataireMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import io.github.mahjoubech.smartlogiv2.repository.ClientExpediteurRepository;
import io.github.mahjoubech.smartlogiv2.repository.DestinataireRepository;
import io.github.mahjoubech.smartlogiv2.service.ClientDestinataireService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientDestinataireServiceImpl implements ClientDestinataireService {

    private final ClientExpediteurRepository expediteurRepository;
    private final DestinataireRepository destinataireRepository;
    private final ClientDestinataireMapper mapper;
    private <T> Optional<T> findClientOrDestinataire(String clientId, Class<T> targetType) {
        if (targetType.equals(ClientExpediteur.class)) {
            return (Optional<T>) expediteurRepository.findById(clientId);
        } else if (targetType.equals(Destinataire.class)) {
            return (Optional<T>) destinataireRepository.findById(clientId);
        }
        return Optional.empty();
    }
    @Override
    @Transactional
    public ClientDestinataireResponse createExpediteur(ClientDestinataireRequest request) {
        ClientExpediteur expediteur = mapper.toClientExpediteur(request);
        // Hna khass tkoun ID generation b'UUID
        return mapper.toClientResponse(expediteurRepository.save(expediteur));
    }
    @Override
    @Transactional
    public ClientDestinataireResponse createDestinataire(ClientDestinataireRequest request) {
        Destinataire destinataire = mapper.toDestinataire(request);
        // Hna khass tkoun ID generation b'UUID
        return mapper.toDestinataireResponse(destinataireRepository.save(destinataire));
    }
    @Override
    public ClientDestinataireResponse getClientById(String clientId) {
        // N7awlo n'searchiou 3la ClientExpediteur
        Optional<ClientExpediteur> expediteurOpt = expediteurRepository.findById(clientId);
        if (expediteurOpt.isPresent()) {
            return mapper.toClientResponse(expediteurOpt.get());
        }
        Optional<Destinataire> destinataireOpt = destinataireRepository.findById(clientId);
        if (destinataireOpt.isPresent()) {
            return mapper.toDestinataireResponse(destinataireOpt.get());
        }

        throw new RuntimeException("Client/Destinataire not found with ID: " + clientId);
    }
    @Override
    @Transactional
    public ClientDestinataireResponse updateClient(String clientId, ClientDestinataireRequest request) {

        Optional<ClientExpediteur> expediteurOpt = expediteurRepository.findById(clientId);
        if (expediteurOpt.isPresent()) {
            ClientExpediteur expediteur = expediteurOpt.get();
            mapper.updateExpediteur(request, expediteur); // Méthode MapStruct dyal l'Update
            return mapper.toClientResponse(expediteurRepository.save(expediteur));
        }

        Optional<Destinataire> destinataireOpt = destinataireRepository.findById(clientId);
        if (destinataireOpt.isPresent()) {
            Destinataire destinataire = destinataireOpt.get();
            mapper.updateDestinataire(request, destinataire); // Méthode MapStruct dyal l'Update
            return mapper.toDestinataireResponse(destinataireRepository.save(destinataire));
        }

        throw new RuntimeException("Client/Destinataire not found for update: " + clientId);
    }

    @Override
    @Transactional
    public void deleteClient(String clientId) {
        // N'searchiou 3la Expéditeur
        if (expediteurRepository.existsById(clientId)) {
            expediteurRepository.deleteById(clientId);
            return;
        }

        // N'searchiou 3la Destinataire
        if (destinataireRepository.existsById(clientId)) {
            destinataireRepository.deleteById(clientId);
            return;
        }

        throw new RuntimeException("Client/Destinataire not found for deletion: " + clientId);
    }

    @Override
    public Page<ClientDestinataireResponse> searchClients(String keyword, Pageable pageable) {

        Page<ClientExpediteur> expPage = expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(keyword, keyword, pageable);

        return expPage.map(mapper::toClientResponse);
    }
}