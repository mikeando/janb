package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelanderson on 7/01/2015.
 */
public class FileListModel extends CategoryModel {
    private List<FileModel> entries = new ArrayList<>();

    FileListModel() {
        entries.add( new FileModel("Some File"));
        entries.add( new FileModel("Another File"));
    }

    @Override
    public String getTitle() {
        return "Files";
    }

    @Override
    public List<FileModel> getEntries() {
        return entries;
    }
}
