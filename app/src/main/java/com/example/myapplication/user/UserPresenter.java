package com.example.myapplication.user;

import android.util.Log;
import com.example.myapplication.user.list.User;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class UserPresenter {
    private UserRepository userRepository;
    private Observable<List<User>> observableUsersList;
    public Observer<List<User>> observerUsers;
    private List<User> userList;

    public UserPresenter() {
        userRepository = new UserRepository();
        this.observableUsersList = userRepository.getUsers();
        if (observableUsersList != null) {
            observerUsers = getUsers();
            observableUsersList.subscribe(observerUsers);
        }
    }

    public List<User> getUserList() {
        return userList;
    }

    public Observer<List<User>> getUsers() {

        return new Observer<List<User>>() {
            @Override
            public void onSubscribe(Disposable d) {
            Log.d("UserPresenter","disposable");
            }

            @Override
            public void onNext(List<User> users) {
                userList = users;
                Log.d("UserPresenter", "Порция получена " + userList.size());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d("UserPresenter", "Всё готово");
            }
        };
    }


}


