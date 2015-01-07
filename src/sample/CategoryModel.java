package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class CategoryModel {
    public String getTitle() {
        return "A Category";
    }

    public List<? extends EntryModel> getEntries() {
        final ArrayList<EntryModel> entries = new ArrayList<>();
        entries.add( new EntryModel() );
        entries.add( new EntryModel() );
        return entries;
    }
}
