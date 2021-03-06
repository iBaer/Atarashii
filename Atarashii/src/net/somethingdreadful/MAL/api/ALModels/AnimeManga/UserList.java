package net.somethingdreadful.MAL.api.ALModels.AnimeManga;

import com.google.gson.annotations.SerializedName;

import net.somethingdreadful.MAL.PrefManager;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class UserList implements Serializable {
    @Getter
    @Setter
    private Lists lists;
    @Getter
    @Setter
    private ArrayList<String> custom_list_anime;
    @Getter
    @Setter
    private ArrayList<String> custom_list_manga;

    @Getter
    @Setter
    private int score_type;
    @Getter
    @Setter
    private int notifications;
    @Getter
    @SerializedName("title_language")
    private String titleLanguage;

    class Lists implements Serializable {
        @Getter
        @Setter
        public ArrayList<ListDetails> completed;
        @Getter
        @Setter
        @SerializedName("plan_to_watch")
        public ArrayList<ListDetails> planToWatch;
        @Getter
        @Setter
        @SerializedName("plan_to_read")
        public ArrayList<ListDetails> planToRead;
        @Getter
        @Setter
        public ArrayList<ListDetails> dropped;
        @Getter
        @Setter
        public ArrayList<ListDetails> watching;
        @Getter
        @Setter
        public ArrayList<ListDetails> reading;
        @Getter
        @Setter
        @SerializedName("on_hold")
        public ArrayList<ListDetails> onHold;
    }

    class ListDetails implements Serializable {
        @Getter
        @Setter
        @SerializedName("record_id")
        private int id;
        @Getter
        @Setter
        @SerializedName("list_status")
        private String listStatus;
        @Getter
        @Setter
        private int priorty;
        @Setter
        private int rewatched;
        @Setter
        private int reread;
        @Getter
        @Setter
        private String notes;
        @Getter
        @Setter
        @SerializedName("updated_time")
        private String updatedtime;
        @Getter
        @Setter
        @SerializedName("added_time")
        private String addedtime;
        @Getter
        @Setter
        @SerializedName("score_raw")
        private int scoreraw;
        @Getter
        @Setter
        @SerializedName("episodes_watched")
        private int episodesWatched;
        @Getter
        @Setter
        @SerializedName("chapters_read")
        private int chaptersRead;
        @Getter
        @Setter
        @SerializedName("volumes_read")
        private int volumesRead;
        @Getter
        @Setter
        private Anime anime;
        @Getter
        @Setter
        private Manga manga;

        public boolean getRewatched() {
            return rewatched > 0;
        }
    }

    public net.somethingdreadful.MAL.api.BaseModels.AnimeManga.UserList createBaseModel() {
        net.somethingdreadful.MAL.api.BaseModels.AnimeManga.UserList model = new net.somethingdreadful.MAL.api.BaseModels.AnimeManga.UserList();
        PrefManager.setTitleNameLang(getTitleLanguage());
        PrefManager.commitChanges();
        model.setAnimeList(combineArrayAnime());
        model.setMangaList(combineArrayManga());
        return model;
    }

    private ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime> combineArrayAnime() {
        ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime> newList = new ArrayList<>();
        newList.addAll(convertAnime(getLists().completed));
        newList.addAll(convertAnime(getLists().planToWatch));
        newList.addAll(convertAnime(getLists().dropped));
        newList.addAll(convertAnime(getLists().watching));
        newList.addAll(convertAnime(getLists().onHold));
        return newList;
    }

    private ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime> convertAnime(ArrayList<ListDetails> list) {
        ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime> newList = new ArrayList<>();
        if (list != null)
            for (ListDetails detail : list) {
                if (detail.getManga() == null) {
                    net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime anime = new net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Anime();
                    Anime AD = detail.getAnime();
                    anime.setId(AD.getId());
                    anime.setTitle(GenericRecord.getLanguageTitle(AD.getTitleRomaji(), AD.getTitleEnglish(), AD.getTitleJapanese()));
                    anime.setType(AD.getType());
                    anime.setImageUrl(AD.getImageUrlLge());
                    anime.setPopularity(AD.getPopularity());
                    anime.setStatus(AD.getAiringStatus());
                    anime.setAverageScore(AD.getAverageScore());
                    anime.setEpisodes(AD.getTotalEpisodes());
                    anime.setWatchedStatus(detail.getListStatus());
                    anime.setScore(detail.getScoreraw());
                    anime.setPriority(detail.getPriorty());
                    anime.setRewatching(detail.getRewatched());
                    anime.setNotes(detail.getNotes());
                    anime.setWatchedEpisodes(detail.getEpisodesWatched());
                    newList.add(anime);
                }
            }
        return newList;
    }

    private ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga> combineArrayManga() {
        ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga> newList = new ArrayList<>();
        newList.addAll(convertManga(getLists().completed));
        newList.addAll(convertManga(getLists().planToRead));
        newList.addAll(convertManga(getLists().dropped));
        newList.addAll(convertManga(getLists().reading));
        newList.addAll(convertManga(getLists().onHold));
        return newList;
    }

    private ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga> convertManga(ArrayList<ListDetails> list) {
        ArrayList<net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga> newList = new ArrayList<>();
        if (list != null)
            for (ListDetails detail : list) {
                if (detail.getAnime() == null) {
                    net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga manga = new net.somethingdreadful.MAL.api.BaseModels.AnimeManga.Manga();
                    Manga MD = detail.getManga();
                    manga.setId(MD.getId());
                    manga.setTitle(GenericRecord.getLanguageTitle(MD.getTitleRomaji(), MD.getTitleEnglish(), MD.getTitleJapanese()));
                    manga.setImageUrl(MD.getImageUrlLge());
                    manga.setType(MD.getType());
                    manga.setReadStatus(detail.getListStatus());
                    manga.setPriority(detail.getPriorty());
                    manga.setChaptersRead(detail.getChaptersRead());
                    manga.setVolumesRead(detail.getVolumesRead());
                    manga.setRereading(detail.getRewatched() ? 1 : 0);
                    manga.setNotes(detail.getNotes());
                    manga.setScore(detail.getScoreraw());
                    newList.add(manga);
                }
            }
        return newList;
    }
}
