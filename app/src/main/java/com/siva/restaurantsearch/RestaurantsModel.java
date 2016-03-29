package com.siva.restaurantsearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siva on 3/15/2016.
 */
class Hours
{
    public List<List<String>> monday;
    public List<List<String>> tuesday;
    public List<List<String>> wednesday;
    public List<List<String>> thursday;
    public List<List<String>> friday;
    public List<List<String>> saturday;
    public List<List<String>> sunday;
}

class Datum
{
    public boolean accessible_wheelchair;
    public String address;
    public String attire;
    public List<Integer> category_ids;
    public List<List<String>> category_labels;
    public String country;
    public List<String> cuisine;
    public String factual_id;
    public Hours hours;
    public String hours_display;
    public double latitude;
    public String locality;
    public double longitude;
    public boolean meal_cater;
    public boolean meal_deliver;
    public boolean meal_dinner;
    public boolean meal_lunch;
    public String name;
    public boolean open_24hrs;
    public boolean parking;
    public boolean parking_free;
    public boolean parking_lot;
    public boolean payment_cashonly;
    public String postcode;
    public int price;
    public double rating;
    public String region;
    public boolean reservations;
    public String tel;
    public String website;
}

class Response
{
    public ArrayList<Datum> data;
    public int included_rows;
}

public class RestaurantsModel
{
    public int version;
    public String status;
    public Response response;
}
