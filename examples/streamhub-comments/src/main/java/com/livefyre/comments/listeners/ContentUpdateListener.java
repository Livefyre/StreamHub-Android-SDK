package com.livefyre.comments.listeners;

import java.util.HashSet;

/**
 * Created by kvanainc1 on 28/01/15.
 */
public interface ContentUpdateListener {
    void onDataUpdate(HashSet<String> authorsSet, HashSet<String> statesSet, HashSet<String> annotationsStates, HashSet<String> updatesSet);
    void loadImage(String imageURL);
}