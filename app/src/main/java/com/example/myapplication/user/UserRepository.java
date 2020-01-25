package com.example.myapplication.user;

import android.util.Log;
import com.example.myapplication.user.list.User;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class UserRepository {
     private Observable<List<User>> observable;
    private List<User> users;

    public UserRepository() {
        this.observable = getUsers();
    }

    public Observable<List<User>> getUsers() {
        observable = Observable.create(emitter -> {
            Thread.sleep(2000);
            users = getTempUsers();
            Log.d("UserRepository",users.toString());
            emitter.onNext(users);
        });
        return observable;
    }

    private List<User> getTempUsers() {
        users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(i, "Vasya" + i, "", ""));
        }
        return users;
    }
}
