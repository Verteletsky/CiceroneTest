package com.smedialink.ciceronetest.navigation;

import com.smedialink.ciceronetest.CiceroneApplication;
import com.smedialink.ciceronetest.R;

import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.result.ResultListener;

/**
 * Created by olegshelyakin on 24.07.17.
 */

public class NavigationPresenter {

    private final Router router;

    public NavigationPresenter() {
        this.router = CiceroneApplication.INSTANCE.getRouter();
    }

    public void onNavigationItemClick(Integer id) {
        if (id == R.id.nav_camera) {
            router.navigateTo("TOP");
        } else if (id == R.id.nav_gallery) {
            router.navigateTo("GALLERY");
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
    }

}
