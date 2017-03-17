package com.mstudenets.studenetsbananaapp.model;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "markers")
public class MyMapMarker
{
    @DatabaseField(columnName = "id", generatedId = true, dataType = DataType.INTEGER)
    private int id;

    @DatabaseField(columnName = "latitude", dataType = DataType.DOUBLE, canBeNull = false)
    private double latitude;

    @DatabaseField(columnName = "longitude", dataType = DataType.DOUBLE, canBeNull = false)
    private double longitude;

    @DatabaseField(columnName = "title", dataType = DataType.STRING)
    private String title;

    @DatabaseField(columnName = "description", dataType = DataType.STRING)
    private String description;

    private BitmapDescriptorFactory color;

    public MyMapMarker() {
    }

    public MyMapMarker(double latitude, double longitude,
                       String title, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
}
