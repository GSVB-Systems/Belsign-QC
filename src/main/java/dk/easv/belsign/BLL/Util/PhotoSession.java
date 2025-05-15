package dk.easv.belsign.BLL.Util;

import dk.easv.belsign.BE.Photos;

public class PhotoSession {

    private static PhotoSession instance;

    private Photos currentPhoto;

    // Private constructor to enforce singleton pattern
    private PhotoSession() {}

    public static PhotoSession getInstance() {
        if (instance == null) {
            instance = new PhotoSession();
        }
        return instance;
    }

    public static void setCurrentPhoto(Photos photo) {
        getInstance().currentPhoto = photo;
    }

    public static Photos getCurrentPhoto() {
        return getInstance().currentPhoto;
    }

    public static void clear() {
        getInstance().currentPhoto = null;
    }
}