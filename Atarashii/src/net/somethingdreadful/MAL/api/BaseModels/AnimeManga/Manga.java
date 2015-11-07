package net.somethingdreadful.MAL.api.BaseModels.AnimeManga;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.somethingdreadful.MAL.MALManager;
import net.somethingdreadful.MAL.api.MALApi;
import net.somethingdreadful.MAL.api.MALModels.RecordStub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Manga extends GenericRecord implements Serializable {

    /**
     * Total number of chapters of the manga.
     * <p/>
     * This value is the number of chapters of the anime, or null if unknown.
     */
    @Setter
    @Getter
    private int chapters;

    /**
     * Total number of volumes of the manga.
     * <p/>
     * This value is the number of volumes of the manga, or null if unknown.
     */
    @Setter
    @Getter
    private int volumes;

    /**
     * A list of anime adaptations of this manga (or conversely, anime from which this manga is adapted)
     */
    @Setter
    @Getter
    @SerializedName("anime_adaptations")
    private ArrayList<RecordStub> animeAdaptations;

    /**
     * A list of related manga
     */
    @Setter
    @Getter
    @SerializedName("related_manga")
    private ArrayList<RecordStub> relatedManga;

    /**
     * User's read status of the manga
     * <p/>
     * This is a string that is one of: reading, completed, on-hold, dropped, plan to read
     */
    @Getter
    @SerializedName("read_status")
    private String readStatus;

    /**
     * Number of chapters already read by the user
     */
    @Getter
    @SerializedName("chapters_read")
    private int chaptersRead;

    /**
     * Number of volumes already read by the user.
     */
    @Getter
    @SerializedName("volumes_read")
    private int volumesRead;

    /**
     * The date the user started reading the title
     */
    @Getter
    @SerializedName("reading_start")
    private String readingStart;

    /**
     * The date the user finished reading the title
     */
    @Getter
    @SerializedName("reading_end")
    private String readingEnd;

    /**
     * The number of chapters downloaded by the user
     */
    @Getter
    @SerializedName("chap_downloaded")
    private int chapDownloaded;

    /**
     * Set if the user is rerereading the manga
     */
    private boolean rereading;

    /**
     * The number of times the user has re-read the title. (Does not include the first time.)
     */
    @Getter
    @SerializedName("reread_count")
    private int rereadCount;

    /**
     * How much value the user thinks there is in rereading the series.
     */
    @Getter
    @SerializedName("reread_value")
    private int rereadValue;

    public void setReadStatus(String readStatus) {
        if (this.readStatus == null || !this.readStatus.equals(readStatus)) {
            this.readStatus = readStatus;
            if (!fromCursor) {
                addDirtyField("readStatus");
                checkProgress();
            }
        }
    }

    public void setChaptersRead(int chaptersRead) {
        if (this.chaptersRead != chaptersRead) {
            this.chaptersRead = chaptersRead;
            if (!fromCursor) {
                addDirtyField("chaptersRead");
                checkProgress();
            }
        }
    }

    public void setVolumesRead(int volumesRead) {
        if (this.volumesRead != volumesRead) {
            this.volumesRead = volumesRead;
            if (!fromCursor) {
                addDirtyField("volumesRead");
                checkProgress();
            }
        }
    }

    public void setReadingStart(String readingStart) {
        if (!fromCursor)
            addDirtyField("readingStart");
        this.readingStart = readingStart;
    }

    public void setReadingEnd(String readingEnd) {
        if (!fromCursor)
            addDirtyField("readingEnd");
        this.readingEnd = readingEnd;
    }

    public void setChapDownloaded(int chapDownloaded) {
        if (!fromCursor)
            addDirtyField("chapDownloaded");
        this.chapDownloaded = chapDownloaded;
    }

    public void setRereading(boolean rereading) {
        if (!fromCursor)
            addDirtyField("rereading");
        this.rereading = rereading;
    }

    public void setRereadCount(int rereadCount) {
        if (!fromCursor)
            addDirtyField("rereadCount");
        this.rereadCount = rereadCount;
    }

    public void setRereadValue(int rereadValue) {
        if (!fromCursor)
            addDirtyField("rereadValue");
        this.rereadValue = rereadValue;
    }

    public void checkProgress() {
        boolean completed = false;
        boolean started = false;

        // Automatically set the max chapters and volumes on completed
        if (getReadStatus().equals(GenericRecord.STATUS_COMPLETED) && getChapters() > 0) {
            setChaptersRead(getChapters());
            if (getVolumes() > 0)
                setVolumesRead(getVolumes());
            completed = true;
        }

        // Automatically set the progress when the max chapters has reached
        if (getChaptersRead() == getChapters() && getChapters() > 0) {
            setReadStatus(GenericRecord.STATUS_COMPLETED);
            completed = true;
        }

        if (completed) {
            // Automatically set the progress when the record has been finished
            if (getRereading() || (getRereadCount() > 0)) {
                setRereadCount(getRereadCount() + 1);
                setRereading(false);
            }

            // Automatically set the end date on completed if it is empty
            if (getReadingEnd() == null || getReadingEnd().equals("")) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                setReadingEnd(year + "-" + month + "-" + day);
            }
        }

        // Automatically set the progress when the chapter 1 has been read
        if (getReadStatus().equals(GenericRecord.STATUS_PLANTOWATCH) && getChaptersRead() == 1) {
            setReadStatus(GenericRecord.STATUS_READING);
            started = true;
        }

        // Automatically set the progress when the volume 1 has been read
        if (getReadStatus().equals(GenericRecord.STATUS_PLANTOWATCH) && getVolumesRead() == 1 && getChaptersRead() == 0) {
            setReadStatus(GenericRecord.STATUS_READING);
            started = true;
        }

        // Automatically set the start date on start if it is empty
        if (getReadStatus().equals(GenericRecord.STATUS_READING) && started) {
            if (getReadingStart() == null || getReadingStart().equals("")) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                setReadingStart(year + "-" + month + "-" + day);
            }
        }
    }

    public int getReadStatusInt() {
        return getUserStatusInt(getReadStatus());
    }

    public void setReadStatus(int id) {
        setReadStatus(MALManager.listSortFromInt(id, MALApi.ListType.MANGA));
    }

    public int getTypeInt() {
        String[] types = {
                "Manga",
                "Novel",
                "One Shot",
                "Doujin",
                "Manwha",
                "Manhua",
                "OEL"
        };
        return Arrays.asList(types).indexOf(getType());
    }

    public int getStatusInt() {
        String[] status = {
                "finished",
                "publishing",
                "not yet published"
        };
        return Arrays.asList(status).indexOf(getStatus());
    }

    public int getProgress(boolean useSecondaryAmount) {
        return useSecondaryAmount ? getVolumesRead() : getChaptersRead();
    }

    public void setProgress(boolean useSecondaryAmount, int progress) {
        if (useSecondaryAmount)
            setVolumesRead(progress);
        else
            setChaptersRead(progress);
    }

    public boolean getRereading() {
        return rereading;
    }

    public void setRereading(int cv) {
        rereading = cv == 1;
    }

    public static Manga fromCursor(Cursor cursor) {
        List<String> columnNames = Arrays.asList(cursor.getColumnNames());
        Manga result = (Manga) GenericRecord.fromCursor(new Manga(), cursor, columnNames);

        result.setChapters(cursor.getInt(columnNames.indexOf("chapters")));
        result.setVolumes(cursor.getInt(columnNames.indexOf("volumes")));
        result.setReadStatus(cursor.getString(columnNames.indexOf("readStatus")));
        result.setChaptersRead(cursor.getInt(columnNames.indexOf("chaptersRead")));
        result.setVolumesRead(cursor.getInt(columnNames.indexOf("volumesRead")));
        result.setReadingStart(cursor.getString(columnNames.indexOf("readingStart")));
        result.setReadingEnd(cursor.getString(columnNames.indexOf("readingEnd")));
        result.setChapDownloaded(cursor.getInt(columnNames.indexOf("chapDownloaded")));
        result.setRereading(cursor.getInt(columnNames.indexOf("rereading")));
        result.setRereadCount(cursor.getInt(columnNames.indexOf("rereadCount")));
        result.setRereadValue(cursor.getInt(columnNames.indexOf("rereadValue")));
        return result;
    }
}