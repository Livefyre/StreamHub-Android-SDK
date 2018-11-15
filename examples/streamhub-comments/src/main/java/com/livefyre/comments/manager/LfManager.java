package com.livefyre.comments.manager;

import com.squareup.otto.Bus;

/**
 * Created by Adobe Systems Incorporated on 16/06/16.
 */
public class LfManager {
    private static LfManager ourInstance = new LfManager();
    private Bus bus = new Bus();

    public static LfManager getInstance() {
        return ourInstance;
    }

    private LfManager() {
    }

    public Bus getBus() {
        return bus;
    }
}
