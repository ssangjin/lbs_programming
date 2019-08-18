package com.lbs.programming.lbs_2_5;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomGeofence {
    public static final double PRECISION = 0.1;
    public static final String PROVIDER_GPS = "gps";
    public static final String PROVIDER_WIFI = "wifi";
    public static final String PROVIDER_BLE = "ble";
    public static final String PROVIDER_DUMMY = "dummy";
    private double currentX = Double.MAX_VALUE;
    private double currentY = Double.MAX_VALUE;
    private String lastProvider;
    private List<Fence> fenceList = new ArrayList<>();
    private Status status = Status.Unknown;
    private OnGeofenceTransition listener;

    enum Status {
        Enter,
        Exit,
        Unknown
    }

    public CustomGeofence(OnGeofenceTransition listener) {
        this.listener = listener;
    }

    public void onLocationChanged(String provider, double x, double y, float accuracy) {
        // 마지막 위치와 같은 위치인지 체크
        if (isSamePosition(provider, x, y, accuracy)) {
            return;
        }

        Fence fence = new Fence(x, y, accuracy);

        Status newStatus = Status.Unknown;

        for (Fence item : fenceList) {
            if (item.equals(fence)) {
                if (status != Status.Enter) {
                    newStatus = Status.Enter;
                    listener.onTransition(Status.Enter);
                    break;
                }
            }
        }

        if (newStatus != Status.Enter && status == Status.Enter) {
            listener.onTransition(Status.Exit);
        }

        status = newStatus;

        lastProvider = provider;
        currentX = x;
        currentY = y;
    }

    private boolean isSamePosition(String provider, double x, double y, float accuracy) {
        if (lastProvider == null || currentX == Double.MAX_VALUE || currentY == Double.MAX_VALUE || accuracy > 100) {
            return false;
        }

        if (TextUtils.equals(provider, PROVIDER_DUMMY)) {
            return true;
        }

        if (equals(currentX, x, PRECISION)
            && equals(currentY, y, PRECISION)) {
            return true;
        }

        return false;
    }

    private boolean equals(double l, double r, double precision ) {
        return (Math.abs(l - r) < precision);
    }

    public void addFence(double x, double y, double diameter) {
        fenceList.add(new Fence(x, y, diameter));
    }

    interface OnGeofenceTransition {
        void onTransition(Status geofenceTransition);
    }

    public static class Fence {
        double x;
        double y;
        double diameter;

        public Fence(double x, double y, double diameter) {
            this.x = x;
            this.y = y;
            this.diameter = diameter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Fence) {
                double distance = Math.pow((this.x - ((Fence) obj).x) * (this.x - ((Fence) obj).x) + (this.y - ((Fence) obj).y) * (this.y - ((Fence) obj).y), 0.5);
                if (distance < (this.diameter + ((Fence) obj).diameter) / 2) {
                    return true;
                } else {
                    return false;
                }
            }
            return super.equals(obj);
        }
    }
}
