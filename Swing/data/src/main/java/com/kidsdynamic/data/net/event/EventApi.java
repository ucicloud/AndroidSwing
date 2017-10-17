package com.kidsdynamic.data.net.event;

import com.kidsdynamic.data.net.event.model.EventWithTodo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/10/17.
 */

public interface EventApi {

//    200	Retrieve successfully
//    500	Internal error. Please send me the error. I will fix it
    @GET("/v1/event/retrieveAllEventsWithTodo")
    Call<List<EventWithTodo>> retrieveAllEventsWithTodo();
}
