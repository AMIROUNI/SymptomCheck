package com.SymptomCheck.userservice.repositories;

import com.SymptomCheck.userservice.models.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepository extends JpaRepository<UserData,String> {




}
