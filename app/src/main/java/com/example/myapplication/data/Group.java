
package com.example.myapplication.data;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.persistence.*;
import com.backendless.geo.GeoPoint;

import java.util.List;
import java.util.Date;

public class Group
{
  private Date created;
  private String type;
  private Date updated;
  private String ownerId;
  private String name;
  private String objectId;
  public Date getCreated()
  {
    return created;
  }

  public String getType()
  {
    return type;
  }

  public void setType( String type )
  {
    this.type = type;
  }

  public Date getUpdated()
  {
    return updated;
  }

  public String getOwnerId()
  {
    return ownerId;
  }

  public String getName()
  {
    return name;
  }

  public void setName( String name )
  {
    this.name = name;
  }

  public String getObjectId()
  {
    return objectId;
  }

                                                    
  public Group save()
  {
    return Backendless.Data.of( Group.class ).save( this );
  }

  public void saveAsync( AsyncCallback<Group> callback )
  {
    Backendless.Data.of( Group.class ).save( this, callback );
  }

  public Long remove()
  {
    return Backendless.Data.of( Group.class ).remove( this );
  }

  public void removeAsync( AsyncCallback<Long> callback )
  {
    Backendless.Data.of( Group.class ).remove( this, callback );
  }

  public static Group findById( String id )
  {
    return Backendless.Data.of( Group.class ).findById( id );
  }

  public static void findByIdAsync( String id, AsyncCallback<Group> callback )
  {
    Backendless.Data.of( Group.class ).findById( id, callback );
  }

  public static Group findFirst()
  {
    return Backendless.Data.of( Group.class ).findFirst();
  }

  public static void findFirstAsync( AsyncCallback<Group> callback )
  {
    Backendless.Data.of( Group.class ).findFirst( callback );
  }

  public static Group findLast()
  {
    return Backendless.Data.of( Group.class ).findLast();
  }

  public static void findLastAsync( AsyncCallback<Group> callback )
  {
    Backendless.Data.of( Group.class ).findLast( callback );
  }

  public static List<Group> find( DataQueryBuilder queryBuilder )
  {
    return Backendless.Data.of( Group.class ).find( queryBuilder );
  }

  public static void findAsync( DataQueryBuilder queryBuilder, AsyncCallback<List<Group>> callback )
  {
    Backendless.Data.of( Group.class ).find( queryBuilder, callback );
  }
}