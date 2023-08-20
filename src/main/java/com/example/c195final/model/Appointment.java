package com.example.c195final.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {

        private int appointmentId;
        private String title;
        private String description;
        private String location;
        private String contactId;
        private String type;
        private LocalDateTime start;
        private LocalDateTime end;
        private String customerId;
        private int userId;

        // Constructor
        public Appointment(int appointmentId, String title, String description, String location,
                           String contactId, String type, LocalDateTime start, LocalDateTime end,
                           String customerId, int userId) {
            this.appointmentId = appointmentId;
            this.title = title;
            this.description = description;
            this.location = location;
            this.contactId = contactId;
            this.type = type;
            this.start = start;
            this.end = end;
            this.customerId = customerId;
            this.userId = userId;
        }

    public Appointment(int appointmentId, String title, String description, String location, String contactId, String type, String start, String end, String customerId, int userId) {

        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactId = contactId;
        this.type = type;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.start = LocalDateTime.parse(start, formatter);
        this.end = LocalDateTime.parse(end, formatter);
        this.customerId = customerId;
        this.userId = userId;
    }

    // Getters and Setters
        public int getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(int appointmentId) {
            this.appointmentId = appointmentId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getContactId() {
            return contactId;
        }

        public void setContactId(String contactId) {
            this.contactId = contactId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
}
