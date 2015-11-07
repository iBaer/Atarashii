package net.somethingdreadful.MAL.api.MALModels;

import com.google.gson.annotations.SerializedName;

import net.somethingdreadful.MAL.account.AccountService;
import net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime;
import net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga;
import net.somethingdreadful.MAL.api.BaseModels.Profile;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class History implements Serializable {

    /**
     * The anime/manga details.
     */
    @Getter
    @Setter
    private Series item;

    /**
     * The type of record.
     * <p/>
     * It indicates what kind of record the item is.
     * Example: "anime" or "manga"
     */
    @Getter
    @Setter
    private String type;

    /**
     * The time when the anime or manga was updated
     */
    @Getter
    @Setter
    @SerializedName("time_updated")
    private String timeUpdated;

    class Series {

        /**
         * The record of the ID.
         */
        @Getter
        @Setter
        private int id;

        /**
         * Total number of chapters of the manga.
         * <p/>
         * This value is the number of chapters of the manga, or null if unknown.
         */
        @Getter
        @Setter
        private int chapters;

        /**
         * Total number of episodes of the anime.
         * <p/>
         * This value is the number of episodes of the anime, or null if unknown.
         */
        @Getter
        @Setter
        private int episodes;

        /**
         * Title of a record.
         **/
        @Getter
        @Setter
        private String title;

        /**
         * The time when the record has been updated.
         **/
        @Getter
        @Setter
        @SerializedName("time_updated")
        private String timeUpdated;
    }

    public net.somethingdreadful.MAL.api.BaseModels.History createBaseModel() {
        net.somethingdreadful.MAL.api.BaseModels.History model = new net.somethingdreadful.MAL.api.BaseModels.History();
        if (type.equals("anime")) {
            model.setAnime(new Anime());
            model.getAnime().setId(getItem().getId());
            model.setValue(String.valueOf(getItem().getEpisodes()));
            model.setStatus("watched episode");
            model.getAnime().setTitle(getItem().getTitle());
            model.getAnime().setImageUrl("http://cdn.myanimelist.net/images/na_series.gif");
        } else {
            model.setManga(new Manga());
            model.getManga().setId(getItem().getId());
            model.setValue(String.valueOf(getItem().getChapters()));
            model.setStatus("read chapter");
            model.getManga().setTitle(getItem().getTitle());
            model.getManga().setImageUrl("http://cdn.myanimelist.net/images/na_series.gif");
        }
        model.setCreatedAt(getTimeUpdated());
        model.setActivityType("list");

        // set User
        ArrayList<Profile> users = new ArrayList<>();
        Profile user = new Profile();
        user.setUsername(AccountService.getUsername());
        users.add(user);
        model.setUsers(users);
        return model;
    }

    public static ArrayList<net.somethingdreadful.MAL.api.BaseModels.History> convertBaseHistoryList(ArrayList<History> histories) {
        ArrayList<net.somethingdreadful.MAL.api.BaseModels.History> historyArrayList = new ArrayList<>();
        for (History history : histories) {
            historyArrayList.add(history.createBaseModel());
        }
        return historyArrayList;
    }
}