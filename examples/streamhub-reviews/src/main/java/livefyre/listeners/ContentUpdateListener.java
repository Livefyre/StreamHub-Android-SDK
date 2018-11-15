package livefyre.listeners;

import java.util.HashSet;

/**
 * Created by Adobe Systems Incorporated on 28/01/15.
 */
public interface ContentUpdateListener {
    void onDataUpdate(HashSet<String> updatesSet);
}