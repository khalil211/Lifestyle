package tn.esprit.lifestyle.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ActivityWithEntries {
    @Embedded
    private Activity activity;
    @Relation(parentColumn = "id", entityColumn = "activityId")
    private List<ActivityEntry> entries;

    public ActivityWithEntries() {

    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<ActivityEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ActivityEntry> entries) {
        this.entries = entries;
    }
}
