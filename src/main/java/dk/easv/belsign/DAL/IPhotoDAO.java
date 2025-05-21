package dk.easv.belsign.DAL;

import dk.easv.belsign.BE.Photos;

import java.sql.SQLException;

public interface IPhotoDAO<T> extends ICrudRepo<T>{

    void updatePhotoComment(Photos photo) throws SQLException;

}
