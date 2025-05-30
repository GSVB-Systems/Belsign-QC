package dk.easv.belsign.BE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PhotosTemplate {
    SMALL(Arrays.asList("Front", "Back", "Left", "Right", "Top")),
    MEDIUM(Arrays.asList("Front", "Front 2", "Back", "Back 2", "Left", "Left 2", "Right", "Right 2", "Top", "Top 2")),
    LARGE(Arrays.asList("Front", "Front 2", "Front 3", "Back", "Back 2", "Back 3", "Left", "Left 2", "Left 3", "Right", "Right 2", "Right 3", "Top", "Top 2", "Top 3"));

    private final List<String> photoNames;

    PhotosTemplate(List<String> photoNames) {
        this.photoNames = photoNames;
    }

    /**
     * Creates a list of Photos objects based on the template size
     *
     * @return List of Photos objects
     */
    public List<Photos> createPhotos(int productId) {
        List<Photos> photosList = new ArrayList<>();

        for (String name : photoNames) {
            String photoName = name;
            String photoPath = "images/addPhoto.png";
            String photoStatus = null;
            String photoComment = null;

            // Using 0 for photoId (will be assigned when saved to database)
            Photos photo = new Photos(0, photoName, photoPath, photoStatus, productId, photoComment);
            photosList.add(photo);
        }

        return photosList;
    }


}