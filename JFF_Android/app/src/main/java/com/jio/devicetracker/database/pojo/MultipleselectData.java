/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.

 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.

 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/

package com.jio.devicetracker.database.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Pojo implementation for multiple device selection .
 */
public class MultipleselectData {
    private String phone;
    private String lat;
    private String lng;
    private String name;
    private String imeiNumber;

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public static class SearchEventData implements Serializable {
        @SerializedName("device")
        private Device device;
        @SerializedName("from")
        private Long from;
        @SerializedName("to")
        private Long to;
        @SerializedName("flags")
        private Flags flags;

        public Device getDevice() {
            return device;
        }

        public void setDevice(Device device) {
            this.device = device;
        }

        public Long getFrom() {
            return from;
        }

        public void setFrom(Long from) {
            this.from = from;
        }

        public Long getTo() {
            return to;
        }

        public void setTo(Long to) {
            this.to = to;
        }

        public Flags getFlags() {
            return flags;
        }

        public void setFlags(Flags flags) {
            this.flags = flags;
        }

        public class Device implements Serializable {
            @SerializedName("_id")
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public class Flags implements Serializable {
            @SerializedName("isPopulateGeofence")
            private boolean isPopulateGeofence;
            @SerializedName("isPopulateRoute")
            private boolean isPopulateRoute;

            public boolean isPopulateGeofence() {
                return isPopulateGeofence;
            }

            public void setPopulateGeofence(boolean populateGeofence) {
                isPopulateGeofence = populateGeofence;
            }

            public boolean isPopulateRoute() {
                return isPopulateRoute;
            }

            public void setPopulateRoute(boolean populateRoute) {
                isPopulateRoute = populateRoute;
            }
        }
    }
}
