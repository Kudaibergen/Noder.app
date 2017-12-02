package com.ka.noder.api;

import com.ka.noder.model.Note;
import com.ka.noder.model.StatusResponse;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NoderApi {

    @GET("notes")
    Call<List<Note>> getNotes();

    @POST("notes")
    Call<StatusResponse> addNote(@Body Note note);

    @PUT("notes")
    Call<StatusResponse> updateNote(@Body Note note);

    @DELETE("notes/{note-uuid}")
    Call<Void> deleteNote(@Path("note-uuid") UUID uuid);

    @POST("notes/del")
    Call<Void> deleteNotes(@Body List<UUID> ids);
}