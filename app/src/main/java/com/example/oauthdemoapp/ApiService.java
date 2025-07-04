package com.example.oauthdemoapp;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("notes")
    Call<List<Note>> getAllNotes(@Header("Authorization") String token);

    @GET("notes/{id}")
    Call<Note> getNoteById(@Header("Authorization") String token, @Path("id") int id);

    @POST("notes")
    Call<ResponseBody> createNote(@Header("Authorization") String token, @Body RequestBody note);

    @PUT("notes/{id}")
    Call<ResponseBody> updateNote(@Header("Authorization") String token, @Path("id") int id, @Body RequestBody note);

    @DELETE("notes/{id}")
    Call<ResponseBody> deleteNote(@Header("Authorization") String token, @Path("id") int id);
} 