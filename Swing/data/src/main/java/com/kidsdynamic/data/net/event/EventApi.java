package com.kidsdynamic.data.net.event;

import com.kidsdynamic.data.net.event.model.EventAddEntity;
import com.kidsdynamic.data.net.event.model.EventEditRep;
import com.kidsdynamic.data.net.event.model.EventInfo;
import com.kidsdynamic.data.net.event.model.EventWithTodo;
import com.kidsdynamic.data.net.event.model.TodoDoneEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/17.
 */

public interface EventApi {
//200	Added successfully
//403	Forbidden. The user doesn't have permission to add event
//400	Bad request. Missing some parameters, or the type is wrong
//500	Internal error. Please send me the error. I will fix it
    @POST("v1/event/add")
    Call<EventEditRep> eventAdd(@Body EventAddEntity eventAddEntity);


//    200	Updated successfully
//    403	Forbidden. The user doesn't have permission to update
//    400	Bad request. Missing some parameters, or the type is wrong
//    500	Internal error. Please send me the error. I will fix it
//    Send the parameter even user does not change it
    @PUT("v1/event/update")
    Call<EventEditRep> eventUpdate(@Body EventInfo eventInfo);

//200	Delete successfully
//403	Forbidden. The user doesn't have permission to delete
//400	Bad request. Missing some parameters, or the type is wrong
//500	Internal error. Please send me the error. I will fix it
    @DELETE("v1/event/delete")
    Call<Object> eventDelete(@Query("eventId") int eventId);

//    200	Retrieve successfully
//    500	Internal error. Please send me the error. I will fix it
    @GET("v1/event/retrieveAllEventsWithTodo")
    Call<List<EventWithTodo>> retrieveAllEventsWithTodo();


//    200	Retrieve successfully
//    403	The user doesn't have access to the kid
//    500	Internal error. Please send me the error. I will fix it
    @GET("v1/event/retrieveAllEventsByKid")
    Call<List<EventWithTodo>> retrieveAllEventsByKid(@Query("kidId")int kidId);

//    200	updated successfully
//    400	Bad request. Missing some parameters, or the type is wrong
//    500	Internal error. Please send me the error. I will fix it
    @PUT("v1/event/todo/done")
    Call<List<Object>> todoItemDone(@Body TodoDoneEntity todoDoneEntity);
}
