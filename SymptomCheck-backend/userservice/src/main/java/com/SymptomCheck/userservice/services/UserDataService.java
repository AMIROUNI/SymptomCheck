package com.SymptomCheck.userservice.services;


import com.SymptomCheck.userservice.models.UserData;
import com.SymptomCheck.userservice.repositories.UserDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataService {
    private final UserDataRepository userDataRepository;


    public Optional<UserData> getUserDataById(String keycloakUserId) {

        Optional<UserData> result = userDataRepository.findById(keycloakUserId);

        if (result.isPresent()) {
        } else {

            // Debug: Check if any data exists in the table
            long count = userDataRepository.count();

            // Debug: Show all IDs in the database (remove in production!)
            userDataRepository.findAll().forEach(ud ->
                    log.info("   - Existing ID in DB: '{}'", ud.getId())
            );
        }

        return result;
    }

}
