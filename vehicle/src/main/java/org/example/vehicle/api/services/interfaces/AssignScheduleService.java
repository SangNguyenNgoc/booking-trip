package org.example.vehicle.api.services.interfaces;

import org.example.vehicle.api.dtos.assign.AssignScheduleCreate;
import org.example.vehicle.api.entities.AssignSchedule;

import java.util.List;

public interface AssignScheduleService {

    void creatAssignSchedule(List<AssignScheduleCreate> assignScheduleCreates);
}
