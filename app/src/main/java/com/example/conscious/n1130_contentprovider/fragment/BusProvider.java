package com.example.conscious.n1130_contentprovider.fragment;

import com.squareup.otto.Bus;

/**
 * Created by conscious on 2016-12-06.
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}