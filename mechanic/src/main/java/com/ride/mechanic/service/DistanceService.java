package com.ride.mechanic.service;

import com.ride.mechanic.dto.MechanicResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistanceService {

    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Add distance information to mechanic list based on user location
     * @param mechanics List of mechanics
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @return List of mechanics with distance information
     */
    public List<MechanicResponseDTO> addDistanceToMechanics(List<MechanicResponseDTO> mechanics, 
                                                          Double userLatitude, 
                                                          Double userLongitude) {
        if (userLatitude == null || userLongitude == null) {
            return mechanics;
        }

        return mechanics.stream()
                .peek(mechanic -> {
                    // Only use center location - individual location tracking removed
                    if (mechanic.getAssignedCenter() != null) {
                        Double lat = mechanic.getAssignedCenter().getLatitude();
                        Double lng = mechanic.getAssignedCenter().getLongitude();
                        
                        if (lat != null && lng != null) {
                            double distance = calculateDistance(userLatitude, userLongitude, lat, lng);
                            mechanic.setDistance(distance);
                        }
                    }
                    // Mechanics without assigned center will have null distance
                })
                .sorted((m1, m2) -> {
                    if (m1.getDistance() == null) return 1;
                    if (m2.getDistance() == null) return -1;
                    return m1.getDistance().compareTo(m2.getDistance());
                })
                .toList();
    }

    /**
     * Filter mechanics within a specific radius
     * @param mechanics List of mechanics with distance information
     * @param maxDistance Maximum distance in km
     * @return List of mechanics within the specified radius
     */
    public List<MechanicResponseDTO> filterByDistance(List<MechanicResponseDTO> mechanics, double maxDistance) {
        return mechanics.stream()
                .filter(mechanic -> mechanic.getDistance() == null || mechanic.getDistance() <= maxDistance)
                .toList();
    }

    /**
     * Add distance information to mechanic list based on center location
     * @param mechanics List of mechanics
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @return List of mechanics with distance information, sorted by distance
     */
    public List<MechanicResponseDTO> addDistanceToMechanicsWithCenterLocation(List<MechanicResponseDTO> mechanics, 
                                                                              Double userLatitude, 
                                                                              Double userLongitude) {
        if (userLatitude == null || userLongitude == null) {
            return mechanics;
        }

        return mechanics.stream()
                .peek(mechanic -> {
                    // Only use center location - individual location tracking removed
                    if (mechanic.getAssignedCenter() != null) {
                        Double lat = mechanic.getAssignedCenter().getLatitude();
                        Double lng = mechanic.getAssignedCenter().getLongitude();
                        
                        if (lat != null && lng != null) {
                            double distance = calculateDistance(userLatitude, userLongitude, lat, lng);
                            mechanic.setDistance(distance);
                        }
                    }
                    // Mechanics without assigned center will have null distance
                })
                .sorted((m1, m2) -> {
                    if (m1.getDistance() == null) return 1;
                    if (m2.getDistance() == null) return -1;
                    return m1.getDistance().compareTo(m2.getDistance());
                })
                .toList();
    }
}
