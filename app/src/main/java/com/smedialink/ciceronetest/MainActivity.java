package com.smedialink.ciceronetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.smedialink.ciceronetest.navigation.NavigationActivity;

import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;

public class MainActivity extends AppCompatActivity {

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter();
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

    private Navigator navigator = new Navigator() {
        @Override
        public void applyCommand(Command command) {
            if (command instanceof Forward) {
                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
            } else if (command instanceof Back) {
                finish();
            }
        }
    };

    public void onNextClick(View view) {
        mainPresenter.onNextClick();
    }

}
