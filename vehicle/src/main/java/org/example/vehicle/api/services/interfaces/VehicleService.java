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
            Long typeId,
            String status,
            String currentLocation
    );

    PageResponse<VehicleInfo> getAllVehiclesByCondition(
            Long typeId,
            String status,
            String currentLocation,
            Integer pageNo, Integer pageSize
    );

    PageResponse<VehicleInfo> getAllVehicles();

    PageResponse<VehicleInfo> getAllVehicles(Integer pageNo, Integer pageSize);

    PageResponse<VehicleInfo> getAllVehiclesByType(Long typeId);

    PageResponse<VehicleInfo> getAllVehiclesByType(Long typeId, Integer pageNo, Integer pageSize);

    PageResponse<VehicleInfo> getAllVehiclesByTypeAndStatus(
            Long typeId, String status
    );

    PageResponse<VehicleInfo> getAllVehiclesByTypeAndStatus(
            Long typeId, String status, Integer pageNo, Integer pageSize
    );

    PageResponse<VehicleInfo> getAllVehiclesByTypeAndStatusAndCurrentLocation(
            Long typeId, String status, String currentLocation
    );

    PageResponse<VehicleInfo> getAllVehiclesByTypeAndStatusAndCurrentLocation(
            Long typeId, String status, String currentLocation, Integer pageNo, Integer pageSize
    );

    VehicleDetail getVehicleDetailById(Long id);

    VehicleDetail createVehicle(VehicleCreate vehicleCreate);

    VehicleDetail toggleVehicle(Long id);

    void handleTripCompletedEvent(TripCompleted tripCompleted);

    void handleTripDepartureEvent(Long vehicleId);


}
