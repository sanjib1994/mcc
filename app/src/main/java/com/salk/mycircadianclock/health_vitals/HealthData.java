package com.salk.mycircadianclock.health_vitals;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class HealthData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String vitalOrlab = "";
    String value = "";
    String unit;
    String healthData_type = "";
    String systolic_value = "";
    String diastolic_value = "";
    String chol_total_value = "";
    String ldl_value = "";
    String hdl_value = "";
    Long timestamp = 0L,actual_log_time =0L;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVitalOrlab() {
        return vitalOrlab;
    }

    public void setVitalOrlab(String vitalOrlab) {
        this.vitalOrlab = vitalOrlab;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getHealthData_type() {
        return healthData_type;
    }

    public void setHealthData_type(String healthData_type) {
        this.healthData_type = healthData_type;
    }

    public String getSystolic_value() {
        return systolic_value;
    }

    public void setSystolic_value(String systolic_value) {
        this.systolic_value = systolic_value;
    }

    public String getDiastolic_value() {
        return diastolic_value;
    }

    public void setDiastolic_value(String diastolic_value) {
        this.diastolic_value = diastolic_value;
    }

    public String getChol_total_value() {
        return chol_total_value;
    }

    public void setChol_total_value(String chol_total_value) {
        this.chol_total_value = chol_total_value;
    }

    public String getLdl_value() {
        return ldl_value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setLdl_value(String ldl_value) {
        this.ldl_value = ldl_value;
    }

    public String getHdl_value() {
        return hdl_value;
    }

    public Long getActual_log_time() {
        return actual_log_time;
    }

    public void setActual_log_time(Long actual_log_time) {
        this.actual_log_time = actual_log_time;
    }

    public void setHdl_value(String hdl_value) {
        this.hdl_value = hdl_value;
    }
}
