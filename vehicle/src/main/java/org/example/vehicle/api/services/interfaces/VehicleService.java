package org.example.vehicle.api.services.interfaces;

import org.example.vehicle.api.dtos.trip.TripCompleted;
import org.example.vehicle.api.dtos.vehicle.VehicleCreate;
import org.example.vehicle.api.dtos.vehicle.VehicleDetail;
import org.example.vehicle.api.dtos.vehicle.VehicleInfo;
import org.example.vehicle.utils.dtos.PageResponse;

import java.util.List;

public interface VehicleService {

    List<String> getAllLicensePlate();

    List<String> getLicensePlateByType(Long typeId);

    PageResponse<VehicleInfo> getAllVehiclesByCondition(
            String beLongTo,
            Long typeId,
            String status
    );

    PageResponse<VehicleInfo> getAllVehiclesByCondition(
            String beLongTo,
            Long typeId,
            String status,
            Integer pageNo, Integer pageSize
    );

    PageResponse<VehicleInfo> getAllVehicles();

    PageResponse<VehicleInfo> getAllVehicles(Integer pageNo, Integer pageSize);

    PageResponse<VehicleInfo> getAllVehiclesByBelongTo(String belongTo);

    PageResponse<VehicleInfo> getAllVehiclesByBelongTo(Integer pageNo, Integer pageSize, String belongTo);

    PageResponse<VehicleInfo> getAllVehiclesByBelongToAndType(String belongTo, Long typeId);

    PageResponse<VehicleInfo> getAllVehiclesByBelongToAndType(Integer pageNo, Integer pageSize, String belongTo, Long typeId);

    PageResponse<VehicleInfo> getAllVehiclesByBelongToAndTypeAndStatus(String belongTo, Long typeId, String status);

    PageResponse<VehicleInfo> getAllVehiclesByBelongToAndTypeAndStatus(
            Integer pageNo, Integer pageSize,
            String belongTo, Long typeId, String status
    );


    VehicleDetail getVehicleDetailById(Long id);

    VehicleDetail createVehicle(VehicleCreate vehicleCreate);

    VehicleDetail toggleVehicle(Long id);

    void handleTripCompletedEvent(TripCompleted tripCompleted);

    void handleTripDepartureEvent(Long vehicleId);


}
