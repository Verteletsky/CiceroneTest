package com.smedialink.ciceronetest;

import ru.terrakok.cicerone.Router;

/**
 * Created by olegshelyakin on 24.07.17.
 */

public class MainPresenter {

    private final Router router;

    public MainPresenter() {
        this.router = CiceroneApplication.INSTANCE.getRouter();
    }

    public void onNextClick() {
        router.navigateTo("");
    }

}
