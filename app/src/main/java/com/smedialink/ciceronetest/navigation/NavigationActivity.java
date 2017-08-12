package com.smedialink.ciceronetest.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.smedialink.ciceronetest.CiceroneApplication;
import com.smedialink.ciceronetest.R;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.BackTo;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

import static android.R.attr.data;
import static android.R.attr.fragment;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NavigationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        presenter = new NavigationPresenter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigator.setActionBar(getSupportActionBar());
        navigator.setNavigationView(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        presenter.onNavigationItemClick(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CiceroneApplication.INSTANCE.getNavigatorHolder().setNavigator(navigator);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CiceroneApplication.INSTANCE.getNavigatorHolder().removeNavigator();
    }

    private NavigationDrawerNavigator navigator = new NavigationDrawerNavigator(getSupportFragmentManager(), R.id.container) {
        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch (screenKey) {
                case "TOP":
                    return new TopFragment();
                case "GALLERY":
                    return new GalleryFragment();
            }
            return null;
        }

        @Override
        protected void showSystemMessage(String message) {
            Toast.makeText(NavigationActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void exit() {
            finish();
        }
    };

    static abstract class NavigationDrawerNavigator implements Navigator {

        private ActionBar actionBar;
        private NavigationView navigationView;
        private final FragmentManager fragmentManager;
        private final int containerId;

        NavigationDrawerNavigator(FragmentManager fragmentManager, int containerId) {
            this.fragmentManager = fragmentManager;
            this.containerId = containerId;
            addBackStackListener();
        }

        private void addBackStackListener() {
            fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentResumed(FragmentManager fm, Fragment f) {
                    super.onFragmentResumed(fm, f);
                    if (f instanceof NavigationItem) {
                        actionBar.setTitle(((NavigationItem) f).getTitle());
                        navigationView.setCheckedItem(((NavigationItem) f).getPosition());
                    } else {
                        throw new RuntimeException("Fragment has to implement MavigationItem");
                    }
                }
            }, false);
        }

        @Override
        public void applyCommand(Command command) {
            if (command instanceof Forward) {
                Forward forward = (Forward) command;
                Fragment fragment = createFragment(forward.getScreenKey(), forward.getTransitionData());
                if (fragment == null) {
                    return;
                }

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction
                        .replace(containerId, fragment)
                        .addToBackStack(forward.getScreenKey())
                        .commit();
            } else if (command instanceof Back) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                } else {
                    exit();
                }
            } else if (command instanceof Replace) {
                Replace replace = (Replace) command;
                Fragment fragment = createFragment(replace.getScreenKey(), replace.getTransitionData());
                if (fragment == null) {
                    return;
                }
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction
                            .replace(containerId, fragment)
                            .addToBackStack(replace.getScreenKey())
                            .commit();
                } else {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction
                            .replace(containerId, fragment)
                            .commit();
                }
            } else if (command instanceof BackTo) {
                String key = ((BackTo) command).getScreenKey();

                if (key == null) {
                    backToRoot();
                } else {
                    boolean hasScreen = false;
                    for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                        if (key.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
                            fragmentManager.popBackStackImmediate(key, 0);
                            hasScreen = true;
                            break;
                        }
                    }
                    if (!hasScreen) {
                    }
                }
            } else if (command instanceof SystemMessage) {
                showSystemMessage(((SystemMessage) command).getMessage());
            }
        }

        abstract protected Fragment createFragment(String screenKey, Object data);

        abstract protected void showSystemMessage(String message);
        abstract protected void exit();

        private void backToRoot() {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        public void setActionBar(ActionBar actionBar) {
            this.actionBar = actionBar;
        }

        public void setNavigationView(NavigationView navigationView) {
            this.navigationView = navigationView;
        }
    };
}
