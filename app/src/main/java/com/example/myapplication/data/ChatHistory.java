package com.example.myapplication.data;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.*;
import com.backendless.geo.GeoPoint;

import java.util.List;
import java.util.Date;

public class ChatHistory
{
    private String ownerId;
    private Date created;
    private String groupId;
    private String messageData;
    private Date updated;
    private String objectId;
    private String publisher;
    public String getOwnerId()
    {
        return ownerId;
    }

    public Date getCreated()
    {
        return created;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getMessageData()
    {
        return messageData;
    }

    public void setMessageData( String messageData )
    {
        this.messageData = messageData;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher( String publisher )
    {
        this.publisher = publisher;
    }


    public ChatHistory save()
    {
        return Backendless.Data.of( ChatHistory.class ).save( this );
    }

    public void saveAsync( AsyncCallback<ChatHistory> callback )
    {
        Backendless.Data.of( ChatHistory.class ).save( this, callback );
    }

    public Long remove()
    {
        return Backendless.Data.of( ChatHistory.class ).remove( this );
    }

    public void removeAsync( AsyncCallback<Long> callback )
    {
        Backendless.Data.of( ChatHistory.class ).remove( this, callback );
    }

    public static ChatHistory findById( String id )
    {
        return Backendless.Data.of( ChatHistory.class ).findById( id );
    }

    public static void findByIdAsync( String id, AsyncCallback<ChatHistory> callback )
    {
        Backendless.Data.of( ChatHistory.class ).findById( id, callback );
    }

    public static ChatHistory findFirst()
    {
        return Backendless.Data.of( ChatHistory.class ).findFirst();
    }

    public static void findFirstAsync( AsyncCallback<ChatHistory> callback )
    {
        Backendless.Data.of( ChatHistory.class ).findFirst( callback );
    }

    public static ChatHistory findLast()
    {
        return Backendless.Data.of( ChatHistory.class ).findLast();
    }

    public static void findLastAsync( AsyncCallback<ChatHistory> callback )
    {
        Backendless.Data.of( ChatHistory.class ).findLast( callback );
    }

    public static List<ChatHistory> find( DataQueryBuilder queryBuilder )
    {
        return Backendless.Data.of( ChatHistory.class ).find( queryBuilder );
    }

    public static void findAsync( DataQueryBuilder queryBuilder, AsyncCallback<List<ChatHistory>> callback )
    {
        Backendless.Data.of( ChatHistory.class ).find( queryBuilder, callback );
    }
}
