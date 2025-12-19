package io.github.mahjoubech.smartlogiv2.service.impl;

import io.github.mahjoubech.smartlogiv2.config.AppConfig;
import io.github.mahjoubech.smartlogiv2.config.JwtService;
import io.github.mahjoubech.smartlogiv2.dto.request.LoginRequest;
import io.github.mahjoubech.smartlogiv2.dto.request.RegisterRequest;
import io.github.mahjoubech.smartlogiv2.dto.response.AuthResponse;
import io.github.mahjoubech.smartlogiv2.exception.ConflictStateException;
import io.github.mahjoubech.smartlogiv2.exception.ResourceNotFoundException;
import io.github.mahjoubech.smartlogiv2.exception.ValidationException;
import io.github.mahjoubech.smartlogiv2.mapper.ClientDestinataireMapper;
import io.github.mahjoubech.smartlogiv2.mapper.GestionnerMapper;
import io.github.mahjoubech.smartlogiv2.mapper.LivreurMapper;
import io.github.mahjoubech.smartlogiv2.mapper.UserMapper;
import io.github.mahjoubech.smartlogiv2.model.entity.*;
import io.github.mahjoubech.smartlogiv2.model.enums.Roles;
import io.github.mahjoubech.smartlogiv2.repository.*;
import io.github.mahjoubech.smartlogiv2.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ClientDestinataireMapper clientDestinataireMapper;
    private final GestionnerMapper gestionnerMapper;
    private final PasswordEncoder passwordEncoder;
    private final ClientExpediteurRepository clientExpediteurRepository;
    private  final GestionnerRepository gestionnerRepository;
    private final LivreurRepository livreurRepository;
    private  final LivreurMapper livreurMapper;
    private  final ZoneRepository zoneRepository;
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictStateException("User already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }

        User savedUser;

        if(request.getRole().equals(Roles.CLIENT)){
            ClientExpediteur user = clientDestinataireMapper.toClientExpediteur(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            savedUser = clientExpediteurRepository.save(user); // save f ClientExpediteur repo
        } else if (request.getRole().equals(Roles.MANAGER)) {
            Gestionner gestionner = gestionnerMapper.toGestionner(request);
            gestionner.setPassword(passwordEncoder.encode(request.getPassword()));
            savedUser = gestionnerRepository.save(gestionner); // save f Gestionner repo
        } else if(request.getRole().equals(Roles.LIVREUR)) {
            Livreur livreur = livreurMapper.toLivreur(request);
            if (request.getZoneAssigned() != null) {
                Zone zone = zoneRepository.findById(request.getZoneAssigned())
                        .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));
                livreur.setZoneAssigned(zone);
            }
            livreur.setPassword(passwordEncoder.encode(request.getPassword()));
            savedUser = livreurRepository.save(livreur);
        } else {
            throw new ValidationException("Role not supported");
        }
      var jwtToken = jwtService.generateToken(savedUser);
        AuthResponse authResponse = userMapper.toAuthResponse(savedUser);
        authResponse.setToken(jwtToken);
        return authResponse;
    }


    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        var jwtToken = jwtService.generateToken(userOptional.get());
        AuthResponse authResponse = userMapper.toAuthResponse(userOptional.get());
        authResponse.setToken(jwtToken);
        return authResponse;
    }

    @Override
    public void logout() {

    }


}
