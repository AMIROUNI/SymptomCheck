package com.symptomcheck.appointmentservice.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class AppointmentDto {

  LocalDateTime dateTime;
  UUID patientId;
  UUID doctorId;
  String  description;



}
