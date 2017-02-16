package livefyre.listeners;

import java.util.HashSet;

/**
 * Created by kvanainc1 on 28/01/15.
 */
public interface ContentUpdateListener {
    void onDataUpdate(HashSet<String> updatesSet);
}