package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.dto.request.ClientDestinataireRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.detail.ClientDestinataireResponse;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.mapper.ClientDestinataireMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.ClientExpediteur;
import io.github.mahjoubech.smartlogiv2.model.entity.Destinataire;
import io.github.mahjoubech.smartlogiv2.repository.ClientExpediteurRepository;
import io.github.mahjoubech.smartlogiv2.repository.DestinataireRepository;
import io.github.mahjoubech.smartlogiv2.service.ClientDestinataireService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientDestinataireServiceImpl implements ClientDestinataireService {

    private final ClientExpediteurRepository expediteurRepository;
    private final DestinataireRepository destinataireRepository;
    private final ClientDestinataireMapper clientDestinataireMapper;
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
         Optional<ClientExpediteur> exist = expediteurRepository.findByEmail(request.getEmail());
            if (exist.isPresent()){
                throw new ResourceNotFoundException("Expéditeur with email "+request.getEmail()+" already exists");
            }
        ClientExpediteur expediteur = clientDestinataireMapper.toClientExpediteur(request);
        return clientDestinataireMapper.toClientResponse(expediteurRepository.save(expediteur));
    }
    @Override
    @Transactional
    public ClientDestinataireResponse createDestinataire(ClientDestinataireRequest request) {
        Optional<Destinataire> exist = destinataireRepository.findByEmail(request.getEmail());
        if (exist.isPresent()){
            throw new ResourceNotFoundException("Destinataire with email "+request.getEmail()+" already exists");
        }
        Destinataire destinataire = clientDestinataireMapper.toDestinataire(request);
        return clientDestinataireMapper.toDestinataireResponse(destinataireRepository.save(destinataire));
    }

    @Override
    @Transactional
    public ClientDestinataireResponse getClientById(String clientId) {
        Optional<ClientExpediteur> expediteurOpt = expediteurRepository.findById(clientId);
        Optional<Destinataire> destinataireOpt = destinataireRepository.findById(clientId);
        if (expediteurOpt.isPresent()) {
            return clientDestinataireMapper.toClientResponse(expediteurOpt.get());
        } else if (destinataireOpt.isPresent()) {
            return clientDestinataireMapper.toDestinataireResponse(destinataireOpt.get());
        }
        throw new ResourceNotFoundException("Client/Destinataire", "ID", clientId);
    }
    @Override
    @Transactional
    public ClientDestinataireResponse updateClient(String clientId, ClientDestinataireRequest request) {

        Optional<ClientExpediteur> expediteurOpt = expediteurRepository.findById(clientId);

        if (expediteurOpt.isPresent()) {
            ClientExpediteur expediteur = expediteurOpt.get();
            clientDestinataireMapper.updateExpediteur(request, expediteur);
            return clientDestinataireMapper.toClientResponse(expediteurRepository.save(expediteur));
        }
        Optional<Destinataire> destinataireOpt = destinataireRepository.findById(clientId);

        if (destinataireOpt.isPresent()) {
            Destinataire destinataire = destinataireOpt.get();
            clientDestinataireMapper.updateDestinataire(request, destinataire);
            return clientDestinataireMapper.toDestinataireResponse(destinataireRepository.save(destinataire));
        }
        throw new ResourceNotFoundException("Client/Destinataire", "ID", clientId);
    }


    @Override
    @Transactional
    public void deleteClient(String clientId) {
        if (expediteurRepository.existsById(clientId)) {
            expediteurRepository.deleteById(clientId);
            return;
        }

        if (destinataireRepository.existsById(clientId)) {
            destinataireRepository.deleteById(clientId);
            return;
        }

        throw new RuntimeException("Client/Destinataire not found for deletion: " + clientId);
    }
    @Override
    @Transactional
    public Page<ClientDestinataireResponse> getAllClients(Pageable pageable) {
        Page<ClientExpediteur> expPage = expediteurRepository.findAll(pageable);
        Page<Destinataire> destPage = destinataireRepository.findAll(pageable);
        List<ClientDestinataireResponse> expList = expPage.getContent().stream()
                .map(clientDestinataireMapper::toClientResponse)
                .toList();

        List<ClientDestinataireResponse> destList = destPage.getContent().stream()
                .map(clientDestinataireMapper::toDestinataireResponse)
                .toList();
        List<ClientDestinataireResponse> combinedContent = new ArrayList<>();
        combinedContent.addAll(expList);
        combinedContent.addAll(destList);
        long totalElements = expPage.getTotalElements() + destPage.getTotalElements();
        return new PageImpl<>(
                combinedContent,
                pageable,
                totalElements
        );

    }

    @Override
    @Transactional
    public Page<ClientDestinataireResponse> searchClients(String keyword, Pageable pageable) {
        Page<ClientExpediteur> expPage = expediteurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        );
        Page<Destinataire> destPage = destinataireRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelephoneContaining(
                keyword, keyword, keyword, keyword, pageable
        );

        List<ClientDestinataireResponse> expList = expPage.getContent().stream()
                .map(clientDestinataireMapper::toClientResponse)
                .collect(Collectors.toList());

        List<ClientDestinataireResponse> destList = destPage.getContent().stream()
                .map(clientDestinataireMapper::toDestinataireResponse)
                .collect(Collectors.toList());

        List<ClientDestinataireResponse> combinedContent = new ArrayList<>();
        combinedContent.addAll(expList);
        combinedContent.addAll(destList);

        long totalElements = expPage.getTotalElements() + destPage.getTotalElements();

        return new PageImpl<>(
                combinedContent,
                pageable,
                totalElements
        );
    }
}