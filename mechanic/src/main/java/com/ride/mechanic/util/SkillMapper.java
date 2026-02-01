package com.ride.mechanic.util;

import java.util.HashMap;
import java.util.Map;

public class SkillMapper {

    private static final Map<String, String> SKILL_MAPPING = new HashMap<>();

    static {
        // Map service request problem types to frontend skill options
        SKILL_MAPPING.put("TIRE_CHANGE", "TIRE_SPECIALIST");
        SKILL_MAPPING.put("TIRE", "TIRE_SPECIALIST");
        SKILL_MAPPING.put("FLAT_TIRE", "TIRE_SPECIALIST");
        SKILL_MAPPING.put("PUNCTURE", "TIRE_SPECIALIST");
        SKILL_MAPPING.put("TIRE_REPAIR", "TIRE_SPECIALIST");
        
        SKILL_MAPPING.put("TOWING", "TOWING");
        SKILL_MAPPING.put("TOW", "TOWING");
        SKILL_MAPPING.put("TOW_TRUCK", "TOWING");
        SKILL_MAPPING.put("TRAILER", "TOWING");
        
        SKILL_MAPPING.put("BATTERY_REPLACEMENT", "BATTERY_EXPERT");
        SKILL_MAPPING.put("BATTERY", "BATTERY_EXPERT");
        SKILL_MAPPING.put("BATTERY_DEAD", "BATTERY_EXPERT");
        SKILL_MAPPING.put("JUMP_START", "BATTERY_EXPERT");
        SKILL_MAPPING.put("ELECTRICAL_BATTERY", "BATTERY_EXPERT");
        
        SKILL_MAPPING.put("LOCKOUT", "LOCKSMITH");
        SKILL_MAPPING.put("LOCKSMITH", "LOCKSMITH");
        SKILL_MAPPING.put("KEYS_LOCKED", "LOCKSMITH");
        SKILL_MAPPING.put("CAR_LOCKED", "LOCKSMITH");
        SKILL_MAPPING.put("KEY_REPLACEMENT", "LOCKSMITH");
        
        // General mechanic for all other issues
        SKILL_MAPPING.put("ENGINE_REPAIR", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("ENGINE", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("BRAKE_REPAIR", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("BRAKES", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("OIL_CHANGE", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("MAINTENANCE", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("SERVICE", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("TRANSMISSION", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("GEAR", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("AC_REPAIR", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("AIR_CONDITIONING", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("COOLING", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("OVERHEATING", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("WIRING", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("ELECTRICAL", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("EXHAUST", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("SUSPENSION", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("CLUTCH", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("STARTER", "GENERAL_MECHANIC");
        SKILL_MAPPING.put("ALTERNATOR", "GENERAL_MECHANIC");
    }

    /**
     * Maps a service request problem type to the corresponding mechanic skill type
     * @param problemType The problem type from service request
     * @return The corresponding mechanic skill type
     */
    public static String mapProblemTypeToSkill(String problemType) {
        if (problemType == null) {
            return "GENERAL_MECHANIC"; // Default fallback
        }
        
        String normalizedProblemType = problemType.toUpperCase().replace(" ", "_");
        return SKILL_MAPPING.getOrDefault(normalizedProblemType, "GENERAL_MECHANIC");
    }

    /**
     * Checks if a mechanic's skill matches the required problem type
     * @param mechanicSkill The mechanic's skill type
     * @param problemType The service request problem type
     * @return true if the mechanic can handle the problem type
     */
    public static boolean isSkillMatch(String mechanicSkill, String problemType) {
        if (mechanicSkill == null || problemType == null) {
            return false;
        }
        
        String requiredSkill = mapProblemTypeToSkill(problemType);
        return mechanicSkill.equalsIgnoreCase(requiredSkill);
    }

    /**
     * Get all available skill types for frontend dropdown
     * @return Array of all valid skill types
     */
    public static String[] getAllSkillTypes() {
        return new String[]{
            "GENERAL_MECHANIC",
            "TOWING", 
            "TIRE_SPECIALIST",
            "BATTERY_EXPERT",
            "LOCKSMITH"
        };
    }

    /**
     * Get all available skill mappings for debugging/admin purposes
     * @return Map of all skill mappings
     */
    public static Map<String, String> getAllMappings() {
        return new HashMap<>(SKILL_MAPPING);
    }
}
