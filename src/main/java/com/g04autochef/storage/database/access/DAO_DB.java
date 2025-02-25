package com.g04autochef.storage.database.access;

import com.g04autochef.data_access.DAO;
import com.g04autochef.data_access.exceptions.accessExceptions.AccessException;
import com.g04autochef.data_access.exceptions.accessExceptions.ConnectionException;
import com.g04autochef.data_access.exceptions.accessExceptions.DeleteNotPresentException;
import com.g04autochef.data_access.filters.Filter;
import com.g04autochef.data_access.filters.FilterField;
import com.g04autochef.model.storableDAO.StorableDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Object needed to interact with the database.
 * Careful !! All ModelObj should be final ! Polymorphism doesn't work on the storage backend !!
 * @param <Type>
 */
public abstract class DAO_DB<Type extends StorableDAO> extends DAO<Type> {
    protected final Connection conn ;

    /**
     * {@return The query needed to perform a delete on an object <Type>}
     */
    protected  String getStringDelete() {return "";} // statement parameters should have the same order as in ObjMod.getUniqueID !!! (otherwise write methode to order them...)
    /**
     * {@return The query needed to perform a select on an object <Type>}
     */
    protected  String getStringSelect() {return "";}
    /**
     * {@return Part of the select query indicating how the result should be ordered}
     */
    protected  String getStringSelectGroupBy() {return "";}
    /**
     * {@return The query needed to get all the element of <Type> in database}
     */
    protected  String getStringSelectAll() {return "";}

    /**
     * {@return The query needed to get the ID (primary key) of an object <Type>}
     */
    protected abstract String getStringGetID();

    /**
     * {@return The name of the column containing the ID (primary key) of an object <Type>}
     */
    protected abstract String getIDColumnName();

    /**
     * A minimal filter to uniquely select "obj" from the database
     * @param obj
     * @return a minimal filter to uniquely select "obj" from the database
     */
    protected abstract  Filter<Type> makeBasicFilter(final Type obj);

    /**
     * Insert used internally to perform some update operation by saving select obj from the db,
     * saving his ID, modifying it, and inserting it with the same ID instead of letting the database
     * give it his ID as it is done for a fresh insert
     */
    protected abstract void insert(final Type obj,Integer id) throws AccessException;

    public final void insert(final Type obj) throws AccessException {
        insert(obj,null);
    }

    protected final boolean isPresent(final Type obj){
        Vector<Type> res = new Vector<>();
        try{res= select(makeBasicFilter(obj));}
        catch (Exception e){e.printStackTrace();}
        return res.size()!=0;
    }


    /**
     * Transform a db query result in an object usable by the controller
     * @return Object of <Type>
     */
    abstract Type buildModelObj (final ResultSet rs) throws SQLException;

    // the name of the field is equal to the column name in the database, nothing to do
    @Override
    protected final String translateFieldName(final FilterField<Type> field) {return field.getFieldName();}

    protected final PreparedStatement getPreparedStatement(String request) throws ConnectionException {

        try {

            return conn.prepareStatement(request);}
        catch (Exception e) {throw new ConnectionException(e);}
    }

    protected DAO_DB() throws ConnectionException {
        try {conn = DBManager.getInstance().getDBConnection();}
        catch (SQLException e) {throw new ConnectionException(e);}
    }

    protected DAO_DB(Connection conn) {this.conn= conn;}

    @Override
    public final void delete(final Type obj) throws AccessException {
       if (!isPresent(obj)) throw new DeleteNotPresentException();
       try (PreparedStatement stmt = getPreparedStatement(getStringDelete())) {
           ArrayList<String> id = obj.getUniqueID();
           for(int i=1; i <= id.size(); ++i)
               stmt.setString(i, id.get(i-1));
           stmt.executeUpdate();
       } catch (SQLException e) {throw new AccessException(e);}
   }


    @Override
    public final Vector<Type> selectAll() throws AccessException {
        Vector<Type> res = new Vector<>();
        try (PreparedStatement stmt = getPreparedStatement(getStringSelectAll())) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                res.add(buildModelObj(rs));
        }
        catch (SQLException e) {throw new AccessException(e);}
        return res;
    }

    // Can't be used if no filter defined OK
    @Override
    public final Vector<Type> select(final Filter<Type> filter) throws AccessException {
        StringBuilder request = new StringBuilder(getStringSelect() + " ");
        Vector<Type> res = new Vector<>();

        for (FilterField<Type> f : filter.getFields())
                request.append(translateFieldName(f)).append("=\'").append(f.getFieldValue()).append("\' AND ");

        request.append("1=1 "); // UwU ??
        request.append(getStringSelectGroupBy());
        try (PreparedStatement stmt = getPreparedStatement(request.toString())) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                res.add(buildModelObj(rs));
        }
        catch (SQLException e) {throw new AccessException(e);}

    return res;
    }

    /**
     * @param obj
     * {@return The ID (primary key) of "obj"}
     */
    final int getID(Type obj) {
        String request = getStringGetID();
        int id = 0;
        try (PreparedStatement stmt = conn.prepareStatement(request)){
            ArrayList<String> uniqueIDs = obj.getUniqueID();
            for (int i = 1; i <= uniqueIDs.size(); ++i) {
                stmt.setString(i, uniqueIDs.get(i - 1));
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) id = rs.getInt(getIDColumnName());
        } catch (SQLException ignored) {}
        return id;
    }
}
