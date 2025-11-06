package com.SymptomCheck.userservice.repositories;

import com.SymptomCheck.userservice.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData,String> {
}
